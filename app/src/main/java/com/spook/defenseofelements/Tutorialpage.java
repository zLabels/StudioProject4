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

public class Tutorialpage extends Activity implements OnClickListener{

    //Buttons
    private Button btn_tutorialback;
    private Button btn_nextpage;
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

        setContentView(R.layout.tutorialpage);

        btn_tutorialback = (Button)findViewById(R.id.btn_tutorialback);
        btn_tutorialback.setOnClickListener(this);

        btn_nextpage = (Button)findViewById(R.id.btn_next);
        btn_nextpage.setOnClickListener(this);

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
            intent.setClass(this,Tutorialpagetwo.class);
        }
        startActivity(intent);
    }

    protected void onPause()
    {
        soundManager.PauseBGM();
        super.onPause();
    }

    protected void onStop()
    {
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}
