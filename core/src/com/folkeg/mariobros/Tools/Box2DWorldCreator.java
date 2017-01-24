package com.folkeg.mariobros.Tools;

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
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Turtle;
import com.folkeg.mariobros.TileObjects.Bricks;
import com.folkeg.mariobros.TileObjects.Coin;
import com.folkeg.mariobros.Sprites.Gomba;
import com.folkeg.mariobros.TileObjects.Ground;

/**
 * Created by folkeg on 5/11/16.
 */
public class Box2DWorldCreator {
    private Array<Gomba> gombas;
    private Array<Turtle> turtles;

    public Array<Enemy> getEnemies(){
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(gombas);
        enemies.addAll(turtles);
        return enemies;
    }

    public Box2DWorldCreator(PlayScreen screen){

        TiledMap map = screen.getMap();
        World world = screen.getWorld();

        FixtureDef fixtureDef = new FixtureDef();
        BodyDef bodyDef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        Body body;

        //create ground bodies/fixtures
        for(MapObject object : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            new Ground(screen, object);

        }

        //create pipes bodies/fixtures
        for(MapObject object : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            bodyDef.type = BodyDef.BodyType.StaticBody;
            bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bodyDef);

            shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM,
                    (rect.getHeight() / 2) / MarioBros.PPM);
            fixtureDef.shape = shape;
            body.createFixture(fixtureDef);
            fixtureDef.filter.categoryBits = MarioBros.OBJECT_BIT;
        }

        //create coins bodies/fixtures
        for(MapObject object : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){

            new Coin(screen, object);
        }

        //create bricks bodies/fixtures
        for(MapObject object : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){

            new Bricks(screen, object);
        }

        gombas = new Array<Gomba>();
        for(MapObject object : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            gombas.add(new Gomba(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }

        turtles = new Array<Turtle>();
        for(MapObject object : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            turtles.add(new Turtle(screen, rect.getX() / MarioBros.PPM, rect.getY() / MarioBros.PPM));
        }

    }
}
