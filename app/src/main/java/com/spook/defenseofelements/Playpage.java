package com.spook.defenseofelements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.media.MediaPlayer;

public class Playpage extends Activity implements OnClickListener{

    //Buttons
    private Button btn_start;
    private Button btn_levelback;
    private Button btn_level1;
    private Button btn_level2;
    int levelSelected = 1;
    //private Button btn_endless;
    //private Button btn_tutorial;
    //Media Player
    SoundManager soundManager;

    AppPrefs appPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar

        setContentView(R.layout.playpage);

        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(this);

        btn_levelback = (Button) findViewById(R.id.btn_levelback);
        btn_levelback.setOnClickListener(this);

        btn_level1 = (Button) findViewById(R.id.btn_level1);
        btn_level1.setOnClickListener(this);

        btn_level2 = (Button) findViewById(R.id.btn_level2);
        btn_level2.setOnClickListener(this);

        /*btn_endless = (Button)findViewById(R.id.btn_endless);
        btn_endless.setOnClickListener(this);

        btn_tutorial = (Button)findViewById(R.id.btn_tutorial);
        btn_tutorial.setOnClickListener(this);*/

        Context context = getApplicationContext();

        appPrefs = new AppPrefs(context);

        appPrefs.CheckIfExist();

        soundManager = new SoundManager();

        if (!soundManager.IsInited())
        {
            soundManager.InitSoundPool(context, appPrefs);
            soundManager.PlayBGM();
        }
        else
        {
            soundManager.PauseCredits();
            //soundManager.PauseGameBgm();
            soundManager.UnPauseBGM();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        soundManager.PlaySFX();

       if(v == btn_start)
        {
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            //intent.setClass(this,GamePage.class);

            switch (levelSelected)
            {
                case 1: intent.setClass(this, GamePage.class);
                    break;
                case 2: intent.setClass(this, GamePageLV2.class);
                    break;
            }

            startActivity(intent);
        }
         if(v == btn_levelback)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Mainmenu.class);
            startActivity(intent);
        }

        if(v == btn_level1)
        {
            levelSelected = 1;
            btn_level1.setBackgroundResource(R.drawable.selected_button);
            btn_level2.setBackgroundResource(R.drawable.unselected_button);
        }

        if(v == btn_level2)
        {
            levelSelected = 2;
            btn_level1.setBackgroundResource(R.drawable.unselected_button);
            btn_level2.setBackgroundResource(R.drawable.selected_button);
        }

         /*if(v == btn_endless)
        {
            intent.setClass(this,GamePage.class);
        }
        if(v == btn_tutorial)
        {
            intent.setClass(this,Tutorialpage.class);
        }*/
    }

    protected void onPause(){
        soundManager.PauseBGM();
        super.onPause();
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
