package com.spook.defenseofelements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

public class GamePanelSurfaceView extends SurfaceView implements SurfaceHolder.Callback {
    // Implement this interface to receive information about changes to the surface.
    private GameThread myThread = null; // Thread to control the rendering

    private Bitmap bg, scaledbg;    //Used for background
    int ScreenWidth, ScreenHeight;  //Define Screen width and Screen height
    private short bgX = 0, bgY = 0;  //Variables for defining background start and end point

    // Variables for FPS
    public float FPS = 0.f;
    Paint paint = new Paint(); //Used for text rendering

    //Feedback
    SoundManager soundManager;
    public Vibrator v;
    float vibrateTime = 0.f;
    float MaxVibrateTime = 0.5f;

    //Stick man animation
    private SpriteAnimation stickman_anim;

    //Random
    Random r = new Random();

    private short GameState;    // Variable for Game State check

    // Variables for swiping
    Vector2 InitialPos = new Vector2(0,0);
    Vector2 LastPos = new Vector2(0,0);
    Vector2 DirectionVector = new Vector2(0,0);
    boolean Tapped = false;
    boolean FingerDown = false;

    //Game elements
    float SpawnRate = 0.5f; //Rate for each obstacle to spawn
    float SpawnTimer = 0.f; //track time to spawn
    short ScrollSpeed = 500;    //Speed of background scrolling
    short BarSpeed = 35;    //Speed of bar scrolling
    float timer = 0.f;  //Timer to increase speed
    int score = 0;  //Play score

    //Grids
    GridNode[][] TowerGrid = new GridNode[9][12];
    boolean UpdateHighscore = true; //Highscore update

    int [][] CSVInfo = new int[9][12];

    AppPrefs appPrefs;  //Shared prefs

    private boolean GameActive = true;  //Status of game
    private boolean GamePaused = false; //Paused status of game

    //In game buttons
    private InGameButton Restart_button = new InGameButton(500,650,
            BitmapFactory.decodeResource(getResources(),R.drawable.restart_ingamebutton),false);
    private InGameButton Mainmenu_button = new InGameButton(1150,650,
            BitmapFactory.decodeResource(getResources(),R.drawable.mainmenu_ingamebutton),false);
    private InGameButton Pause_button = new InGameButton(1700,30,
            BitmapFactory.decodeResource(getResources(),R.drawable.pauseicon),false);

    private InGameScreens Pause_screen = new InGameScreens(400,200,
            BitmapFactory.decodeResource(getResources(),R.drawable.pause_screen));

    private InGameScreens GridTest = new InGameScreens(0,0,
            BitmapFactory.decodeResource(getResources(),R.drawable.gridtest));

