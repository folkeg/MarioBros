package com.folkeg.mariobros.TileObjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Vector2;
import com.folkeg.mariobros.Item.Flower;
import com.folkeg.mariobros.Item.ItemDef;
import com.folkeg.mariobros.Item.Mushroom;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/11/16.
 */
public class Coin extends InteractiveTilesObject {
    private TiledMapTileSet tileSet;
    private final int BLANK_COIN = 28;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tileSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COIN_BIT);
    }
    @Override
    public void onHeadHit(Mario mario) {
        Gdx.app.log("Coin", "Collision");
        if(getCell().getTile().getId() == BLANK_COIN){
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        }
        else {
            if (object.getProperties().containsKey("mushroom")) {
                screen.addSpawnItems(new ItemDef(new Vector2(body.getPosition().x,
                        body.getPosition().y + 16 / MarioBros.PPM),
                        mario.isBigMario() ? Flower.class : Mushroom.class));
                MarioBros.assetManager.get("audio/sounds/powerup_spawn.wav", Sound.class).play();
            } else {
                MarioBros.assetManager.get("audio/sounds/coin.wav", Sound.class).play();
            }
            getCell().setTile(tileSet.getTile(BLANK_COIN));
        }
        Hud.addScore(100);
    }

    @Override
    public MapObject getObject() {
        return null;
    }
}
