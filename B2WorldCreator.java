package com.mygdx.game;


import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;

public class B2WorldCreator {

    private Array<Zombie> zombie;

    public B2WorldCreator(PlayState screen){

        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        //crea il ground

        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MyGame.PPM, (rect.getY() + rect.getHeight() / 2) / MyGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MyGame.PPM, rect.getHeight() / 2 / MyGame.PPM);
            fdef.shape = shape;
            body.createFixture(fdef);
        }

        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bdef.type = BodyDef.BodyType.StaticBody;
            bdef.position.set((rect.getX() + rect.getWidth() / 2) / MyGame.PPM, (rect.getY() + rect.getHeight() / 2) / MyGame.PPM);

            body = world.createBody(bdef);

            shape.setAsBox(rect.getWidth() / 2 / MyGame.PPM, rect.getHeight() / 2 / MyGame.PPM);
            fdef.shape = shape;
            fdef.filter.categoryBits = MyGame.OBJECT_BIT;
            body.createFixture(fdef);
        }

        zombie = new Array<Zombie>();

        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            zombie.add(new Zombie(screen, rect.getX() / MyGame.PPM, rect.getY() / MyGame.PPM));
        }

    }

    public Array<Zombie> getzombie() {
        return zombie;
    }

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(zombie);
        return enemies;
    }

}