    //Images
    private Bitmap TileMap =  BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor_tileset);
    private Bitmap TD_Grid_Frame = BitmapFactory.decodeResource(getResources(), R.drawable.td_grid_frame);
    private Bitmap T_selection_bar = BitmapFactory.decodeResource(getResources(), R.drawable.tower_select_bar);

    //constructor for this GamePanelSurfaceView class
    public GamePanelSurfaceView(Context context,int Mode){

        // Context is the current state of the application/object
        super(context);
        // Adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);
        //Set information to get screen size
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        ScreenWidth = metrics.widthPixels;
        ScreenHeight = metrics.heightPixels;

        //Loading images when created
        bg = BitmapFactory.decodeResource(getResources(), R.drawable.game_background);
        scaledbg = Bitmap.createScaledBitmap(bg, ScreenWidth, ScreenHeight, true);
        //Media Players
        soundManager = new SoundManager();

        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.level1_td_grid));
        for (int i = 0; i < 9; ++i) {
            String temp = scanner.next();
            String[] parts = temp.split(",");
            for (int j = 0; j < 12; ++j) {
                CSVInfo[i][j] = Integer.parseInt(parts[j]);
            }
        }
        scanner.close();

        Vector2 midPoints = new Vector2(48.0f,0.0f);
        for(int i = 0; i < 9; ++i)
        {
            for (int j = 0; j < 12; ++j)
            {
                TowerGrid[i][j] = new GridNode(new AABB2D(midPoints,64.0f,64.0f), TileMap, 10, CSVInfo[i][j], GridNode.GRID_TYPE.GT_FREE);
                midPoints.x += 64.0f;
            }
            midPoints.x = 48.0f;
            midPoints.y += 64.0f;
        }

        //Text rendering values
        paint.setARGB(255, 0, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(30);

        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        //Shared prefs
        appPrefs = new AppPrefs(context);
    }

    //must implement inherited abstract methods
    public void surfaceCreated(SurfaceHolder holder) {
        // Create the thread
        if (!myThread.isAlive()) {
            myThread = new GameThread(getHolder(), this);
            myThread.startRun(true);
            myThread.start();
        }
    }
    public void surfaceDestroyed(SurfaceHolder holder) {
        // Destroy the thread
        if (myThread.isAlive()) {
            myThread.startRun(false);
        }
        boolean retry = true;
        while (retry) {
            try {
                myThread.join();
                retry = false;
            } catch (InterruptedException e) {
            }
        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void RenderGameplay(Canvas canvas) {
        // 2) Re-draw 2nd image after the 1st image ends
        if (canvas == null) {
            return;
        }
        canvas.drawBitmap(scaledbg, bgX, bgY, null);
        canvas.drawBitmap(scaledbg, bgX + ScreenWidth, bgY, null);

        //FPS
        canvas.drawText("FPS:" + FPS, 50, 800, paint);
        
        if(GameActive)
        {
            //Rendering Grids
            for(int i = 0; i < 9; ++i)
            {
                for (int j = 0; j < 12; ++j)
                {
                    canvas.drawBitmap(TileMap,
                            TowerGrid[i][j].getSourceRect(),
                            TowerGrid[i][j].getDestRect(),
                            null);
                }
            }

            //Grid Frame
            canvas.drawBitmap(TD_Grid_Frame, 0, 0, null);
            canvas.drawBitmap(T_selection_bar,0 ,650, null);

        }

        //Game is paused
        if (GamePaused) {
            //Paused Game
            canvas.drawBitmap(Pause_screen.getImage(), Pause_screen.getPosX(), Pause_screen.getPosY(), null);
        }

        //Game is lost
        if(GameActive == false){
            canvas.drawBitmap(Restart_button.getImage(),Restart_button.getPosX(),Restart_button.getPosY(),null);
            canvas.drawBitmap(Mainmenu_button.getImage(), Mainmenu_button.getPosX(), Mainmenu_button.getPosY(),null);
        }
    }

    //Update method to update the game play
    public void update(float dt, float fps){
        FPS = fps;

        switch (GameState) {
            case 0: {
                //Only if game is not paused
                if (!GamePaused)
                {
                    //Only when game is active we update the following
                    if (GameActive) {

                        //stickman_anim.update(System.currentTimeMillis());

                    }
                    //Feedback for game over
                    if (GameActive == false) {

                        //Vibration feedback
                        //vibrateTime += dt;
                        //if (vibrateTime > MaxVibrateTime) {
                        //    stopVibrate();
                        //} else {
                        //    startVibrate();
                        //}

                    }
                }
            }
            break;
        }
    }

    // Rendering is done on Canvas
    public void doDraw(Canvas canvas){
        switch (GameState)
        {
            case 0:
                RenderGameplay(canvas);
                break;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        //Only process if game is active
        if(GameActive && !GamePaused)
        {
            return true;
        }

        else if(GameActive && GamePaused)
        {
            return true;
        }
        //To process other taps while game is not active
        else
        {
            return true;
        }
    }

    //Proccess user's input
    public int ProcessSwipe(Vector2 SwipeDirection) {
        float x = SwipeDirection.x;
        float y = SwipeDirection.y;

        // x more than 0
        if (0 < x) {
            // y more than 0
            if (0 < y) {
                // Since x & y positive check which bigger
                // x more than y hence direction right
                if (x > y) {
                    return 2;
                }
                // y more than x hence direction down
                else {
                    return 4;
                }
            }
            // y less than 0
            else
            {
                // Check x or y(converted to positive) which is bigger
                // x bigger than y when positive hence direction right
                if(x > (-1 * y))
                {
                    return 2;
                }
                // y when positive is bigger than x hence direction up
                else
                {
                    return 3;
                }
            }
        }
        // x less than 0
        else {
            // y more than 0
            if (0 < y) {
                // Since x & y positive check which bigger
                // x when positive more than y hence direction left
                if ((-1 * x) > y) {
                    return 1;
                }
                // y more than x when positive hence direction down
                else {
                    return 4;
                }
            }
            // y less than 0
            else {
                // Check x or y(converted to positive) which is bigger
                // x when positive bigger than y when positive hence direction left
                if ((-1 * x) > (-1 * y)) {
                    return 1;
                }
                // y when positive is bigger than x hence direction up
                else {
                    return 3;
                }
            }
        }
    }

    //Vibration
    public void startVibrate(){
        long pattern[] = {0,500,500};
        v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern,0);
    }
    public void stopVibrate(){
        v.cancel();
    }

    //Restart game variables
    public void Reset()
    {
        SpawnRate = 0.5f;
        SpawnTimer = 0.f;
        ScrollSpeed = 500;
        timer = 0.f;
        score = 0;
        Restart_button.setActive(false);
        Mainmenu_button.setActive(false);
        Tapped = false;
        FingerDown = false;
        vibrateTime = 0.f;
        UpdateHighscore = true;
        //Reset everything first before we set game active back to true
        GameActive = true;
    }
}
