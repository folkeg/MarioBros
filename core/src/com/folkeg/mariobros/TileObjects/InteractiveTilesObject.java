package com.folkeg.mariobros.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/11/16.
 */
public abstract class InteractiveTilesObject {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tiledMapTile;
    protected Rectangle rect;
    protected Body body;
    protected Fixture fixture;
    protected FixtureDef fixtureDef;
    protected PlayScreen screen;
    protected MapObject object;

    public InteractiveTilesObject(PlayScreen screen, MapObject object){
        this.object = object;
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.rect = ((RectangleMapObject)object).getRectangle();

        BodyDef bodyDef = new BodyDef();
        fixtureDef = new FixtureDef();
        PolygonShape shape = new PolygonShape();

        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set((rect.getX() + rect.getWidth() / 2) / MarioBros.PPM,
                (rect.getY() + rect.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bodyDef);

        shape.setAsBox((rect.getWidth() / 2) / MarioBros.PPM,
                (rect.getHeight() / 2) / MarioBros.PPM);
        fixtureDef.shape = shape;
        fixture = body.createFixture(fixtureDef);

    }

    public abstract void onHeadHit(Mario mario);
    public abstract MapObject getObject();

    public void setCategoryFilter(short filterBit){
        Filter filter = new Filter();
        filter.categoryBits = filterBit;
        fixture.setFilterData(filter);
    }

    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * MarioBros.PPM / 16),
                (int)(body.getPosition().y * MarioBros.PPM / 16));
    }
}
