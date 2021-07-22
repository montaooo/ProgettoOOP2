package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.MyGame;

public class IntroScreen extends State {

    public MyGame game;

    private Texture background;
    private Texture playBtn;

    protected IntroScreen(GameStateManager gsm) {
            super(gsm);
            cam.setToOrtho(false, MyGame.V_WIDTH / 2, MyGame.V_HEIGHT / 2);
            background = new Texture("bg.png");
            playBtn = new Texture("playbtn.png");

    }


    @Override
    public void handleInput() {
        if(Gdx.input.justTouched()){
           // gsm.set(new PlayState(gsm));
        }
    }

    @Override
    public void update(float dt) {
        handleInput();
    }

    @Override
    public void render(SpriteBatch sb) {
        sb.setProjectionMatrix(cam.combined);
        sb.begin();
        sb.draw(background, 0,0);
        sb.draw(playBtn, cam.position.x - playBtn.getWidth() / 2, cam.position.y);
        sb.end();
    }

    @Override
    public void dispose() {
        background.dispose();
        playBtn.dispose();
        System.out.println("Menu State Disposed");
    }
}
