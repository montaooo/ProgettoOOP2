package com.mygdx.game;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Options implements Screen {


    private Viewport viewport;
    private Stage stage;
    private Game game;
    private Label titleLabel;
    private Label volumeMusicLabel;
    private Label volumeSoundLabel;
    private Label musicOnOffLabel;
    private Label soundONOffLabel;

    public Options (Game game) {
        this.game = game;

        viewport = new FitViewport(MyGame.V_WIDTH, MyGame.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MyGame) game).batch);

    }

    @Override
    public void show() {
        final AppPreferences preferences = new AppPreferences();
        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
        Skin skin = new Skin(Gdx.files.internal("skin/uiskin.json"));


        Table table = new Table();
        table.setFillParent(true);
        stage.addActor(table);

        titleLabel = new Label("OPTIONS", font);
        volumeMusicLabel = new Label("Music Volume", font);
        table.add(titleLabel).colspan(10);
        table.row();
        table.add(volumeMusicLabel).left();
        final Slider slider = new Slider(0.1f,1f,0.1f,false, skin);
        slider.setValue(preferences.getMusicVolume());
        table.add(slider).expandX();
        table.row();
        Label returnLabel = new Label ("Press back to return", font);
        table.add(returnLabel).left();

        slider.addListener(new ClickListener(){
           public void clicked(InputEvent event, float x, float y){
                MyGame.getMusic().setVolume(slider.getValue());
                preferences.setMusicVolume(slider.getValue());
           }
        });

        Gdx.input.setInputProcessor(stage);
    }

    @Override
    public void render(float delta) {
        if(Gdx.input.isKeyPressed(Input.Keys.BACKSPACE)){
            game.setScreen(new IntroScreen((MyGame) game));
            dispose();
        }

        stage.act(Math.min(Gdx.graphics.getDeltaTime(), 1 / 30f));
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        //stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
