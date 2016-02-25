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
    boolean UpdateHighscore = true; //Highscore update

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
    GridNode[][] DarkGrid = new GridNode[3][3];
    GridNode[][] WaterGrid = new GridNode[3][3];
    GridNode[][] FireGrid = new GridNode[3][3];
    GridNode[][] NatureGrid = new GridNode[3][3];

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

    //List containing All the Waves
    Vector<Vector<AI>> WaveList = new Vector<Vector<AI>>();

    //List containing All the Projectiles
    Vector<Projectiles> ProjectileList = new Vector<Projectiles>();

    //In Game Screens
    private InGameScreens Pause_screen = new InGameScreens(400,200,
            BitmapFactory.decodeResource(getResources(),R.drawable.pause_screen));


    //Test
    private Bitmap GridTest = BitmapFactory.decodeResource(getResources(),R.drawable.gridtest);

    //Images
    private Bitmap TileMap =  BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor_tileset);
    private Bitmap TD_Grid_Frame = BitmapFactory.decodeResource(getResources(), R.drawable.td_grid_frame);
    private Bitmap WorkerImage = BitmapFactory.decodeResource(getResources(), R.drawable.worker);

    //Towers
    private Bitmap NormalTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal);
    private Bitmap NormalTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal_drag);
    private Bitmap FastTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast);
    private Bitmap FastTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast_drag);

    //Ais
    private Bitmap NormalAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_round);
    private Bitmap FastAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_spirit);
    private Bitmap SlowAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_head);

    //Projectiles
    private Bitmap BubbleProjectileImage = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_bullet);

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

        //Initialize Element Grids
        midPoints.Set(204,780);
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                //Check if its a path
                DarkGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 70.0f, 70.0f),
                        GridNode.GRID_TYPE.GT_FREE);

                midPoints.x += 70.0f;
            }
            midPoints.x = 204.0f;
            midPoints.y += 70.0f;
        }
        midPoints.Set(204,1038);
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                //Check if its a path
                FireGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 70.0f, 70.0f),
                        GridNode.GRID_TYPE.GT_FREE);

                midPoints.x += 70.0f;
            }
            midPoints.x = 204.0f;
            midPoints.y += 70.0f;
        }
        midPoints.Set(458,780);
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                //Check if its a path
                NatureGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 70.0f, 70.0f),
                        GridNode.GRID_TYPE.GT_FREE);

                midPoints.x += 70.0f;
            }
            midPoints.x = 458.0f;
            midPoints.y += 70.0f;
        }
        midPoints.Set(458,1038);
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                //Check if its a path
                WaterGrid[i][j] = new GridNode(new AABB2D(new Vector2(midPoints.x, midPoints.y), 70.0f, 70.0f),
                        GridNode.GRID_TYPE.GT_FREE);

                midPoints.x += 70.0f;
            }
            midPoints.x = 458.0f;
            midPoints.y += 70.0f;
        }

        //Reading Waypoints
        scanner = new Scanner(getResources().openRawResource(R.raw.waypointlevel1));
        while(scanner.hasNext())
        {
            String temp = scanner.next();
            String[] values = temp.split(",");

            Vector2 point = new Vector2(Float.parseFloat(values[0]), Float.parseFloat(values[1]));

            Waypoints.addElement(point);
        }
        scanner.close();

        //Reading Waves
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
                     Vector2 position = new Vector2(0,100);
                     WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, NormalAIImage, AI.AI_TYPE.AI_NORMAL));
                 }
            }

            else if(parts[0].equals("Fast"))
            {
                int loopNum = Integer.parseInt(parts[1]);

                for(int i = 0; i < loopNum; ++i)
                {
                    Vector2 position = new Vector2(0,100);
                    WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, FastAIImage, AI.AI_TYPE.AI_FAST));
                }
            }

            else if(parts[0].equals("Tank"))
            {
                int loopNum = Integer.parseInt(parts[1]);

                for(int i = 0; i < loopNum; ++i)
                {
                    Vector2 position = new Vector2(0,100);
                    WaveList.get(waveIndex - 1).addElement(new AI(position,Waypoints, SlowAIImage, AI.AI_TYPE.AI_SLOWBUTTANKY));
                }
            }
        }
        scanner.close();

        //InGameButton List
        ButtonList.addElement(new InGameButton(48, 667,
                NormalTowerImage, false, InGameButton.BUTTON_TYPE.UI_NORMAL_TOWER));
        ButtonList.addElement(new InGameButton(250, 667,
                FastTowerImage, false, InGameButton.BUTTON_TYPE.UI_FAST_TOWER));
        ButtonList.addElement(new InGameButton(37, 810,
                WorkerImage, false, InGameButton.BUTTON_TYPE.UI_WORKER));

        //AIList.addElement(new AI(position,Waypoints, NormalAIImage, AI.AI_TYPE.AI_NORMAL));

        //Initialize projectiles to be reused
        for(int i = 0; i < 50; ++i)
        {
            ProjectileList.addElement(new Projectiles());
        }

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

        //Rendering Tower Grid
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
        //Render Elemental Grids
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                canvas.drawBitmap(GridTest,
                        DarkGrid[i][j].getBoundingBox().getTopLeft().x,
                        DarkGrid[i][j].getBoundingBox().getTopLeft().y,
                        null);
            }
        }
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                canvas.drawBitmap(GridTest,
                        NatureGrid[i][j].getBoundingBox().getTopLeft().x,
                        NatureGrid[i][j].getBoundingBox().getTopLeft().y,
                        null);
            }
        }
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                canvas.drawBitmap(GridTest,
                        WaterGrid[i][j].getBoundingBox().getTopLeft().x,
                        WaterGrid[i][j].getBoundingBox().getTopLeft().y,
                        null);
            }
        }
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                canvas.drawBitmap(GridTest,
                        FireGrid[i][j].getBoundingBox().getTopLeft().x,
                        FireGrid[i][j].getBoundingBox().getTopLeft().y,
                        null);
            }
        }

        //Render These while game is active
        if(GameActive)
        {
            //Rendering Towers
            for(int i = 0; i < TowerList.size(); ++i)
            {
                TowerList.elementAt(i).Draw(canvas);
            }

            //Rendering
            for(int i = 0; i < ProjectileList.size(); ++i)
            {
                if(ProjectileList.get(i).isActive()) {
                    ProjectileList.elementAt(i).Draw(canvas);
                }
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
        if(!GameActive){
           // canvas.drawBitmap(Restart_button.getImage(),Restart_button.getPosX(),Restart_button.getPosY(),null);
            //canvas.drawBitmap(Mainmenu_button.getImage(), Mainmenu_button.getPosX(), Mainmenu_button.getPosY(),null);
        }

        //FPS
        canvas.drawText("FPS:" + FPS, 50, 50, paint);
        canvas.drawText("touchPos X:" + FirstTouch.x, 50, 75, paint);
        canvas.drawText("touchPos Y:" + FirstTouch.y, 50, 100, paint);
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

                        //Spawning enemies
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

                        //Updating enemies
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

                        //If wave has started
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

                        /*
                        *TOWER RELATED UPDATES
                        */

                        //Only if there is a tower on the grid we start updating towers
                        if(TowerList.size() > 0)
                        {
                            //Iterate through all the towers that are on the grid
                            for(int i = 0; i < TowerList.size(); ++i)
                            {
                                //Only if tower is able to fire
                                if(TowerList.get(i).Fire(dt))
                                {
                                    if (currentWave < WaveList.size())
                                    {
                                        //Update Enemies
                                        for (int j = 0; j < WaveList.get(currentWave).size(); ++j)
                                        {
                                        //Only if the enemy is active
                                            if (WaveList.get(currentWave).get(j).isActive()) {
                                                float distance = WaveList.get(currentWave).get(j).getPosition().operatorMinus(TowerList.get(i).getPosition()).Length();
                                                //Only if its within range, we update the tower
                                                if (distance < TowerList.get(i).getRange()) {
                                                    //Check which type of tower it is
                                                    //To assign different type of variables based on its type to the projectile
                                                    switch (TowerList.get(i).getType()) {
                                                        case TOWER_NORMAL:
                                                            FetchProjectiles(TowerList.get(i).getPosition(),
                                                                    BubbleProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300);
                                                            break;
                                                        case TOWER_HIGHFIRERATE:
                                                            FetchProjectiles(TowerList.get(i).getPosition(),
                                                                    BubbleProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300);
                                                            break;
                                                        case TOWER_SLOW:
                                                            break;
                                                    }
                                                    break;

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        //Update Projectiles
                        for(int i = 0; i < ProjectileList.size(); ++i)
                        {
                            if(ProjectileList.get(i).isActive()) {
                                ProjectileList.get(i).Update(dt);
                            }
                        }

                        //Check Collision
                        for(int i = 0; i < ProjectileList.size(); ++i)
                        {
                            //Only if the projectile is active, check it against all other active enemies
                            if(ProjectileList.get(i).isActive()) {
                                if (currentWave < WaveList.size())
                                {
                                    for (int j = 0; j < WaveList.get(currentWave).size(); ++j)
                                    {
                                        //Only if the enemy is active
                                        if (WaveList.get(currentWave).get(j).isActive())
                                        {
                                            //If they intersect with each other
                                            if (ProjectileList.get(i).getBounding_box().CheckIntersect(WaveList.get(currentWave).get(j).getBoundingbox())) {
                                                //Remove Projectile
                                                ProjectileList.get(i).setActive(false);
                                                //Update Health
                                                WaveList.get(currentWave).get(j).setHealth(WaveList.get(currentWave).get(j).getHealth() - ProjectileList.get(i).getDamage());

                                                //Remove Enemy
                                                if (WaveList.get(currentWave).get(j).getHealth() < 0) {
                                                    WaveList.get(currentWave).get(j).setActive(false);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    //Feedback for game over
                    else if (!GameActive) {
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
                                    case UI_WORKER:
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

    public void FetchProjectiles(Vector2 startPosition, Bitmap image,Vector2 TargetPos,float damage, float speed)
    {
        boolean Full = true;
        //Iterate through current list
        for(int i = 0; i < ProjectileList.size(); ++i)
        {
            //Get the first non active projectile to reuse
            if(ProjectileList.elementAt(i).isActive() == false)
            {
                //Set all data of the projectile
                ProjectileList.elementAt(i).SetAllData(new Vector2(startPosition.x,startPosition.y), image, speed, damage, TargetPos, true);
                Full = false;
                //Stop the loop
                break;
            }
        }
        if(Full) {
            //Add more into the list
            for (int i = 0; i < 20; ++i) {
                ProjectileList.addElement(new Projectiles());
            }
            //Use the last/ newly added projectile
            ProjectileList.lastElement().SetAllData(new Vector2(startPosition.x, startPosition.y), image, speed, damage, TargetPos, true);
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
