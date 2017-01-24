package com.folkeg.mariobros.Item;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Mario;
import com.folkeg.mariobros.Sprites.Turtle;

/**
 * Created by folkeg on 5/16/16.
 */
public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(new TextureRegion(screen.getBasicAtlas().findRegion("mushroom"), 0, 0, 16, 16));
        velocity = new Vector2(0.6f, 0);
    }

    @Override
    public void update(float dt) {
        super.update(dt);

        //set pos to the moving body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        //set speed
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }

    @Override
    public void defineItem() {

        //set Bodydef
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
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
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle &&
                ((Turtle) enemy).getCurrentState() == Turtle.State.MOVING_SHELL){
            killed();
        }
        else{
            reverseVelocity(true, false);
        }
    }

    public void killed(){
        Filter filter = new Filter();
        filter.categoryBits = MarioBros.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        MarioBros.assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    //after use of Mashroom, setToDestroy the body
    @Override
    public void use(Mario mario) {
        Hud.addScore(500);
        destroy();
        mario.grow();
    }

}
