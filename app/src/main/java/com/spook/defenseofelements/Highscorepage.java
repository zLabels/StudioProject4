package com.spook.defenseofelements;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import java.util.Vector;


public class Highscorepage extends Activity implements OnClickListener{

    private Button btn_highscoreback;
    private Vector<TextView> tv_scoreTexts;
    private Vector<String> scoreStrings;
    int numScore;
    AppPrefs appPrefs;
    SoundManager soundManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// hide title
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide top bar

        setContentView(R.layout.highscorepage);

        numScore = 5;

        Context context = getApplicationContext();
        appPrefs = new AppPrefs(context);

        Vector<Integer> scores = new Vector<Integer>();

        scores = appPrefs.getHighscore();

        //Typeface typeface = Typeface.createFromAsset(getAssets(), "fonts/LoveSweets.ttf");

        tv_scoreTexts = new Vector<TextView>();

        tv_scoreTexts.addElement((TextView) findViewById(R.id.tv_score1));
        tv_scoreTexts.addElement((TextView) findViewById(R.id.tv_score2));
        tv_scoreTexts.addElement((TextView) findViewById(R.id.tv_score3));
        tv_scoreTexts.addElement((TextView) findViewById(R.id.tv_score4));
        tv_scoreTexts.addElement((TextView) findViewById(R.id.tv_score5));

        scoreStrings = new Vector<String>();

        for(int i = 0; i < numScore; ++i)
        {
            scoreStrings.addElement(i+1 + ". " + scores.get(i));
            tv_scoreTexts.get(i).setText(scoreStrings.get(i));

            Rect bounds = new Rect();
            Paint textPaint = tv_scoreTexts.get(i).getPaint();
            textPaint.getTextBounds(scoreStrings.get(i), 0, scoreStrings.get(i).length(), bounds);
            tv_scoreTexts.get(i).setWidth(bounds.width());
            //tv_scoreTexts.get(i).setTypeface(typeface);
        }

        btn_highscoreback = (Button)findViewById(R.id.btn_highscoreback);
        btn_highscoreback.setOnClickListener(this);

        soundManager = new SoundManager();
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent();

        soundManager.PlaySFX();

        if(v == btn_highscoreback)
        {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.setClass(this,Mainmenu.class);
        }

        startActivity(intent);
    }

    protected void onPause(){
        super.onPause();
    }

    protected void onStop(){
        super.onStop();
    }

    protected void onDestroy(){
        super.onDestroy();
    }
}

