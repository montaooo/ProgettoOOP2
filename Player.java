package com.mygdx.game;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;


public class Player extends Sprite {
    public enum State { FALLING, JUMPING, STANDING, RUNNING, GROWING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion playerStand;
    private Animation playerRun;
    private TextureRegion playerJump;
    private TextureRegion playerDead;

    private float stateTimer;
    private boolean runningRight;
    private boolean timeToRedefinePlayer;
    private boolean playerIsDead;
    private PlayState screen;

    private Array<FireBall> fireballs;

    public Player(PlayState screen){
        //initialize default values
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        //get run animation frames and add them to marioRun Animation
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("player1"), i * 16, 0, 16, 16));
        playerRun = new Animation(0.1f, frames);

        frames.clear();

        //get jump animation frames and add them to marioJump Animation
        playerJump = new TextureRegion(screen.getAtlas().findRegion("player1"), 80, 0, 16, 16);
       // bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);

        //create texture region for mario standing
        playerStand = new TextureRegion(screen.getAtlas().findRegion("player1"), 0, 0, 16, 16);
        //bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        //create dead mario texture region
        playerDead = new TextureRegion(screen.getAtlas().findRegion("player1"), 96, 0, 16, 16);

        //define mario in Box2d
        definePlayer();

        //set initial values for marios location, width and height. And initial frame as marioStand.
        setBounds(0, 0, 16 / MyGame.PPM, 16 / MyGame.PPM);
        setRegion(playerStand);

        fireballs = new Array<FireBall>();

    }

    public void update(float dt){

        setRegion(getFramePlayer(dt));

        if (screen.getHud().isTimeUp() && !isDead()) {
            die();
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);

        if(timeToRedefinePlayer)
            redefinePlayer();

        for(FireBall  ball : fireballs) {
            ball.update(dt);
            if(ball.isDestroyed())
                fireballs.removeValue(ball, true);
        }

    }


    public TextureRegion getFramePlayer(float dt){
        //get marios current state. ie. jumping, running, standing...
        currentState = getState();

        TextureRegion region;

        //depending on the state, get corresponding animation keyFrame.
        switch(currentState){
            case DEAD:
                region = playerDead;
                break;
            case JUMPING:
                region = playerJump;
                break;
            case RUNNING:
                region = (TextureRegion) playerRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = playerStand;
                break;
        }

        //if mario is running left and the texture isnt facing left... flip it.
        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        //if mario is running right and the texture isnt facing right... flip it.
        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        //if the current state is the same as the previous state increase the state timer.
        //otherwise the state has changed and we need to reset timer.
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        //update previous state
        previousState = currentState;
        //return our final adjusted frame
        return region;

    }



    public State getState(){
        //Test to Box2D for velocity on the X and Y-Axis
        //if mario is going positive in Y-Axis he is jumping... or if he just jumped and is falling remain in jump state
        if(playerIsDead)
            return State.DEAD;

        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
            //if negative in Y-Axis mario is falling
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
            //if mario is positive or negative in the X axis he is running
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
            //if none of these return then he must be standing
        else
            return State.STANDING;
    }

    public void die() {

        if (!isDead()) {

            MyGame.manager.get("audio/music/mario_music.ogg", Music.class).stop();
            MyGame.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
            playerIsDead = true;
            Filter filter = new Filter();
            filter.maskBits = MyGame.NOTHING_BIT;

            for (Fixture fixture : b2body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
        }
    }

    public boolean isDead(){
        return playerIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }


    public void jump(){
        if ( currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }


    public void hit(Enemy enemy){

        if(enemy instanceof Zombie && ((Zombie) enemy).currentState == Zombie.State.STANDING_SHELL)
            ((Zombie) enemy).kick(enemy.b2body.getPosition().x > b2body.getPosition().x ? Zombie.KICK_RIGHT : Zombie.KICK_LEFT);
        else {
                die();
            }
        }



    public void redefinePlayer(){
        Vector2 position = b2body.getPosition();
        world.destroyBody(b2body);

        BodyDef bdef = new BodyDef();
        bdef.position.set(position);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGame.PPM);
        fdef.filter.categoryBits = MyGame.PLAYER_BIT;
        fdef.filter.maskBits = MyGame.GROUND_BIT |
                MyGame.COIN_BIT |
                MyGame.BRICK_BIT |
                MyGame.ENEMY_BIT |
                MyGame.OBJECT_BIT |
                MyGame.ENEMY_HEAD_BIT |
                MyGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MyGame.PPM, 6 / MyGame.PPM), new Vector2(2 / MyGame.PPM, 6 / MyGame.PPM));
        fdef.filter.categoryBits = MyGame.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);

        timeToRedefinePlayer = false;

    }



    public void definePlayer(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32 / MyGame.PPM, 32 / MyGame.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGame.PPM);
        fdef.filter.categoryBits = MyGame.PLAYER_BIT;
        fdef.filter.maskBits = MyGame.GROUND_BIT |
                MyGame.COIN_BIT |
                MyGame.BRICK_BIT |
                MyGame.ENEMY_BIT |
                MyGame.OBJECT_BIT |
                MyGame.ENEMY_HEAD_BIT |
                MyGame.ITEM_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MyGame.PPM, 6 / MyGame.PPM), new Vector2(2 / MyGame.PPM, 6 / MyGame.PPM));
        fdef.filter.categoryBits = MyGame.PLAYER_HEAD_BIT;
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData(this);
    }


    public void fire(){
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight ? true : false));
    }



    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }
}
