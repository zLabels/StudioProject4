package com.spook.defenseofelements;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Vibrator;
import android.text.method.Touch;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.media.MediaPlayer;
import android.widget.Button;

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
    private short bgX = 0, bgY = -20;  //Variables for defining background start and end point

    // Variables for FPS
    public float FPS = 0.f;
    Paint paint = new Paint(); //Used for text rendering

    //Feedback
    SoundManager soundManager;
    public Vibrator v;
    float vibrateTime = 0.f;
    float MaxVibrateTime = 0.5f;

    //Random
    Random r = new Random();

    private short GameState;    // Variable for Game State check

    // Variables for swiping
    Vector2 InitialPos = new Vector2(0,0);
    Vector2 LastPos = new Vector2(0,0);
    Vector2 DirectionVector = new Vector2(0,0);
    boolean Tapped = false;

    //Dragging Variables
    boolean ActionDown = false;
    boolean ActionUp = false;
    Vector2 FirstTouch = new Vector2(0.0f,0.0f);
    Vector2 TouchPos = new Vector2(0.0f,0.0f);

    //Game elements
    float SpawnRate = 0.5f; //Rate for each obstacle to spawn
    float SpawnTimer = 0.f; //track time to spawn
    short ScrollSpeed = 500;    //Speed of background scrolling
    short BarSpeed = 35;    //Speed of bar scrolling
    float timer = 0.f;  //Timer to increase speed
    int score = 0;  //Play score

    //Sp4 Game elements
    int currentWave = 0;
    int currentSpawnIndex = 0;
    float aiSpawnrate = 1.f;
    float spawnTimer = 0.f;
    boolean waveStarted = false;

    Tower selectedTower;    //Currently selected Tower

    Vector<Vector2> Waypoints = new Vector<Vector2>();

    //Grids
    GridNode[][] TowerGrid = new GridNode[9][12];
    boolean UpdateHighscore = true; //Highscore update

    int[][] CSVInfo = new int[9][12];

    AppPrefs appPrefs;  //Shared prefs

    private boolean GameActive = true;  //Status of game
    private boolean GamePaused = false; //Paused status of game

    //In game buttons
    /*private InGameButton Restart_button = new InGameButton(500,650,
            BitmapFactory.decodeResource(getResources(),R.drawable.restart_ingamebutton),false,"Restart");
    private InGameButton Mainmenu_button = new InGameButton(1150,650,
            BitmapFactory.decodeResource(getResources(),R.drawable.mainmenu_ingamebutton),false,"MainMenu");
    private InGameButton Pause_button = new InGameButton(1700,30,
            BitmapFactory.decodeResource(getResources(),R.drawable.pauseicon),false,"Pause");*/

    //List containing All In Game Buttons
    Vector<InGameButton> ButtonList = new Vector<InGameButton>();

    //List containing All Towers
    Vector<Tower> TowerList = new Vector<Tower>();

    Vector<Vector<AI>> WaveList = new Vector<Vector<AI>>();

    private InGameScreens Pause_screen = new InGameScreens(400,200,
            BitmapFactory.decodeResource(getResources(),R.drawable.pause_screen));

    private InGameScreens GridTest = new InGameScreens(0,0,
            BitmapFactory.decodeResource(getResources(),R.drawable.gridtest));

    //Images
    private Bitmap TileMap =  BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor_tileset);
    private Bitmap TD_Grid_Frame = BitmapFactory.decodeResource(getResources(), R.drawable.td_grid_frame);
    private Bitmap T_selection_bar = BitmapFactory.decodeResource(getResources(), R.drawable.tower_select_bar);

    //Towers
    private Bitmap NormalTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal);
    private Bitmap NormalTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal_drag);
    private Bitmap FastTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast);
    private Bitmap FastTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast_drag);

    //Ais
    private Bitmap NormalAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_round);
    private Bitmap FastAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_spirit);
    private Bitmap SlowAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_head);

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

        //Reading Values from CSV files
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.level1_td_grid));
        for (int i = 0; i < 9; ++i) {
            String temp = scanner.next();
            String[] parts = temp.split(",");
            for (int j = 0; j < 12; ++j) {
                CSVInfo[i][j] = Integer.parseInt(parts[j]);
            }
        }
        scanner.close();

        //Initializing Tower Grids
        Vector2 midPoints = new Vector2(48.0f,42.0f);
        for(int i = 0; i < 9; ++i)
        {
            for (int j = 0; j < 12; ++j)
            {
                //Check if its a path
                if(CSVInfo[i][j] > 0) {
                    TowerGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 64.0f, 64.0f),
                            TileMap, 7, CSVInfo[i][j], GridNode.GRID_TYPE.GT_PATH);
                }
                else
                {
                    TowerGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 64.0f, 64.0f),
                            TileMap, 7, CSVInfo[i][j], GridNode.GRID_TYPE.GT_FREE);
                }
                midPoints.x += 64.0f;
            }
            midPoints.x = 48.0f;
            midPoints.y += 64.0f;
        }

        scanner = new Scanner(getResources().openRawResource(R.raw.waypointlevel1));
        while(scanner.hasNext())
        {
            String temp = scanner.next();
            String[] values = temp.split(",");

            Vector2 point = new Vector2(Float.parseFloat(values[0]), Float.parseFloat(values[1]));

            Waypoints.addElement(point);
        }
        scanner.close();

        int waveIndex = 0;

        scanner = new Scanner(getResources().openRawResource(R.raw.wavelevel1));
        while(scanner.hasNext())
        {
            String temp = scanner.next();
            String[] parts = temp.split(",");

            if(parts[0].equals("Wave")) {
                waveIndex = Integer.parseInt(parts[1]);

                if (waveIndex > WaveList.size()) {
                    WaveList.addElement(new Vector<AI>());
                }
            }

           /* String spawnlist = scanner.next();
            String[] spawnpart = spawnlist.split(",");*/

            else if(parts[0].equals("Normal"))
            {
                int loopNum = Integer.parseInt(parts[1]);

                 for(int i = 0; i < loopNum; ++i)
                 {
                     Vector2 position = new Vector2(90,0);
                     WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, NormalAIImage, AI.AI_TYPE.AI_NORMAL));
                 }
            }

            else if(parts[0].equals("Fast"))
            {
                int loopNum = Integer.parseInt(parts[1]);

                for(int i = 0; i < loopNum; ++i)
                {
                    Vector2 position = new Vector2(90,0);
                    WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, FastAIImage, AI.AI_TYPE.AI_FAST));
                }
            }

            else if(parts[0].equals("Tank"))
            {
                int loopNum = Integer.parseInt(parts[1]);

                for(int i = 0; i < loopNum; ++i)
                {
                    Vector2 position = new Vector2(90,0);
                    WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, SlowAIImage, AI.AI_TYPE.AI_SLOWBUTTANKY));
                }
            }
        }
        scanner.close();

        //InGameButton List
        ButtonList.addElement(new InGameButton(48, 667,
                NormalTowerImage, false, InGameButton.BUTTON_TYPE.UI_NORMAL_TOWER));

        ButtonList.addElement(new InGameButton(120, 667,
                FastTowerImage, false, InGameButton.BUTTON_TYPE.UI_FAST_TOWER));

        //AIList.addElement(new AI(position,Waypoints, NormalAIImage, AI.AI_TYPE.AI_NORMAL));


        //Text rendering values
        paint.setARGB(255, 0, 0, 0);
        paint.setStrokeWidth(100);
        paint.setTextSize(30);

        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);

        //Set Current Selected Tower to null
        selectedTower = null;

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

        canvas.drawBitmap(bg, bgX, bgY, null);
        //canvas.drawBitmap(scaledbg, bgX + ScreenWidth, bgY, null);

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
        
        if(GameActive)
        {

            //Rendering Towers
            for(int i = 0; i < TowerList.size(); ++i)
            {
                TowerList.elementAt(i).Draw(canvas);
            }

            //Rendering Buttons
            for(int i = 0; i < ButtonList.size(); ++i)
            {
                canvas.drawBitmap(ButtonList.elementAt(i).getImage(),
                        ButtonList.elementAt(i).getBoundingBox().getTopLeft().x,
                        ButtonList.elementAt(i).getBoundingBox().getTopLeft().y,
                        null);
            }

            if(currentWave < WaveList.size()) {
                for (int i = 0; i < WaveList.get(currentWave).size(); ++i) {
                    if (WaveList.get(currentWave).get(i).isActive()) {
                        WaveList.get(currentWave).get(i).Draw(canvas);
                    }
                }
            }
            //Grid Frame
            canvas.drawBitmap(TD_Grid_Frame, 0, -20, null);

            //Drag Tower
            if(ActionDown)
            {
                if(selectedTower != null) {
                    switch (selectedTower.getType()) {
                        case TOWER_NORMAL: {
                            canvas.drawBitmap(NormalTowerImageDrag, TouchPos.x, TouchPos.y, null);
                        }
                        break;
                        case TOWER_HIGHFIRERATE:{
                            canvas.drawBitmap(FastTowerImageDrag, TouchPos.x, TouchPos.y, null);
                        }
                        break;
                        case TOWER_SLOW:{

                        }
                        break;
                    }
                }
            }

        }

        //Game is paused
        if (GamePaused) {
            //Paused Game
            canvas.drawBitmap(Pause_screen.getImage(), Pause_screen.getPosX(), Pause_screen.getPosY(), null);
        }

        //Game is lost
        if(GameActive == false){
           // canvas.drawBitmap(Restart_button.getImage(),Restart_button.getPosX(),Restart_button.getPosY(),null);
            //canvas.drawBitmap(Mainmenu_button.getImage(), Mainmenu_button.getPosX(), Mainmenu_button.getPosY(),null);
        }

        //FPS
        canvas.drawText("FPS:" + FPS, 50, 50, paint);
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

                        if (currentWave < WaveList.size()) {
                            spawnTimer += dt;

                            if (spawnTimer >= aiSpawnrate)
                            {
                                if (currentSpawnIndex < WaveList.get(currentWave).size())
                                {
                                    spawnTimer = 0;
                                    WaveList.get(currentWave).get(currentSpawnIndex).setActive(true);
                                    waveStarted = true;
                                    ++currentSpawnIndex;
                                }
                            }
                        }

                        if (currentWave < WaveList.size())
                        {
                            for (int i = 0; i < WaveList.get(currentWave).size(); ++i)
                            {
                                if (WaveList.get(currentWave).get(i).isActive())
                                {
                                    WaveList.get(currentWave).get(i).Update(dt);
                                }
                            }
                        }

                        if(waveStarted)
                        {
                            boolean wavecleared = false;

                            for (int i = 0; i < WaveList.get(currentWave).size(); ++i)
                            {
                                if (WaveList.get(currentWave).get(i).isActive())
                                {
                                    wavecleared = false;
                                    break;
                                }
                                else
                                {
                                    wavecleared = true;
                                }
                            }

                            if (currentWave < WaveList.size() && wavecleared) {
                                waveStarted = false;
                                currentSpawnIndex = 0;
                                ++currentWave;
                            }
                        }
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
        //To process if game is active and NOT paused
        if(GameActive && !GamePaused)
        {
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN: {
                    //If its the first time tapping down
                    if(!ActionDown) {
                        FirstTouch.x = event.getX();
                        FirstTouch.y = event.getY();
                        //Loop to iterate through all buttons
                        for (int i = 0; i < ButtonList.size(); ++i) {
                            if (ButtonList.elementAt(i).getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY()))) {
                                switch (ButtonList.elementAt(i).buttonID) {
                                    case UI_NORMAL_TOWER:
                                        //Check if there is a selected tower
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_NORMAL) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL);
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL);
                                        }
                                        break;
                                    case UI_FAST_TOWER:
                                        //Check if there is a selected tower
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_HIGHFIRERATE) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE);
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE);
                                        }
                                        break;
                                }
                            }
                        }
                        ActionDown = true;
                        ActionUp = false;
                    }
                }
                break;

                case MotionEvent.ACTION_UP:
                {
                    //Only if a Tower is selected to be built
                    if (selectedTower != null) {
                        Boolean GridSelected = false;
                        //Loop to iterate through the grids
                        for (int i = 0; i < 9; ++i) {
                            for (int j = 0; j < 12; ++j) {
                                //Grid needs to be free first
                                if (TowerGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                    //if Tap on this grid
                                    if (TowerGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY()))) {
                                        TowerList.addElement(new Tower(TowerGrid[i][j].getBoundingBox().getCenterPoint(),
                                                selectedTower.getImage(), selectedTower.getType()));

                                        //Set Grid to Occupied
                                        TowerGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);

                                        GridSelected = true;
                                        break;
                                    }
                                }
                            }
                            //If a grid has been selected
                            if (GridSelected) {
                                //Stop loop
                                break;
                            }
                        }
                    }
                    ActionDown = false;
                    ActionUp = true;
                    selectedTower = null;
                }
                break;
            }

            if(ActionDown)
            {
                TouchPos.x = event.getX() - 32.0f;
                TouchPos.y = event.getY() - 32.0f;
            }

            return true;
        }
        //To process if game is active BUT paused
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
        //Restart_button.setActive(false);
        //Mainmenu_button.setActive(false);
        Tapped = false;
        vibrateTime = 0.f;
        UpdateHighscore = true;
        //Reset everything first before we set game active back to true
        GameActive = true;
    }
}
