package com.folkeg.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.folkeg.mariobros.Item.Item;
import com.folkeg.mariobros.Screens.PlayScreen;

/**
 * Created by folkeg on 5/14/16.
 */

public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;
    public Body body;
    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x, y);
        defineEnemy();
        velocity = new Vector2(0.4f, 0);
        body.setActive(false);
    }


    protected abstract void defineEnemy();
    public abstract void onHeadHit(Mario mario);
    public abstract void update(float dt);
    public abstract void onEnemyHit(Enemy enemy);
    public abstract void killed();

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
