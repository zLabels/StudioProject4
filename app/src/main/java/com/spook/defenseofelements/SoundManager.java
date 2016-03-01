package com.spook.defenseofelements;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;

/**
 * Created by Malcolm on 17/1/2016.
 */
public class SoundManager {
    final static SoundPool soundpool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

    static MediaPlayer mediaPlayer;
    static MediaPlayer creditsPlayer;
    static MediaPlayer gamesBGMPlayer;

    static int SFX;
    static int EffectFX;
    static float BGMVolume, SFXVolume;
    static boolean Inited = false;

    void InitSoundPool(Context context, AppPrefs appPrefs){

        mediaPlayer = MediaPlayer.create(context, R.raw.mainmenu_bgm);
        creditsPlayer = MediaPlayer.create(context, R.raw.credits_bgm);
        gamesBGMPlayer = MediaPlayer.create(context, R.raw.game_bgm);

        SFX = soundpool.load(context, R.raw.menu_feedback, 1);
        EffectFX = soundpool.load(context, R.raw.bubble_pop_sound, 1);

        BGMVolume = ((float)appPrefs.getVolume().get(0))/100;
        SFXVolume = ((float)appPrefs.getVolume().get(1))/100;

        mediaPlayer.setVolume(BGMVolume, BGMVolume);
        creditsPlayer.setVolume(BGMVolume, BGMVolume);
        gamesBGMPlayer.setVolume(BGMVolume, BGMVolume);

        soundpool.setVolume(SFX, SFXVolume, SFXVolume);
        soundpool.setVolume(EffectFX, SFXVolume, SFXVolume);

        Inited = true;
    }

    void PlayBGM() {
        mediaPlayer.setLooping(true);
        mediaPlayer.start();
    }

    void PlayCredits()
    {
        creditsPlayer.setLooping(true);
        creditsPlayer.start();
    }

    void PlayGameBgm()
    {
        gamesBGMPlayer.setLooping(true);
        gamesBGMPlayer.start();
    }

    void PlaySFX() {
        soundpool.play(SFX, SFXVolume, SFXVolume, 1, 0, 1);
    }

    void PlayEffect()
    {
        soundpool.play(EffectFX, SFXVolume, SFXVolume, 1, 0, 1);
    }

    void SetBGMVolume(int newVolume){

        BGMVolume = ((float)newVolume) / 100;

        mediaPlayer.setVolume(BGMVolume, BGMVolume);
        creditsPlayer.setVolume(BGMVolume, BGMVolume);
        gamesBGMPlayer.setVolume(BGMVolume, BGMVolume);
    }

    void SetSFXVolume(int newVolume){

        SFXVolume = ((float)newVolume) / 100;

        soundpool.setVolume(SFX, SFXVolume, SFXVolume);
        soundpool.setVolume(EffectFX, SFXVolume, SFXVolume);
    }

    void PauseBGM(){
        mediaPlayer.pause();
    }

    void UnPauseBGM(){
        mediaPlayer.start();
    }

    void PauseCredits()
    {
        creditsPlayer.pause();
    }

    void UnPauseCredits()
    {
        creditsPlayer.start();
    }

    void PauseGameBgm()
    {
        gamesBGMPlayer.pause();
    }

    void UnPauseGameBgm()
    {
        gamesBGMPlayer.start();
    }

    boolean IsInited(){
        return Inited;
    }
}
