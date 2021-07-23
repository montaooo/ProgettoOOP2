package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;

public class Zombie extends Enemy {

    public enum State {WALKING, DEAD}
    public State currentState;
    public State previousState;
    private float stateTime;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean setToDestroy;
    private boolean destroyed;


    public Zombie(PlayState screen, float x, float y) {

        super(screen, x, y);
        frames = new Array<TextureRegion>();

        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("zombie"), i - 8, 75, 40, 55));
        walkAnimation = new Animation(0.1f, frames);

        currentState = previousState = State.WALKING;
        destroyed = false;
        setToDestroy = false;
        stateTime = 0;

        setBounds(getX(), getY(), 16 / MyGame.PPM, 24 / MyGame.PPM);

    }

    @Override
    protected void defineEnemy() {

        BodyDef bdef = new BodyDef();
        bdef.position.set(getX(), getY());
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MyGame.PPM);
        fdef.filter.categoryBits = MyGame.ENEMY_BIT;
        fdef.filter.maskBits = MyGame.GROUND_BIT |
                MyGame.ENEMY_BIT |
                MyGame.OBJECT_BIT |
                MyGame.FIREBALL_BIT |
                MyGame.PLAYER_BIT;

        fdef.shape = shape;
        b2body.createFixture(fdef).setUserData(this);

    }

    public TextureRegion getFrameEnemy(float dt){

        TextureRegion region;

        switch (currentState){
            case DEAD:
            case WALKING:
            default:
                region = (TextureRegion) walkAnimation.getKeyFrame(stateTime, true);
                break;
        }

        if(velocity.x > 0 && !region.isFlipX()){
            region.flip(true, false);
        }
        if(velocity.x < 0 && region.isFlipX()){
            region.flip(true, false);
        }
        stateTime = currentState == previousState ? stateTime + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void update(float dt) {

        setRegion(getFrameEnemy(dt));

        stateTime += dt;

        if(setToDestroy && !destroyed){

            world.destroyBody(b2body);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("zombie"), 1, 75, 40, 55));
            stateTime = 0;

        }

        else if(!destroyed) {
            b2body.setLinearVelocity(velocity);
            setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - getHeight() / 2);
            setRegion((TextureRegion) walkAnimation.getKeyFrame(stateTime, true));
        }

        setPosition(b2body.getPosition().x - getWidth() / 2, b2body.getPosition().y - 8 /MyGame.PPM);
        b2body.setLinearVelocity(velocity);

    }

    public void hitByFire(FireBall fireBall){

        zombieDie();

    }

    @Override
    public void hitByEnemy(Enemy enemy) {
        reverseVelocity(true, false);
    }

    public void zombieDie(){

        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.maskBits = MyGame.NOTHING_BIT;

        for(Fixture fixture : b2body.getFixtureList())
            fixture.setFilterData(filter);

        b2body.applyLinearImpulse(new Vector2(0, 5f), b2body.getWorldCenter(), true);

    }

}


