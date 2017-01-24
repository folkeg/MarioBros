package com.folkeg.mariobros.Sprites;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Screens.PlayScreen;

/**
 * Created by folkeg on 5/14/16.
 */

public class Gomba extends Enemy {

    private float stateTimer;
    private Animation walkAnimation;
    private Array<TextureRegion> frames;
    private boolean isDestroyed;
    private boolean toBeDestroyed;


    public Gomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 2; i++){
            //set the region that goomba is walking
            frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("goomba"), i * 16, 0, 16, 16));
        }

        //give animation chaning speed and its all frames
        walkAnimation = new Animation(0.4f, frames);

        //set the fixture size and initial pos in the screen
        setBounds(getX(),getY(),16 / MarioBros.PPM, 16 / MarioBros.PPM);
        stateTimer = 0;
        toBeDestroyed = false;
        isDestroyed = false;
    }

    public void update(float dt){
        stateTimer += dt;
        velocity.y = body.getLinearVelocity().y;
        if(toBeDestroyed && !isDestroyed){

            //setToDestroy Box2D body
            world.destroyBody(body);
            isDestroyed = true;

            //set to the region that goomba is hit, since only one frame do not need animation
            setRegion(new TextureRegion(screen.getBasicAtlas().findRegion("goomba"), 32, 0, 16, 16));
            stateTimer = 0;
        }
        else if (!isDestroyed){

            //set body's moving speed
            body.setLinearVelocity(velocity);

            //set texture position to the moving Box2D body
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

            //set Animation to walkAnimation and loop it
            setRegion(walkAnimation.getKeyFrame(stateTimer, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_BIT;
        //set which type of objects enemy can interact
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT |
                MarioBros.MARIO_BIT | MarioBros.ITEM_BIT |
                MarioBros.FIREBALL_BIT;

        fixtureDef.shape = shape;

        //set userdata to be used in contact listener
        body.createFixture(fixtureDef).setUserData(this);

        //create fixturedef "head" to make collision happen when Mario hit goomba's head
        PolygonShape head = new PolygonShape();
        Vector2[] vertice = new Vector2[4];
        vertice[0] = new Vector2(-3, 8).scl(1 / MarioBros.PPM);
        vertice[1] = new Vector2(3, 8).scl(1 / MarioBros.PPM);
        vertice[2] = new Vector2(-3, 3).scl(1 / MarioBros.PPM);
        vertice[3] = new Vector2(3, 3).scl(1 / MarioBros.PPM);
        head.set(vertice);

        fixtureDef.shape = head;
        fixtureDef.filter.categoryBits = MarioBros.ENEMY_HEAD_BIT;

        //set rebounds when the fixturedef is being hit
        fixtureDef.restitution = 1f;
        body.createFixture(fixtureDef).setUserData(this);

    }

    public void draw(Batch batch){
        if(!isDestroyed || stateTimer < 1){
            super.draw(batch);
        }
    }
    @Override
    public void onHeadHit(Mario mario) {
        toBeDestroyed = true;
        Hud.addScore(500);
        MarioBros.assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
    }

    @Override
    public void onEnemyHit(Enemy enemy) {
        if(enemy instanceof Turtle &&
                ((Turtle) enemy).getCurrentState() == Turtle.State.MOVING_SHELL){
            killed();
            Hud.addScore(500);
        }
        else{
            reverseVelocity(true, false);
        }
    }

    @Override
    public void killed(){
        Filter filter = new Filter();
        filter.categoryBits = MarioBros.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        MarioBros.assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
    }
}
