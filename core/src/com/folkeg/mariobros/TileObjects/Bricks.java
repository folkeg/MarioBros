package com.folkeg.mariobros.TileObjects;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.Gdx;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/11/16.
 */
public class Bricks extends InteractiveTilesObject {
    public Bricks(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.BRICK_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
        if(mario.isBigMario() || mario.isFireMario()) {
            Gdx.app.log("Brick", "Collision");
            setCategoryFilter(MarioBros.DESTROYED_BIT);
            getCell().setTile(null);
            Hud.addScore(200);
            MarioBros.assetManager.get("audio/sounds/breakblock.wav", Sound.class).play();
        }
        else{
            MarioBros.assetManager.get("audio/sounds/bump.wav", Sound.class).play();
        }
    }

    @Override
    public MapObject getObject() {
        return null;
    }
}
