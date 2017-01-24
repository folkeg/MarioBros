package com.folkeg.mariobros.Item;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Created by folkeg on 5/16/16.
 */
public class ItemDef {
    public Vector2 position;
    public Class<?> type;

    public ItemDef(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }
}
