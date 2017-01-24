package com.folkeg.mariobros.Sprites;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
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
 * Created by folkeg on 5/18/16.
 */
public class Turtle extends Enemy {

    public static int KICK_LEFT_SPEED = -2;
    public static int KICK_RIGHT_SPEED = 2;
    public enum State {WALKING, STANDING_SHELL, MOVING_SHELL,DEAD};
    private float stateTimer;
    private Animation walkAnimation;
    private Animation shellAnimation;
    private Array<TextureRegion> frames;
    private State currentState;
    private State previousState;

    public Turtle(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();

        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("turtle"), 16, 0, 16, 24));
        walkAnimation = new Animation(0.2f, frames);
        frames.clear();

        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("turtle"), 64, 0, 16, 24));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("turtle"), 80, 0, 16, 24));
        shellAnimation = new Animation(0.4f, frames);
        frames.clear();

        currentState = previousState = State.WALKING;
        setBounds(0, 0, 16 / MarioBros.PPM, 24 / MarioBros.PPM);
        stateTimer = 0;
    }

    @Override
    protected void defineEnemy() {
        BodyDef bodyDef = new BodyDef();
        FixtureDef fixtureDef = new FixtureDef();

        bodyDef.position.set(getX(),getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

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

    @Override
    public void onHeadHit(Mario mario) {
        if(currentState != State.STANDING_SHELL){
            currentState = State.STANDING_SHELL;
            velocity.x = 0;
        }
        else{
            setSpeed(mario.getX() <= this.getX() ? KICK_RIGHT_SPEED : KICK_LEFT_SPEED);
        }
    }

    public void setSpeed(int speed){
        velocity.x = speed;
        currentState = State.MOVING_SHELL;
    }

    public State getCurrentState(){
        return currentState;
    }

    @Override
    public void update(float dt) {
        setRegion(getFrame(dt));
        setPosition(body.getPosition().x - getWidth() / 2,
                body.getPosition().y - 8 / MarioBros.PPM);
        velocity.y = body.getLinearVelocity().y;

        if(currentState == State.STANDING_SHELL && stateTimer > 5){
            currentState = State.WALKING;
            velocity.x = 0.5f;
        }
        /*
        if(currentState == State.DEAD){
            world.destroyBody(body);
        }*/

        setPosition(body.getPosition().x - getWidth() / 2,
                body.getPosition().y - 8 / MarioBros.PPM);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity);
    }

    public TextureRegion getFrame(float dt){
        TextureRegion region;
        switch (currentState){
            case STANDING_SHELL:
                if(stateTimer < 2) {
                    region = shellAnimation.getKeyFrame(0, true);
                }
                else {
                    region = shellAnimation.getKeyFrame(stateTimer, true);
                }
                break;
            case DEAD:
            case MOVING_SHELL:
                region = shellAnimation.getKeyFrame(0, true);
                break;
            case WALKING:
            default:
                region = walkAnimation.getKeyFrame(stateTimer, true);
                break;
        }
        if (velocity.x > 0 && !region.isFlipX()) {
            region.flip(true, false);
        } else if (velocity.x < 0 && region.isFlipX()) {
            region.flip(true, false);
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    @Override
    public void onEnemyHit(Enemy enemy) {

        if(currentState != State.MOVING_SHELL){
            if(enemy instanceof Turtle && ((Turtle)enemy).currentState == State.MOVING_SHELL) {
                killed();
            }
            else{
                reverseVelocity(true, false);
            }
        }
        else {
            //when moving shell hit moving shell or standing shell, both destroyed
            if(enemy instanceof Turtle && (((Turtle)enemy).currentState == State.MOVING_SHELL ||
                    ((Turtle)enemy).currentState == State.STANDING_SHELL)){
                //Cannot access
                Gdx.app.log("Moving Shell Hit", "Standing Shell");
                killed();
                ((Turtle) enemy).killed();
            }
        }
    }

    @Override
    public void killed(){
        currentState = State.DEAD;
        Filter filter = new Filter();
        filter.categoryBits = MarioBros.NOTHING_BIT;
        for (Fixture fixture : body.getFixtureList())
            fixture.setFilterData(filter);
        body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        MarioBros.assetManager.get("audio/sounds/stomp.wav", Sound.class).play();
        Hud.addScore(800);
    }
}
