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


public class Mainmenu extends Activity implements OnClickListener{

    //Buttons
    private Button btn_play;
    private Button btn_options;
    private Button btn_credits;
    private Button btn_highscore;

    //Sound manager
    SoundManager soundManager;

    //Share prefs
    AppPrefs appPrefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar

        setContentView(R.layout.mainmenu);

        Context context = getApplicationContext();

        appPrefs = new AppPrefs(context);

        appPrefs.CheckIfExist();

        soundManager = new SoundManager();

        //Checking if sound manager is made
        if(!soundManager.IsInited())
        {
            soundManager.InitSoundPool(context, appPrefs);
            //soundManager.PlayBGM();
        }

        //Initializing buttons
        btn_play = (Button)findViewById(R.id.btn_play);
        btn_play.setOnClickListener(this);

        btn_options = (Button)findViewById(R.id.btn_options);
        btn_options.setOnClickListener(this);

        btn_credits = (Button)findViewById(R.id.btn_credits);
        btn_credits.setOnClickListener(this);

        btn_highscore = (Button)findViewById(R.id.btn_highscore);
        btn_highscore.setOnClickListener(this);
    }

    //On tapping one of the mainmenu buttons
    public void onClick(View v) {
        Intent intent = new Intent();

        soundManager.PlaySFX();

        //Setting intent based on button input
        if(v == btn_play)
        {
            intent.setClass(this,Playpage.class);
        }
        else if( v == btn_options)
        {
            intent.setClass(this,Optionspage.class);
        }
        else if( v == btn_credits)
        {
            intent.setClass(this,Creditpage.class);
        }
        else if( v == btn_highscore)
        {
            intent.setClass(this,Highscorepage.class);
        }

        //Start the new activity
        startActivity(intent);
    }

    protected void onPause(){
        //soundManager.PauseBGM();
        super.onPause();
    }

    protected void onResume(){
        //soundManager.UnPauseBGM();
        super.onResume();
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
