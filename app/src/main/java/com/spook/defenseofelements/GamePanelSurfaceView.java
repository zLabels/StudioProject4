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
    boolean Win = false;

    //Sp4 Game elements
    int currentWave = 0;
    int currentSpawnIndex = 0;
    float aiSpawnrate = 5.f;
    float spawnTimer = 0.f;
    boolean waveStarted = false;
    boolean selectedWorker = false; //For use on UI Button Worker
    InGameButton selectedPlacedWorker;   //For use on worker that is already on grid
    float elementSpawnrate = 2.f;
    float elementTimer = 0.f;

    Player player;

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

    //List containing All the workers that are on the grid
    Vector<InGameButton> WorkerList = new Vector<InGameButton>();

    //In Game Screens
    private InGameScreens Pause_screen = new InGameScreens(400,200,
            BitmapFactory.decodeResource(getResources(),R.drawable.pause_screen));

    //Test
    private Bitmap GridTest = BitmapFactory.decodeResource(getResources(),R.drawable.gridtest);

    //Images
    private Bitmap TileMap =  BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor_tileset);
    private Bitmap TD_Grid_Frame = BitmapFactory.decodeResource(getResources(), R.drawable.td_grid_frame);
    private Bitmap WorkerImage = BitmapFactory.decodeResource(getResources(), R.drawable.worker);
    private Bitmap WorkerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.worker_drag);

    //Towers
    private Bitmap NormalTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal);
    private Bitmap NormalTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal_drag);
    private Bitmap FastTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast);
    private Bitmap FastTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast_drag);
    private Bitmap SlowTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_slow);
    private Bitmap SlowTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_slow_drag);
    private Bitmap OpTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_op);
    private Bitmap OpTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_op_drag);

    //Used for rendering tower resources
    Tower UITowerNormal = new Tower(new Vector2(0,0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL);
    Tower UITowerHighfirerate = new Tower(new Vector2(0,0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE);
    Tower UITowerSlow = new Tower(new Vector2(0,0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW);
    Tower UITowerOP = new Tower(new Vector2(0,0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP);

    //Ais
    private Bitmap NormalAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_round);
    private Bitmap FastAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_spirit);
    private Bitmap SlowAIImage = BitmapFactory.decodeResource(getResources(), R.drawable.ghost_head);

    //Projectiles
    private Bitmap BubbleProjectileImage = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_bullet);
    private Bitmap Bubble2ProjectileImage = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_bullet_2);
    private Bitmap Bubble3ProjectileImage = BitmapFactory.decodeResource(getResources(), R.drawable.bubble_bullet_3);

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

        player = new Player();

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

        InitializeGrid();

        InitializeWaypoint();

        InitializeWave();

        InitializeButtons();
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

    void InitializeGrid()
    {
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
        midPoints.Set(204, 780);
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
        midPoints.Set(204, 1038);
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
        midPoints.Set(458, 780);
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
        midPoints.Set(458, 1038);
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
    }

    void InitializeButtons()
    {
        //InGameButton List
        ButtonList.addElement(new InGameButton(48, 667,
                NormalTowerImage, false, InGameButton.BUTTON_TYPE.UI_NORMAL_TOWER));
        ButtonList.addElement(new InGameButton(250, 667,
                FastTowerImage, false, InGameButton.BUTTON_TYPE.UI_FAST_TOWER));
        ButtonList.addElement(new InGameButton(435, 667,
                SlowTowerImage, false, InGameButton.BUTTON_TYPE.UI_SLOW_TOWER));
        ButtonList.addElement(new InGameButton(638, 667,
                OpTowerImage, false, InGameButton.BUTTON_TYPE.UI_OP_TOWER));
        ButtonList.addElement(new InGameButton(37, 810,
                WorkerImage, false, InGameButton.BUTTON_TYPE.UI_WORKER));
    }

    void InitializeWaypoint()
    {
        //Reading Waypoints
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.waypointlevel1));
        while(scanner.hasNext())
        {
            String temp = scanner.next();
            String[] values = temp.split(",");

            Vector2 point = new Vector2(Float.parseFloat(values[0]), Float.parseFloat(values[1]));

            Waypoints.addElement(point);
        }
        scanner.close();
    }

    void InitializeWave()
    {
        //Reading Waves
        int waveIndex = 0;
        Scanner scanner = new Scanner(getResources().openRawResource(R.raw.wavelevel1));
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

            //Rendering Worker List on grids
            for(int i = 0; i < WorkerList.size(); ++i)
            {
                canvas.drawBitmap(WorkerList.elementAt(i).getImage(),
                        WorkerList.elementAt(i).getBoundingBox().getTopLeft().x,
                        WorkerList.elementAt(i).getBoundingBox().getTopLeft().y,
                        null);
            }

            //Render Enemies
            if(currentWave < WaveList.size()) {
                for (int i = 0; i < WaveList.get(currentWave).size(); ++i) {
                    if (WaveList.get(currentWave).get(i).isActive()) {
                        WaveList.get(currentWave).get(i).Draw(canvas);
                    }
                }
            }
            //Grid Frame
            canvas.drawBitmap(TD_Grid_Frame, 0, -20, null);

            if(ActionDown)
            {
                //Drag Tower
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
                            canvas.drawBitmap(SlowTowerImageDrag, TouchPos.x, TouchPos.y, null);
                        }
                        break;
                        case TOWER_OP:{
                            canvas.drawBitmap(OpTowerImageDrag, TouchPos.x, TouchPos.y, null);
                        }
                        break;
                    }
                }

                if(selectedWorker)
                {
                    canvas.drawBitmap(WorkerImageDrag, TouchPos.x, TouchPos.y, null);
                }
                if(selectedPlacedWorker != null)
                {
                    canvas.drawBitmap(WorkerImageDrag, TouchPos.x, TouchPos.y, null);
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

            // render win stuff
            if(Win)
            {
                 canvas.drawBitmap(NormalAIImage, 400, 600, null);
            }
            // render lose stuff
            else if(!Win)
            {
                canvas.drawBitmap(FastAIImage, 400, 600, null);
            }
           // canvas.drawBitmap(Restart_button.getImage(),Restart_button.getPosX(),Restart_button.getPosY(),null);
            //canvas.drawBitmap(Mainmenu_button.getImage(), Mainmenu_button.getPosX(), Mainmenu_button.getPosY(),null);
        }

        //FPS
        canvas.drawText("FPS:" + FPS, 50, 50, paint);
        canvas.drawText("touchPos X:" + FirstTouch.x, 50, 75, paint);
        canvas.drawText("touchPos Y:" + FirstTouch.y, 50, 100, paint);

        canvas.drawText(Integer.toString(player.getLivesCount()),727, 790, paint);
        canvas.drawText(Integer.toString(player.getWorkerCount()),93, 810, paint);

        canvas.drawText(Integer.toString(player.getFireElement()), 93, 915, paint);
        canvas.drawText(Integer.toString(player.getWaterElement()), 93, 985, paint);
        canvas.drawText(Integer.toString(player.getWindElement()), 93, 1055, paint);
        canvas.drawText(Integer.toString(player.getEarthElement()), 93, 1125, paint);

        RenderTowerRates(canvas);
    }

    public void RenderTowerRates(Canvas canvas)
    {
        canvas.drawText(Integer.toString(UITowerNormal.getFirecost()), 115, 660, paint);
        canvas.drawText(Integer.toString(UITowerNormal.getWatercost()), 175, 660, paint);
        canvas.drawText(Integer.toString(UITowerNormal.getEarthcost()), 115, 700, paint);
        canvas.drawText(Integer.toString(UITowerNormal.getWindcost()), 175, 700, paint);

        canvas.drawText(Integer.toString(UITowerHighfirerate.getFirecost()), 310, 660, paint);
        canvas.drawText(Integer.toString(UITowerHighfirerate.getWatercost()), 365, 660, paint);
        canvas.drawText(Integer.toString(UITowerHighfirerate.getEarthcost()), 310, 700, paint);
        canvas.drawText(Integer.toString(UITowerHighfirerate.getWindcost()), 365, 700, paint);

        canvas.drawText(Integer.toString(UITowerSlow.getFirecost()), 500, 660, paint);
        canvas.drawText(Integer.toString(UITowerSlow.getWatercost()), 565, 660, paint);
        canvas.drawText(Integer.toString(UITowerSlow.getEarthcost()), 500, 700, paint);
        canvas.drawText(Integer.toString(UITowerSlow.getWindcost()), 565, 700, paint);

        canvas.drawText(Integer.toString(UITowerOP.getFirecost()), 700, 660, paint);
        canvas.drawText(Integer.toString(UITowerOP.getWatercost()), 760, 660, paint);
        canvas.drawText(Integer.toString(UITowerOP.getEarthcost()), 700, 700, paint);
        canvas.drawText(Integer.toString(UITowerOP.getWindcost()), 760, 700, paint);
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

                        // lose condition
                        if(player.getLivesCount() <= 0)
                        {
                            GameActive = false;
                            Win = false;
                        }

                        // win condition
                        else if((currentWave >= WaveList.size()) && (player.getLivesCount() > 0))
                        {
                            GameActive = false;
                            Win = true;
                        }

                        //Spawning enemies
                        if (currentWave < WaveList.size()) {
                            spawnTimer += dt;

                            if (spawnTimer >= aiSpawnrate) {
                                if (currentSpawnIndex < WaveList.get(currentWave).size()) {
                                    spawnTimer = 0;
                                    WaveList.get(currentWave).get(currentSpawnIndex).setActive(true);
                                    waveStarted = true;
                                    ++currentSpawnIndex;
                                }
                            }
                        }

                        //Updating enemies
                        if (currentWave < WaveList.size()) {
                            for (int i = 0; i < WaveList.get(currentWave).size(); ++i) {
                                if (WaveList.get(currentWave).get(i).isActive()) {
                                    WaveList.get(currentWave).get(i).Update(dt);

                                    if(WaveList.get(currentWave).get(i).isEndofwaypoint())
                                    {
                                        player.DecreaseLives();
                                    }
                                }
                            }
                        }

                        //If wave has started
                        if (waveStarted) {
                            boolean wavecleared = false;

                            for (int i = 0; i < WaveList.get(currentWave).size(); ++i) {
                                if (WaveList.get(currentWave).get(i).isActive()) {
                                    wavecleared = false;
                                    break;
                                } else {
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
                        if (TowerList.size() > 0) {
                            //Iterate through all the towers that are on the grid
                            for (int i = 0; i < TowerList.size(); ++i) {
                                //Only if tower is able to fire
                                if (TowerList.get(i).Fire(dt)) {
                                    if (currentWave < WaveList.size()) {
                                        //Update Enemies
                                        for (int j = 0; j < WaveList.get(currentWave).size(); ++j) {
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
                                                                    Bubble3ProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300, Projectiles.PROJECTILE_TYPE.PROJECTILE_NORMAL);
                                                            break;
                                                        case TOWER_HIGHFIRERATE:
                                                            FetchProjectiles(TowerList.get(i).getPosition(),
                                                                    BubbleProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300, Projectiles.PROJECTILE_TYPE.PROJECTILE_NORMAL);
                                                            break;
                                                        case TOWER_SLOW:
                                                            FetchProjectiles(TowerList.get(i).getPosition(),
                                                                    Bubble2ProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300, Projectiles.PROJECTILE_TYPE.PROJECTILE_SLOW);
                                                            break;
                                                        case TOWER_OP:
                                                            FetchProjectiles(TowerList.get(i).getPosition(),
                                                                    Bubble2ProjectileImage, WaveList.get(currentWave).get(j).getPosition(), TowerList.get(i).getDamage(), 300, Projectiles.PROJECTILE_TYPE.PROJECTILE_SLOW);
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

                        elementTimer += dt;

                        if(elementTimer >= elementSpawnrate)
                        {
                            elementTimer = 0;

                            int firegain = 0;
                            int watergain = 0;
                            int windgain = 0;
                            int earthgain = 0;

                            for(int i = 0; i < FireGrid.length; ++i)
                            {
                                for(int j = 0; j < FireGrid[i].length; ++j)
                                {
                                    if(FireGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                                    {
                                        ++firegain;
                                    }
                                }
                            }

                            for(int i = 0; i < WaterGrid.length; ++i)
                            {
                                for(int j = 0; j < WaterGrid[i].length; ++j)
                                {
                                    if(WaterGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                                    {
                                        ++watergain;
                                    }
                                }
                            }

                            for(int i = 0; i < NatureGrid.length; ++i)
                            {
                                for(int j = 0; j < NatureGrid[i].length; ++j)
                                {
                                    if(NatureGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                                    {
                                        ++windgain;
                                    }
                                }
                            }

                            for(int i = 0; i < DarkGrid.length; ++i)
                            {
                                for(int j = 0; j < DarkGrid[i].length; ++j)
                                {
                                    if(DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                                    {
                                        ++earthgain;
                                    }
                                }
                            }

                            player.AddtoElements(firegain, watergain, windgain, earthgain);
                        }

                        //Update Projectiles
                        for (int i = 0; i < ProjectileList.size(); ++i) {
                            if (ProjectileList.get(i).isActive()) {
                                ProjectileList.get(i).Update(dt);
                            }
                        }

                        //Check Collision
                        for (int i = 0; i < ProjectileList.size(); ++i) {
                            //Only if the projectile is active, check it against all other active enemies
                            if (ProjectileList.get(i).isActive()) {
                                if (currentWave < WaveList.size()) {
                                    for (int j = 0; j < WaveList.get(currentWave).size(); ++j) {
                                        //Only if the enemy is active
                                        if (WaveList.get(currentWave).get(j).isActive()) {
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
                                                //Remove Projectile
                                                ProjectileList.get(i).setActive(false);
                                                //Update Health
                                                WaveList.get(currentWave).get(j).setHealth(WaveList.get(currentWave).get(j).getHealth() - ProjectileList.get(i).getDamage());

                                                if (ProjectileList.get(i).getType() == Projectiles.PROJECTILE_TYPE.PROJECTILE_SLOW)
                                                {

                                                    if (!WaveList.get(currentWave).get(j).slowed)
                                                    {
                                                        WaveList.get(currentWave).get(j).setMovespeed(WaveList.get(currentWave).get(j).getMovespeed() - ProjectileList.get(i).getDecreaseSpeed());
                                                        WaveList.get(currentWave).get(j).setSlowed(true);
                                                    }
                                                }
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
                        for (int i = 0; i < ButtonList.size(); ++i)
                        {
                            if (ButtonList.elementAt(i).getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY()))) {
                                switch (ButtonList.elementAt(i).buttonID) {
                                    case UI_NORMAL_TOWER:
                                        //Check if there is a selected tower
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_NORMAL) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL);

                                                if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                                {
                                                    selectedTower = null;
                                                }
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL);

                                            if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                            {
                                                selectedTower = null;
                                            }
                                        }
                                        break;

                                    case UI_FAST_TOWER:
                                        //Check if there is a selected tower
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_HIGHFIRERATE) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE);

                                                if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                                {
                                                    selectedTower = null;
                                                }
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE);

                                            if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                            {
                                                selectedTower = null;
                                            }
                                        }
                                        break;

                                    case UI_SLOW_TOWER:
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_SLOW) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW);

                                                if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                                {
                                                    selectedTower = null;
                                                }
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW);

                                            if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                            {
                                                selectedTower = null;
                                            }
                                        }
                                        break;

                                    case UI_OP_TOWER:
                                        if (selectedTower != null) {
                                            //Check if the selected tower is different from the one currently being selected
                                            if (selectedTower.getType() != Tower.TOWER_TYPE.TOWER_OP) {
                                                //If different, change selected tower to this
                                                selectedTower = new Tower(new Vector2(0, 0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP);

                                                if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                                {
                                                    selectedTower = null;
                                                }
                                            } else {
                                                //If currently selected tower is the same, deselect it
                                                selectedTower = null;
                                            }
                                        } else {
                                            //there is no selected tower
                                            selectedTower = new Tower(new Vector2(0, 0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP);

                                            if(!player.CheckCanBuild(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost()))
                                            {
                                                selectedTower = null;
                                            }
                                        }
                                        break;

                                    case UI_WORKER:
                                        //Only if there is more than 1 worker, we process this tap
                                        if(player.getWorkerCount() > 0)
                                        {
                                            selectedWorker = true;
                                        }
                                        break;
                                }
                            }
                        }

                        //Loop to process all the workers on the grid
                        for(int i = 0; i < WorkerList.size(); ++i)
                        {
                            //If Selected on a worker
                            if (WorkerList.elementAt(i).getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                            {
                                //Store this button
                                selectedPlacedWorker = WorkerList.elementAt(i);
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
                                        //Add Tower
                                        TowerList.addElement(new Tower(TowerGrid[i][j].getBoundingBox().getCenterPoint(),
                                                selectedTower.getImage(), selectedTower.getType()));

                                        //Set Grid to Occupied
                                        TowerGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);

                                        player.MinusfromElements(selectedTower.getFirecost(), selectedTower.getWatercost(), selectedTower.getWindcost(), selectedTower.getEarthcost());

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

                    //Process this only if there is only if worker is selected
                    //Worker must be selected from UI Button to process this part
                    if(selectedWorker)
                    {
                        boolean GridSelected = false;

                        //Processing Dark grid first
                        for(int i = 0; i < 3; ++i)
                        {
                            for (int j = 0; j < 3; ++j)
                            {
                                //Check That Grid is free first
                                if (DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                    //Check if action up is on that grid
                                    if (DarkGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                    {
                                        //Create worker on Grid
                                        WorkerList.addElement(new InGameButton(DarkGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                DarkGrid[i][j].getBoundingBox().getCenterPoint().y,
                                                WorkerImage, true, InGameButton.BUTTON_TYPE.UI_WORKER));

                                        //Reduce number of workers
                                        player.DecreaseWorker();
                                        //Set grid to occupied
                                        DarkGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                        GridSelected = true;
                                        break;
                                    }
                                }
                                if(GridSelected) {
                                    //Stop Loop
                                    break;
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Nature Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (NatureGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (NatureGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            //Create worker on Grid
                                            WorkerList.addElement(new InGameButton(NatureGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    NatureGrid[i][j].getBoundingBox().getCenterPoint().y,
                                                    WorkerImage, true, InGameButton.BUTTON_TYPE.UI_WORKER));

                                            //Reduce number of workers
                                            player.DecreaseWorker();
                                            //Set grid to occupied
                                            NatureGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Fire Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (FireGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (FireGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            //Create worker on Grid
                                            WorkerList.addElement(new InGameButton(FireGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    FireGrid[i][j].getBoundingBox().getCenterPoint().y,
                                                    WorkerImage, true, InGameButton.BUTTON_TYPE.UI_WORKER));

                                            //Reduce number of workers
                                            player.DecreaseWorker();
                                            //Set grid to occupied
                                            FireGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Water Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (WaterGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (WaterGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            //Create worker on Grid
                                            WorkerList.addElement(new InGameButton(WaterGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    WaterGrid[i][j].getBoundingBox().getCenterPoint().y,
                                                    WorkerImage, true, InGameButton.BUTTON_TYPE.UI_WORKER));

                                            //Reduce number of workers
                                            player.DecreaseWorker();
                                            //Set grid to occupied
                                            WaterGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        selectedWorker = false;
                    }

                    //Process this part if selected on a worker that is already placed
                    if(selectedPlacedWorker != null)
                    {
                        boolean GridSelected = false;

                        //Processing Dark grid first
                        for(int i = 0; i < 3; ++i)
                        {
                            for (int j = 0; j < 3; ++j)
                            {
                                //Check That Grid is free first
                                if (DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                    //Check if action up is on that grid
                                    if (DarkGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                    {
                                        FreeGrid(new Vector2(selectedPlacedWorker.getPosX(),selectedPlacedWorker.getPosY()));
                                        //Readjust position to the new one
                                        selectedPlacedWorker.setPosition(DarkGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                DarkGrid[i][j].getBoundingBox().getCenterPoint().y);

                                        //Set grid to occupied
                                        DarkGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                        GridSelected = true;
                                        break;
                                    }
                                }
                                if(GridSelected) {
                                    //Stop Loop
                                    break;
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Nature Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (NatureGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (NatureGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            FreeGrid(new Vector2(selectedPlacedWorker.getPosX(),selectedPlacedWorker.getPosY()));
                                            //Readjust position to the new one
                                            selectedPlacedWorker.setPosition(NatureGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    NatureGrid[i][j].getBoundingBox().getCenterPoint().y);

                                            //Set grid to occupied
                                            NatureGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Fire Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (FireGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (FireGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            FreeGrid(new Vector2(selectedPlacedWorker.getPosX(),selectedPlacedWorker.getPosY()));
                                            //Readjust position to the new one
                                            selectedPlacedWorker.setPosition(FireGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    FireGrid[i][j].getBoundingBox().getCenterPoint().y);

                                            //Set grid to occupied
                                            FireGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        //Only if a grid is not selected, we continue processing other Grids
                        //Water Grid
                        if(!GridSelected) {
                            for (int i = 0; i < 3; ++i) {
                                for (int j = 0; j < 3; ++j) {
                                    //Check That Grid is free first
                                    if (WaterGrid[i][j].getType() == GridNode.GRID_TYPE.GT_FREE) {
                                        //Check if action up is on that grid
                                        if (WaterGrid[i][j].getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                                        {
                                            FreeGrid(new Vector2(selectedPlacedWorker.getPosX(),selectedPlacedWorker.getPosY()));
                                            //Readjust position to the new one
                                            selectedPlacedWorker.setPosition(WaterGrid[i][j].getBoundingBox().getCenterPoint().x,
                                                    WaterGrid[i][j].getBoundingBox().getCenterPoint().y);

                                            //Set grid to occupied
                                            WaterGrid[i][j].setType(GridNode.GRID_TYPE.GT_OCCUPIED);
                                            GridSelected = true;
                                            break;
                                        }
                                    }
                                    if(GridSelected) {
                                        //Stop Loop
                                        break;
                                    }
                                }
                            }
                        }
                        selectedPlacedWorker = null;
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

    public void FetchProjectiles(Vector2 startPosition, Bitmap image,Vector2 TargetPos,float damage, float speed, Projectiles.PROJECTILE_TYPE type)
    {
        boolean Full = true;
        //Iterate through current list
        for(int i = 0; i < ProjectileList.size(); ++i)
        {
            //Get the first non active projectile to reuse
            if(ProjectileList.elementAt(i).isActive() == false)
            {
                //Set all data of the projectile
                ProjectileList.elementAt(i).SetAllData(new Vector2(startPosition.x,startPosition.y), image, speed, damage, TargetPos, true, type);
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
            ProjectileList.lastElement().SetAllData(new Vector2(startPosition.x, startPosition.y), image, speed, damage, TargetPos, true, type);
        }
    }

    public void FreeGrid(Vector2 Position)
    {
        boolean GridSelected = false;

        //Processing Dark grid first
        for(int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < 3; ++j)
            {
                //Check That Grid is free first
                if (DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                {
                    //If its this grid
                    if(DarkGrid[i][j].getBoundingBox().getCenterPoint().x == Position.x &&
                            DarkGrid[i][j].getBoundingBox().getCenterPoint().y == Position.y)
                    {
                        //Free grid
                        DarkGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                        GridSelected = true;
                        break;
                    }
                }
                if(GridSelected) {
                    //Stop Loop
                    break;
                }
            }
        }
        //Only if a grid is not selected, we continue processing other Grids
        //Nature Grid
        if(!GridSelected) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    //Check That Grid is free first
                    if (NatureGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                    {
                        //If its this grid
                        if(NatureGrid[i][j].getBoundingBox().getCenterPoint().x == Position.x &&
                                NatureGrid[i][j].getBoundingBox().getCenterPoint().y == Position.y)
                        {
                            //Free grid
                            NatureGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                            GridSelected = true;
                            break;
                        }
                    }
                    if(GridSelected) {
                        //Stop Loop
                        break;
                    }
                }
            }
        }
        //Only if a grid is not selected, we continue processing other Grids
        //Fire Grid
        if(!GridSelected) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    //Check That Grid is free first
                    if (FireGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                    {
                        //If its this grid
                        if(FireGrid[i][j].getBoundingBox().getCenterPoint().x == Position.x &&
                                FireGrid[i][j].getBoundingBox().getCenterPoint().y == Position.y)
                        {
                            //Free grid
                            FireGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                            GridSelected = true;
                            break;
                        }
                    }
                    if(GridSelected) {
                        //Stop Loop
                        break;
                    }
                }
            }
        }
        //Only if a grid is not selected, we continue processing other Grids
        //Water Grid
        if(!GridSelected) {
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    //Check That Grid is free first
                    if (WaterGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                    {
                        //If its this grid
                        if(WaterGrid[i][j].getBoundingBox().getCenterPoint().x == Position.x &&
                                WaterGrid[i][j].getBoundingBox().getCenterPoint().y == Position.y)
                        {
                            //Free grid
                            WaterGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                            GridSelected = true;
                            break;
                        }
                    }
                    if(GridSelected) {
                        //Stop Loop
                        break;
                    }
                }
            }
        }
    }

    //Vibration
    public void startVibrate(){
        long pattern[] = {0,500,500};
        v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(pattern, 0);
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
