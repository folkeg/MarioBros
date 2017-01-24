package com.folkeg.mariobros.FireBalls;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;
import com.folkeg.mariobros.Sprites.Enemy;

/**
 * Created by folkeg on 5/22/16.
 */
public class FireBall extends Sprite{
    private World world;
    private PlayScreen screen;
    private Body body;
    private Animation fireAnimation;
    private Array<TextureRegion> frames;
    private boolean fireRight;
    private boolean toBeDestroyed;
    private boolean isDestroyed;
    private float stateTimer;

    public FireBall(PlayScreen screen, float x, float y, boolean fireRight){
        this.fireRight = fireRight;
        this.screen = screen;
        world = screen.getWorld();
        frames = new Array<TextureRegion>();
        for(int i = 0; i < 4; i++){
            //set the region of fireBall
            frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("fireball"), i * 8, 0, 8, 8));
        }
        fireAnimation = new Animation(0.2f, frames);

        //set initial location, width and height of fireball and initial frame of animation
        setBounds(x, y, 6 / MarioBros.PPM, 6 / MarioBros.PPM);
        setRegion(fireAnimation.getKeyFrame(0));

        defineFireBall();

        toBeDestroyed = false;
        isDestroyed = false;
        stateTimer = 0;
    }

    public void defineFireBall(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(fireRight ? getX() + 12 / MarioBros.PPM :
                getX() - 12 / MarioBros.PPM, getY());
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(3 / MarioBros.PPM);
        fixtureDef.filter.categoryBits = MarioBros.FIREBALL_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT | MarioBros.OBJECT_BIT |
                MarioBros.ITEM_BIT;
        fixtureDef.shape = shape;
        fixtureDef.restitution = 0.8f;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(this);
        body.setLinearVelocity(new Vector2(fireRight ? 2 : -2, 2));
    }

    public void update(float dt){
        stateTimer += dt;

        //set texture position to the moving Box2D body
        setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);

        //set Animation to fireAnimation and loop it
        setRegion(fireAnimation.getKeyFrame(stateTimer, true));

        if((stateTimer > 3 || toBeDestroyed) && !isDestroyed){
            world.destroyBody(body);
            isDestroyed = true;
        }
        if(body.getLinearVelocity().y > 2f)
            body.setLinearVelocity(body.getLinearVelocity().x, 2f);

        //if there's no velocity of fireball, setToDestroy the fireball
        if((fireRight && body.getLinearVelocity().x < 0) ||
                (!fireRight && body.getLinearVelocity().x > 0))
            setToDestroy();
    }

    public void onEnemyHit(Enemy enemy){
        toBeDestroyed = true;
        enemy.killed();
    }

    public void setToDestroy(){
        toBeDestroyed = true;
    }

    public boolean isDestroyed(){
        return isDestroyed;
    }
}
