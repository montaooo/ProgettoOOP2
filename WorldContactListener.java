package com.mygdx.game;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;

public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        int cDef = fixA.getFilterData().categoryBits | fixB.getFilterData().categoryBits;

        switch (cDef){

            case MyGame.ENEMY_BIT | MyGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Enemy)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MyGame.PLAYER_BIT | MyGame.ENEMY_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.PLAYER_BIT)
                    ((Player) fixA.getUserData()).hit((Enemy)fixB.getUserData());
                else
                    ((Player) fixB.getUserData()).hit((Enemy)fixA.getUserData());
                break;
            case MyGame.ENEMY_BIT:
                ((Enemy)fixA.getUserData()).hitByEnemy((Enemy)fixB.getUserData());
                ((Enemy)fixB.getUserData()).hitByEnemy((Enemy)fixA.getUserData());
                break;
            case MyGame.ITEM_BIT | MyGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.ITEM_BIT)
                    ((Item)fixA.getUserData()).reverseVelocity(true, false);
                else
                    ((Item)fixB.getUserData()).reverseVelocity(true, false);
                break;
            case MyGame.ITEM_BIT | MyGame.PLAYER_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.ITEM_BIT)
                    ((Item)fixA.getUserData()).use((Player) fixB.getUserData());
                else
                    ((Item)fixB.getUserData()).use((Player) fixA.getUserData());
                break;

            case MyGame.FIREBALL_BIT | MyGame.OBJECT_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.FIREBALL_BIT)
                    ((FireBall)fixA.getUserData()).setToDestroy();
                else
                    ((FireBall)fixB.getUserData()).setToDestroy();
                break;

            case MyGame.ENEMY_BIT | MyGame.FIREBALL_BIT:
                if(fixA.getFilterData().categoryBits == MyGame.ENEMY_BIT)
                    ((Enemy)fixA.getUserData()).hitByFire(((FireBall) fixB.getUserData()));
                else
                    ((Enemy)fixB.getUserData()).hitByFire(((FireBall) fixA.getUserData()));
                break;

        }
    }

    @Override
    public void endContact(Contact contact) {
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}