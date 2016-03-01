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
            intent.setClass(this,GamePage.class);
        }
         if(v == btn_levelback)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Mainmenu.class);
        }
         /*if(v == btn_endless)
        {
            intent.setClass(this,GamePage.class);
        }
        if(v == btn_tutorial)
        {
            intent.setClass(this,Tutorialpage.class);
        }*/

        startActivity(intent);
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
