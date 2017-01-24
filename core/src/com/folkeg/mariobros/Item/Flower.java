package com.folkeg.mariobros.Item;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.utils.Array;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Mario;

/**
 * Created by folkeg on 5/19/16.
 */
public class Flower extends Item {
    private float startTimer;
    private Animation flowerAnimation;
    private Array<TextureRegion> frames;

    public Flower(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Flower"), 0, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Flower"), 16, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Flower"), 32, 0, 16, 16));
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Flower"), 48, 0, 16, 16));
        flowerAnimation = new Animation(0.4f, frames);
        startTimer = 0;
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
    }

    public TextureRegion getFrame(float dt){
        return flowerAnimation.getKeyFrame(startTimer, true);
    }

    @Override
    public void update(float dt) {
        startTimer += dt;
        super.update(dt);
        setRegion(getFrame(dt));
    }

    @Override
    public void defineItem() {
        //set Bodydef
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.StaticBody;
        body = world.createBody(bodyDef);

        //set Fixturedef
        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fixtureDef.shape = shape;

        fixtureDef.filter.categoryBits = MarioBros.ITEM_BIT;

        //set which type of objects enemy can interact
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.BRICK_BIT | MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT | MarioBros.ITEM_BIT |
                MarioBros.ENEMY_BIT | MarioBros.COIN_BIT;

        //set userdata to be used in contact listener
        body.createFixture(fixtureDef).setUserData(this);
    }

    @Override
    public void use(Mario mario) {
        Hud.addScore(500);
        destroy();
        mario.secondGrow();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {

    }
}
