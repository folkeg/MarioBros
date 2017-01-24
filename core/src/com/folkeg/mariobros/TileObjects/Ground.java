package com.folkeg.mariobros.TileObjects;

import com.badlogic.gdx.maps.MapObject;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/24/16.
 */
public class Ground extends InteractiveTilesObject {
    public Ground(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.GROUND_BIT);
    }

    @Override
    public void onHeadHit(Mario mario) {
    }

    @Override
    public MapObject getObject() {
        return object;
    }
}
