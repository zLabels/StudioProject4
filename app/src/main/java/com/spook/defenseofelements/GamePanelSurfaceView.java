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
    Paint paint = new Paint(); //Used for text rendering (black)
    Paint paint2 = new Paint(); //White

    //Feedback
    SoundManager soundManager;

    GamePage gamePage;

    //Random
    Random r = new Random();

    private short GameState;    // Variable for Game State check

    //Dragging Variables
    boolean ActionDown = false;
    boolean ActionUp = false;
    Vector2 FirstTouch = new Vector2(0.0f,0.0f);
    Vector2 TouchPos = new Vector2(0.0f,0.0f);

    //Game elements
    int currentWave = 0;
    int currentSpawnIndex = 0;
    float aiSpawnrate = 5.f;
    float spawnTimer = 0.f;
    boolean waveStarted = false;
    boolean selectedWorker = false; //For use on UI Button Worker
    InGameButton selectedPlacedWorker;   //For use on worker that is already on grid
    float elementSpawnrate = 5.f;
    float elementTimer = 0.f;
    boolean Win = false;

    int UpgradeLevel = 0;   //Player's Upgrade level for towers
    int UpgradeFireCost = 10;
    int UpgradeDarkCost = 10;
    int UpgradeWaterCost = 10;
    int UpgradeNatureCost = 10;
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

    //List containing All In Game Buttons
    Vector<InGameButton> ButtonList = new Vector<InGameButton>();
    InGameButton retryButton = new InGameButton(140, 600, BitmapFactory.decodeResource(getResources(), R.drawable.retry_button), false, InGameButton.BUTTON_TYPE.UI_RETRY);
    InGameButton replayButton = new InGameButton(140, 600, BitmapFactory.decodeResource(getResources(), R.drawable.replay_button), false, InGameButton.BUTTON_TYPE.UI_REPLAY);
    InGameButton mainmainButton = new InGameButton(140, 750, BitmapFactory.decodeResource(getResources(), R.drawable.mainmenu_button), false, InGameButton.BUTTON_TYPE.UI_MAINMENU);

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

    //Images
    private Bitmap TileMap =  BitmapFactory.decodeResource(getResources(), R.drawable.grass_floor_tileset);
    private Bitmap TD_Grid_Frame = BitmapFactory.decodeResource(getResources(), R.drawable.td_grid_frame);
    private Bitmap WorkerImage = BitmapFactory.decodeResource(getResources(), R.drawable.worker);
    private Bitmap WorkerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.worker_drag);
    private Bitmap UpgradeButton = BitmapFactory.decodeResource(getResources(), R.drawable.upgrade_button);
    private Bitmap UpgradeButtonInactive = BitmapFactory.decodeResource(getResources(), R.drawable.upgrade_button_inactive);

    //Towers
    private Bitmap NormalTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal);
    private Bitmap NormalTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal_drag);
    private Bitmap NormalTowerImageGrey = BitmapFactory.decodeResource(getResources(), R.drawable.tower_normal_grey);
    private Bitmap FastTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast);
    private Bitmap FastTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast_drag);
    private Bitmap FastTowerImageGrey = BitmapFactory.decodeResource(getResources(), R.drawable.tower_fast_grey);
    private Bitmap SlowTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_slow);
    private Bitmap SlowTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_slow_drag);
    private Bitmap SlowTowerImageGrey = BitmapFactory.decodeResource(getResources(), R.drawable.tower_slow_grey);
    private Bitmap OpTowerImage = BitmapFactory.decodeResource(getResources(), R.drawable.tower_op);
    private Bitmap OpTowerImageDrag = BitmapFactory.decodeResource(getResources(), R.drawable.tower_op_drag);
    private Bitmap OpTowerImageGrey = BitmapFactory.decodeResource(getResources(), R.drawable.tower_op_grey);

    //Used for rendering tower resources
    Tower UITowerNormal = new Tower(new Vector2(0,0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL,0);
    Tower UITowerHighfirerate = new Tower(new Vector2(0,0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE,0);
    Tower UITowerSlow = new Tower(new Vector2(0,0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW,0);
    Tower UITowerOP = new Tower(new Vector2(0,0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP,0);

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

        paint2.setARGB(255, 255, 255, 255);
        paint2.setStrokeWidth(100);
        paint2.setTextSize(30);

        // Create the game loop thread
        myThread = new GameThread(getHolder(), this);

        //Set Current Selected Tower to null
        selectedTower = null;

        // Make the GamePanel focusable so it can handle events
        setFocusable(true);

        //Shared prefs
        appPrefs = new AppPrefs(context);

        appPrefs.CheckIfExist();

        //Play the GameBGM
        if(!soundManager.IsInited())
        {
            soundManager.InitSoundPool(context, appPrefs);
            soundManager.PlayGameBgm();
        }
        else
        {
            soundManager.PauseBGM();
            soundManager.PlayGameBgm();
        }
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
        ButtonList.addElement(new InGameButton(63, 1180,
                UpgradeButton, false, InGameButton.BUTTON_TYPE.UI_UPGRADE));
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
            //soundManager.PauseGameBgm();
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
                //soundManager.PauseGameBgm();
            }
        }
    }
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    public void RenderGameplay(Canvas canvas)
    {
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

        RenderOtherText(canvas);

        RenderTowerRates(canvas);

        //Game is lost
        if(!GameActive){

            // render win stuff
            if(Win)
            {
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.gameclear_page), 100, 225, null);
                canvas.drawBitmap(replayButton.getImage(), replayButton.getPosX(), replayButton.getPosY(), null);
                canvas.drawBitmap(mainmainButton.getImage(), mainmainButton.getPosX(), mainmainButton.getPosY(), null);
            }
            // render lose stuff
            else if(!Win)
            {
                canvas.drawBitmap(BitmapFactory.decodeResource(getResources(),R.drawable.gameover_page), 100, 225, null);
                canvas.drawBitmap(retryButton.getImage(), retryButton.getPosX(), retryButton.getPosY(), null);
                canvas.drawBitmap(mainmainButton.getImage(), mainmainButton.getPosX(), mainmainButton.getPosY(), null);
            }
        }
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

    public void RenderOtherText(Canvas canvas)
    {
        //Debugging Information
        canvas.drawText("FPS:" + FPS, 50, 50, paint);
        canvas.drawText("touchPos X:" + FirstTouch.x, 50, 75, paint);
        canvas.drawText("touchPos Y:" + FirstTouch.y, 50, 100, paint);

        //Player Related Variables
        canvas.drawText(Integer.toString(player.getLivesCount()),727, 790, paint);
        canvas.drawText(Integer.toString(player.getWorkerCount()), 93, 810, paint);

        canvas.drawText(Integer.toString(player.getFireElement()), 80, 915, paint);
        canvas.drawText(Integer.toString(player.getWaterElement()), 80, 985, paint);
        canvas.drawText(Integer.toString(player.getEarthElement()), 80, 1055, paint);
        canvas.drawText(Integer.toString(player.getWindElement()), 80, 1125, paint);

        //Display current wave
        canvas.drawText(Integer.toString(currentWave + 1), 710, 1175, paint);
        canvas.drawText(Integer.toString(WaveList.size()), 740, 1200, paint);

        //Element generating Timer
        canvas.drawText(Integer.toString((int) elementTimer), 390, 985,paint2);
    }

    public void UpdateUIImages()
    {
        for(int i = 0; i < ButtonList.size(); ++i)
        {
            switch(ButtonList.get(i).buttonID)
            {
                case UI_NORMAL_TOWER:
                    //If player is able to build
                    if(player.CheckCanBuild(UITowerNormal.getFirecost(),
                            UITowerNormal.getWatercost(),
                            UITowerNormal.getWindcost(),
                            UITowerNormal.getEarthcost()))
                    {
                        if(ButtonList.get(i).getImage() != NormalTowerImage)
                        {
                            ButtonList.get(i).setImage(NormalTowerImage);
                        }
                    }
                    else
                    {
                        //Unable to build
                        if(ButtonList.get(i).getImage() != NormalTowerImageGrey)
                        {
                            ButtonList.get(i).setImage(NormalTowerImageGrey);
                        }
                    }
                    break;
                case UI_FAST_TOWER:
                    //If player is able to build
                    if(player.CheckCanBuild(UITowerHighfirerate.getFirecost(),
                            UITowerHighfirerate.getWatercost(),
                            UITowerHighfirerate.getWindcost(),
                            UITowerHighfirerate.getEarthcost()))
                    {
                        if(ButtonList.get(i).getImage() != FastTowerImage)
                        {
                            ButtonList.get(i).setImage(FastTowerImage);
                        }
                    }
                    else
                    {
                        //Unable to build
                        if(ButtonList.get(i).getImage() != FastTowerImageGrey)
                        {
                            ButtonList.get(i).setImage(FastTowerImageGrey);
                        }
                    }
                    break;
                case UI_SLOW_TOWER:
                    //If player is able to build
                    if(player.CheckCanBuild(UITowerSlow.getFirecost(),
                            UITowerSlow.getWatercost(),
                            UITowerSlow.getWindcost(),
                            UITowerSlow.getEarthcost()))
                    {
                        if(ButtonList.get(i).getImage() != SlowTowerImage)
                        {
                            ButtonList.get(i).setImage(SlowTowerImage);
                        }
                    }
                    else
                    {
                        //Unable to build
                        if(ButtonList.get(i).getImage() != SlowTowerImageGrey)
                        {
                            ButtonList.get(i).setImage(SlowTowerImageGrey);
                        }
                    }
                    break;
                case UI_OP_TOWER:
                    //If player is able to build
                    if(player.CheckCanBuild(UITowerOP.getFirecost(),
                            UITowerOP.getWatercost(),
                            UITowerOP.getWindcost(),
                            UITowerOP.getEarthcost()))
                    {
                        if(ButtonList.get(i).getImage() != OpTowerImage)
                        {
                            ButtonList.get(i).setImage(OpTowerImage);
                        }
                    }
                    else
                    {
                        //Unable to build
                        if(ButtonList.get(i).getImage() != OpTowerImageGrey)
                        {
                            ButtonList.get(i).setImage(OpTowerImageGrey);
                        }
                    }
                    break;
                case UI_UPGRADE:
                    //If player is able to build
                    if(player.CheckCanBuild(UpgradeFireCost,
                            UpgradeWaterCost,
                            UpgradeDarkCost,
                            UpgradeNatureCost))
                    {
                        if(ButtonList.get(i).getImage() != UpgradeButton)
                        {
                            ButtonList.get(i).setImage(UpgradeButton);
                        }
                    }
                    else
                    {
                        //Unable to build
                        if(ButtonList.get(i).getImage() != UpgradeButtonInactive)
                        {
                            ButtonList.get(i).setImage(UpgradeButtonInactive);
                        }
                    }
                    break;
            }
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
                    if (GameActive)
                    {
                        UpdateUIImages();
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
                                if((currentWave + 1)% 2 == 0)
                                {
                                    soundManager.PlayWorker();
                                    player.IncreaseWorker();
                                }
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
                                                    TowerList.get(i).Update(WaveList.get(currentWave).get(j).getPosition());
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

                        //Element Generating
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
                                        ++earthgain;
                                    }
                                }
                            }

                            for(int i = 0; i < DarkGrid.length; ++i)
                            {
                                for(int j = 0; j < DarkGrid[i].length; ++j)
                                {
                                    if(DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED)
                                    {
                                        ++windgain;
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
                                                //Play the Effect
                                                soundManager.PlayEffect();

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
                        soundManager.PauseGameBgm();
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
                                                selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL,UpgradeLevel);
                                                //Play the Sound effect
                                                soundManager.PlaySFX();

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
                                            selectedTower = new Tower(new Vector2(0, 0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL,UpgradeLevel);
                                            //Play the Sound effect
                                            soundManager.PlaySFX();

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
                                                selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE,UpgradeLevel);
                                                //Play the Sound effect
                                                soundManager.PlaySFX();

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
                                            selectedTower = new Tower(new Vector2(0, 0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE,UpgradeLevel);
                                            //Play the Sound effect
                                            soundManager.PlaySFX();

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
                                                selectedTower = new Tower(new Vector2(0, 0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW,UpgradeLevel);
                                                //Play the Sound effect
                                                soundManager.PlaySFX();

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
                                            selectedTower = new Tower(new Vector2(0, 0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW,UpgradeLevel);
                                            //Play the Sound effect
                                            soundManager.PlaySFX();

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
                                                selectedTower = new Tower(new Vector2(0, 0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP,0);
                                                //Play the Sound effect
                                                soundManager.PlaySFX();

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
                                            selectedTower = new Tower(new Vector2(0, 0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP,0);
                                            //Play the Sound effect
                                            soundManager.PlaySFX();

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
                                            //Play the Sound effect
                                            soundManager.PlaySFX();
                                        }
                                        break;
                                    case UI_UPGRADE:
                                        //Only if player has enough resources
                                        if(player.getEarthElement() >= UpgradeNatureCost &&
                                                player.getFireElement() >= UpgradeFireCost &&
                                                player.getWaterElement() >= UpgradeWaterCost &&
                                                player.getWindElement() >= UpgradeDarkCost)
                                        {
                                            //Increase Upgrade Level
                                            UpgradeLevel++;
                                            //Reduce player resources first
                                            player.MinusfromElements(UpgradeFireCost,UpgradeWaterCost,UpgradeDarkCost,UpgradeNatureCost);

                                            //Increase Cost for next upgrade
                                            UpgradeFireCost += 10;
                                            UpgradeNatureCost += 10;
                                            UpgradeWaterCost += 10;
                                            UpgradeDarkCost += 10;

                                            //Update UI resources to match
                                            UITowerHighfirerate.UpgradeTower(1);
                                            UITowerNormal.UpgradeTower(1);
                                            UITowerSlow.UpgradeTower(1);

                                            //Play the Sound effect
                                            soundManager.PlaySFX();
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

                                        //Only if its not the Ultimate Tower
                                        if(selectedTower.getType() != Tower.TOWER_TYPE.TOWER_OP) {
                                            //Add Tower
                                            TowerList.addElement(new Tower(TowerGrid[i][j].getBoundingBox().getCenterPoint(),
                                                    selectedTower.getImage(), selectedTower.getType(), UpgradeLevel));
                                        }
                                        else
                                        {
                                            //Dont upgrade Ultimate tower
                                            TowerList.addElement(new Tower(TowerGrid[i][j].getBoundingBox().getCenterPoint(),
                                                    selectedTower.getImage(), selectedTower.getType(), 0));
                                        }

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
            switch (event.getAction())
            {
                case MotionEvent.ACTION_DOWN:
                {
                    //If tap on retry button
                    if(retryButton.getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                    {
                        soundManager.PauseGameBgm();
                        soundManager.PlaySFX();
                        Reset();
                    }
                    //If tap on replay button
                    else if(replayButton.getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                    {
                        soundManager.PauseGameBgm();
                        soundManager.PlaySFX();
                        Reset();
                    }
                    //If tap on main menu button
                    else if(mainmainButton.getBoundingBox().CheckIntersect(new Vector2(event.getX(), event.getY())))
                    {
                        soundManager.PlaySFX();

                        Intent intent = new Intent();
                        intent.setClass(getContext(), Mainmenu.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        getContext().startActivity(intent);

                        soundManager.PauseGameBgm();
                    }
                }
                break;
            }
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

    public void ResetGrids()
    {
        //Reset Tower Grid
        for(int i = 0; i < 9; ++i)
        {
            for(int j = 0; j < 12; ++j)
            {
                //Reset all occupied grids back to free
                if(TowerGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED) {
                    TowerGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                }
            }
        }

        //Reset All Elemental Grids
        for(int i = 0; i < 3; ++i)
        {
            for(int j = 0; j < 3; ++j)
            {
                //Reset all occupied grids back to free
                if(DarkGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED) {
                    DarkGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                }
                if(WaterGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED) {
                    WaterGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                }
                if(FireGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED) {
                    FireGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                }
                if(NatureGrid[i][j].getType() == GridNode.GRID_TYPE.GT_OCCUPIED) {
                    NatureGrid[i][j].setType(GridNode.GRID_TYPE.GT_FREE);
                }
            }
        }
    }

    public void ResetVariables()
    {
        currentWave = 0;
        currentSpawnIndex = 0;
        aiSpawnrate = 5.0f;
        spawnTimer = 0.0f;
        waveStarted = false;
        selectedWorker = false;
        elementSpawnrate = 5.0f;
        elementTimer = 0.0f;
        Win = false;
        selectedPlacedWorker = null;

        //Reset all upgrades
        UpgradeLevel = 0;
        UpgradeFireCost = 10;
        UpgradeDarkCost = 10;
        UpgradeWaterCost = 10;
        UpgradeNatureCost = 10;

        UITowerNormal = new Tower(new Vector2(0,0), NormalTowerImage, Tower.TOWER_TYPE.TOWER_NORMAL,0);
        UITowerHighfirerate = new Tower(new Vector2(0,0), FastTowerImage, Tower.TOWER_TYPE.TOWER_HIGHFIRERATE,0);
        UITowerSlow = new Tower(new Vector2(0,0), SlowTowerImage, Tower.TOWER_TYPE.TOWER_SLOW,0);
        UITowerOP = new Tower(new Vector2(0,0), OpTowerImage, Tower.TOWER_TYPE.TOWER_OP,0);
    }

    public void ResetVectors()
    {
        //Clear All Towers
        TowerList.clear();

        //Clear and reinitialize waves
        WaveList.clear();
        InitializeWave();

        //Clear All worker in grid
        WorkerList.clear();

        //Reset All Projectile
        for(int i = 0; i < ProjectileList.size(); ++i)
        {
            //Get the first non active projectile to reuse
            if(ProjectileList.elementAt(i).isActive())
            {
                //Set all data of the projectile
                ProjectileList.elementAt(i).setActive(false);
                //Stop the loop
                break;
            }
        }

    }

    //Restart game variables
    public void Reset()
    {
        //Reset Grid back to original
        ResetGrids();

        //Reset Variables back to original
        ResetVariables();

        //Reset Vectors back to original
        ResetVectors();

        //Reset Player to default
        player.ResetAll();

        //Reset the Game BGM sound
        soundManager.PlayGameBgm();

        //Reset everything first before we set game active back to true
        GameActive = true;
    }

}
