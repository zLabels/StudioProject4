package com.spook.defenseofelements;

/**
 * Created by Jessica on 22/2/2016.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Creditpage extends Activity implements OnClickListener{

    private Button btn_creditsback;
    //Media Player
    SoundManager soundManager;
    //Share prefs
    AppPrefs appPrefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar

        setContentView(R.layout.creditpage);

        //Initializing buttons
        btn_creditsback = (Button)findViewById(R.id.btn_creditsback);
        btn_creditsback.setOnClickListener(this);

        soundManager = new SoundManager();

        Context context = getApplicationContext();

        appPrefs = new AppPrefs(context);

        appPrefs.CheckIfExist();

        soundManager = new SoundManager();

        if(!soundManager.IsInited())
        {
            soundManager.InitSoundPool(context, appPrefs);
            soundManager.PlayCredits();
        }
        else
        {
            soundManager.PauseBGM();
            soundManager.PlayCredits();
        }

    }
    public void onClick(View v) {
        Intent intent = new Intent();

        soundManager.PlaySFX();

        if(v == btn_creditsback)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this, Mainmenu.class);
        }
        startActivity(intent);
    }

    protected void onPause(){
        soundManager.PauseCredits();
        super.onPause();
    }

    protected void onResume()
    {
        soundManager.PauseBGM();
        soundManager.UnPauseCredits();
        super.onResume();
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
