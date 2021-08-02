package com.mygdx.game;

import com.badlogic.gdx.Gdx;

public class AppPreferences {
    private static final String PREF_MUSIC_VOLUME = "volume";
    private static final String PREF_MUSIC_ENABLED = "music enabled";
    private static final String PREF_SOUND_ENABLED = "sound enalbed";
    private static final String PREF_SOUND_VOLUME = "sound";
    private static final String PREF_NAME = "nome audio";

    protected com.badlogic.gdx.Preferences getPrefs(){
        return Gdx.app.getPreferences(PREF_NAME);
    }

    // getter per prendere il volume salvato o, nel caso, quello di default a metà volume (0.5)

    public float getMusicVolume(){
        return getPrefs().getFloat(PREF_MUSIC_VOLUME, 0.5f);
    }

    // setter per settare il volume, il flush selve per salvare su disco

    public void setMusicVolume(float volume){
        getPrefs().putFloat(PREF_MUSIC_VOLUME, volume);
        getPrefs().flush();
    }

    // faccio lo stesso per le altre variabili

    public boolean isMusicEnabled(){
        return getPrefs().getBoolean(PREF_MUSIC_ENABLED, true);
    }

    public void setMusicEnabled(boolean musicEnabled){
        getPrefs().putBoolean(PREF_MUSIC_ENABLED, musicEnabled);
        getPrefs().flush();
    }

    public float getSoundVolume(){
        return getPrefs().getFloat(PREF_SOUND_VOLUME, 0.5f);
    }

    public void setSoundVolume(float soundVolume){
        getPrefs().putFloat(PREF_SOUND_VOLUME, soundVolume);
        getPrefs().flush();
    }

    public boolean isSoundEnabled(){
        return getPrefs().getBoolean(PREF_SOUND_ENABLED, true);
    }

    public void setSoundEnabled(boolean soundEnabled){
        getPrefs().putBoolean(PREF_SOUND_ENABLED, soundEnabled);
        getPrefs().flush();
    }
}
