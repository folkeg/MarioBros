package com.folkeg.mariobros.Item;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/16/16.
 */
public abstract class Item extends Sprite {
    protected World world;
    private PlayScreen screen;
    protected Body body;
    private boolean toBeDestroyed;
    private boolean isDestroyed;
    protected Vector2 velocity;

    public Item(PlayScreen screen, float x, float y){
        this.screen = screen;
        world = screen.getWorld();

        //set initial position in the screen
        setPosition(x, y);

        //set size
        setBounds(getX(),getY(), 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        defineItem();
        toBeDestroyed = false;
        isDestroyed = false;

    }

    public void update(float dt){
        if(toBeDestroyed && !isDestroyed){
            world.destroyBody(body);
            isDestroyed = true;
        }
    }
    public abstract void defineItem();
    public abstract void use(Mario mario);
    public abstract void onEnemyHit(Enemy enemy);

    public void draw(Batch batch){
        if(!isDestroyed){
            super.draw(batch);
        }
    }

    public void destroy(){
        toBeDestroyed = true;
    }

    //when hit the wall, reverse the velocity
    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }
}
