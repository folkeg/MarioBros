package com.folkeg.mariobros.Sprites;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;

/**
 * Created by folkeg on 5/24/16.
 */
public class StingTurtle extends Enemy {
    private float stateTimer;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean isDestroyed;
    private boolean toBeDestroyed;

    public StingTurtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++) {
            frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Big"), i * 16, 0, 16, 16));
        }
        walkAnimation = new Animation(0.2f, frames);
        setBounds(getX(),getY(),16 / MarioBros.PPM, 16 / MarioBros.PPM);
        stateTimer = 0;
        toBeDestroyed = false;
        isDestroyed = false;
    }

    @Override

    protected void defineEnemy() {

    }

    @Override
    public void onHeadHit(Mario mario) {

    }

    @Override
    public void update(float dt) {

    }

    @Override
    public void onEnemyHit(Enemy enemy) {

    }

    @Override
    public void killed() {

    }
}
