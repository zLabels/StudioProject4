package com.spook.defenseofelements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

/**
 * Created by Jessica on 29/2/2016.
 */

public class Tutorialpagetwo extends Activity implements View.OnClickListener {

    //Buttons
    private Button btn_tutorialback;
    private Button btn_nextpage;
    private Button btn_backpage;
    //Media Player
    SoundManager soundManager;

    AppPrefs appPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar

        setContentView(R.layout.tutorialtwopage);

        btn_tutorialback = (Button)findViewById(R.id.btn_tutorialback);
        btn_tutorialback.setOnClickListener(this);

        btn_nextpage = (Button)findViewById(R.id.btn_next);
        btn_nextpage.setOnClickListener(this);

        btn_backpage = (Button)findViewById(R.id.btn_back);
        btn_backpage.setOnClickListener(this);

        Context context = getApplicationContext();

        appPrefs = new AppPrefs(context);

        appPrefs.CheckIfExist();

        soundManager = new SoundManager();

        if(!soundManager.IsInited())
        {
            soundManager.InitSoundPool(context, appPrefs);
            soundManager.PlayBGM();
        }
        else
        {
            soundManager.UnPauseBGM();
        }
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        soundManager.PlaySFX();

        if(v == btn_tutorialback)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Mainmenu.class);
        }
        else if(v == btn_nextpage)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Tutorialthreepage.class);
        }
        else if(v == btn_backpage)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Tutorialpage.class);
        }
        startActivity(intent);
    }

    protected void onPause(){
        soundManager.PauseBGM();
        super.onPause();
    }

    protected void onStop(){
        soundManager.UnPauseBGM();
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
