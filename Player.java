package com.mygdx.game;

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
    public enum State {JUMPING, STANDING, RUNNING, FALLING, DEAD };
    public State currentState;
    public State previousState;

    public World world;
    public Body b2body;

    private TextureRegion playerStand;
    //private Animation playerRun;
    private TextureRegion playerJump;
    private TextureRegion playerDead;
    private TextureRegion playerRun;

    private float stateTimer;
    private boolean runningRight;
    private boolean timeToRedefinePlayer;
    private boolean playerIsDead;
    private PlayState screen;

    private Array<FireBall> fireballs;

    public Player(PlayState screen){

        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();

        /*

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("player"), i * 203, 18, 46, 48));
        playerRun = new Animation(0.1f, frames);

         */

        playerRun = new TextureRegion(screen.getAtlas().findRegion("player"), 203, 18, 46, 48);

        frames.clear();

        playerJump = new TextureRegion(screen.getAtlas().findRegion("player"), 265, 68, 45, 55);

        playerStand = new TextureRegion(screen.getAtlas().findRegion("player"), 14, 19, 40, 48);

        playerDead = new TextureRegion(screen.getAtlas().findRegion("player"), 14, 19, 40, 48);

        definePlayer();

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

        currentState = getState();
        TextureRegion region;

        switch(currentState){
            case FALLING:
            case DEAD:
                region = playerDead;
                break;
            case JUMPING:
                region = playerJump;
                break;
            case RUNNING:
                region = playerRun;
                //region = (TextureRegion) playerRun.getKeyFrame(stateTimer, true);
                break;
            case STANDING:
            default:
                region = playerStand;
                break;
        }

        if((b2body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        }

        else if((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()){
            region.flip(true, false);
            runningRight = true;
        }

        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;

    }

    public State getState(){

        if(playerIsDead)
            return State.DEAD;
        else if((b2body.getLinearVelocity().y > 0 && currentState == State.JUMPING) || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else if(b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else
            return State.STANDING;

    }


    public void die() {

        if (!isDead()) {

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
        if (currentState != State.JUMPING ) {
            b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    public void hit(Enemy enemy){

        die();

    }

    public void fall(World world){

        die();

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
                MyGame.ENEMY_BIT |
                MyGame.OBJECT_BIT;

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
                MyGame.ENEMY_BIT |
                MyGame.OBJECT_BIT;


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
        fireballs.add(new FireBall(screen, b2body.getPosition().x, b2body.getPosition().y, runningRight));
    }



    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireballs)
            ball.draw(batch);
    }

}
