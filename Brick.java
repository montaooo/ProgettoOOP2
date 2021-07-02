package com.mygdx.game;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;


public class Brick extends InteractiveTileObject {

    public Brick(PlayState screen, MapObject object){
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MyGame.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Player mario) {
        /*
        if(mario.isBig()) {
            setCategoryFilter(MyGame.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MyGame.manager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        MyGame.manager.get("audio/sounds/bump.wav", Sound.class).play();

         */
    }

}

