package com.folkeg.mariobros.Sprites;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.folkeg.mariobros.FireBalls.FireBall;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Screens.PlayScreen;

/**
 * Created by folkeg on 5/11/16.
 */
public class Mario extends Sprite{
    public enum State { FALLING, RUNNING, STANDING,
        JUMPING, GROWING, SECONDGROWING, SHRINKING, DEAD};
    public State currentState;
    public State previousState;

    public World world;
    public Body body;
    public PlayScreen screen;

    private TextureRegion marioStand;
    private TextureRegion marioJump;
    private TextureRegion bigMarioJump;
    private TextureRegion bigMarioStand;
    private TextureRegion fireMarioJump;
    private TextureRegion fireMarioStand;
    private TextureRegion marioDead;

    private Animation marioRun;
    private Animation bigMarioRun;
    private Animation fireMarioRun;
    private Animation growingMario;
    private Animation firingMario;

    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsBigMario;
    private boolean marioIsFireMario;
    private boolean runGrowAnimation;
    private boolean runSecondGrowAnimation;
    private boolean timeToDefineBigMario;
    private boolean timeToRedefineMario;
    private boolean isMarioDead;

    private Array<FireBall> fireBalls;

    public Mario(PlayScreen screen){
        this.screen = screen;
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        //get texture region from running mario and set the animation
        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("little_mario"),
                    i * 16, 0, 16, 16));
        }
        marioRun = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"),
                    i * 16, 0, 16, 32));
        }
        bigMarioRun = new Animation(0.1f, frames);
        frames.clear();

        for(int i = 1; i < 4; i++){
            frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Fire"),
                    i * 16, 0, 16, 32));
        }
        fireMarioRun = new Animation(0.1f, frames);
        frames.clear();

        //get texture region from growing mario and set the animation
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growingMario = new Animation(0.2f, frames);
        frames.clear();

        //get texture region from firing mario and set the animation
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Fire"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAdvAtlas().findRegion("Fire"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        firingMario = new Animation(0.2f, frames);
        frames.clear();

        //get texture region from stand mario
        marioStand = new TextureRegion(screen.getBasicAtlas().findRegion("little_mario"), 0, 0, 16, 16);
        bigMarioStand = new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 0, 0, 16, 32);
        fireMarioStand = new TextureRegion(screen.getAdvAtlas().findRegion("Fire"), 0, 0, 16, 32);

        //get texture region from jump mario
        marioJump = new TextureRegion(screen.getBasicAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getBasicAtlas().findRegion("big_mario"), 80, 0, 16, 32);
        fireMarioJump = new TextureRegion(screen.getAdvAtlas().findRegion("Fire"), 80, 0, 16, 32);

        //get texture region from dead mario
        marioDead = new TextureRegion(screen.getBasicAtlas().findRegion("little_mario"), 96, 0, 16, 16);

        //define Mario in Box2D
        defineMario();

        //set initial values of Mario location, width and height and initial frame as marioStand
        setBounds(0, 0, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);

        //initial fireball array
        fireBalls = new Array<FireBall>();
    }

    public void update(float dt){

        //update sprite correspond with Box2D body position
        if(marioIsBigMario || marioIsFireMario){
            setPosition(body.getPosition().x - getWidth() / 2,
                    body.getPosition().y - getHeight() / 2 - 6 /MarioBros.PPM);
        }
        else {
            setPosition(body.getPosition().x - getWidth() / 2, body.getPosition().y - getHeight() / 2);
        }

        //update sprite with correct frames depending on current Mario State
        setRegion(getFrame(dt));
        if(timeToDefineBigMario){
            defineBigMario();
        }
        if(timeToRedefineMario){
            redefineMario();
        }

        //update fireball object position, animation and velocity
        //if destroyed, remove from fireballs array
        //why update here not the play screen?
        for(FireBall fireBall : fireBalls){
            fireBall.update(dt);
            if(fireBall.isDestroyed()){
                fireBalls.removeValue(fireBall, true);
            }
        }
    }

    //return animation region depend on current State
    public TextureRegion getFrame(float dt){
        currentState = getState();
        TextureRegion region;

        switch (currentState){
            case DEAD:
                region = marioDead;
                break;
            case SECONDGROWING:
                region = firingMario.getKeyFrame(stateTimer);

                //if animation ended, set animation false
                if(firingMario.isAnimationFinished(stateTimer)){
                    runSecondGrowAnimation = false;
                }
                break;
            case GROWING:
                region = growingMario.getKeyFrame(stateTimer);
                if(growingMario.isAnimationFinished(stateTimer)){
                    runGrowAnimation = false;
                }
                break;
            case JUMPING:
                if(marioIsBigMario){
                    region = bigMarioJump;
                }
                else if(marioIsFireMario){
                    region = fireMarioJump;
                }
                else{
                    region = marioJump;
                }
                break;
            case RUNNING:
                if(marioIsBigMario){
                    region = bigMarioRun.getKeyFrame(stateTimer, true);
                }
                else if(marioIsFireMario){
                    region = fireMarioRun.getKeyFrame(stateTimer, true);
                }
                else{
                    region = marioRun.getKeyFrame(stateTimer, true);
                }
                break;
            case FALLING:
            case STANDING:
            default:
                if(marioIsBigMario){
                    region = bigMarioStand;
                }
                else if(marioIsFireMario){
                    region = fireMarioStand;
                }
                else{
                    region = marioStand;
                }
                break;
        }

        //if mario is running left and texture is not flip to left, flip texture
        if ((body.getLinearVelocity().x < 0 || !runningRight) && !region.isFlipX()) {
            region.flip(true, false);
            runningRight = false;
        }
        //if mario is running right and texture is not flip to right, flip texture
        else if ((body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX()) {
            region.flip(true, false);
            runningRight = true;
        }

        //if current state is same as previous state, increase the timer
        //otherwise the state is changed, we need to reset the timer
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    //return the current state depend on different case
    public State getState(){
        if(isMarioDead)
            return State.DEAD;
        else if(runGrowAnimation)
            return State.GROWING;
        else if(runSecondGrowAnimation)
            return State.SECONDGROWING;

        //if Mario is jumping or if is just falling after jump
        else if(body.getLinearVelocity().y > 0 ||
                (body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if(body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if(body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    //set effects (boolean) when mario need to grow
    public void grow(){
        if(!isBigMario() || !isFireMario()) {
            runGrowAnimation = true;
            marioIsBigMario = true;
            timeToDefineBigMario = true;
            MarioBros.assetManager.get("audio/sounds/powerup.wav", Sound.class).play();

            // adjust Pixel to 16 * 32 instead 16 * 16
            setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        }
    }

    //set effects (boolean) when mario need to second grow
    public void secondGrow(){
        if(isBigMario()) {
            runSecondGrowAnimation = true;
            marioIsFireMario = true;
            marioIsBigMario = false;
            MarioBros.assetManager.get("audio/sounds/powerup.wav", Sound.class).play();
        }
    }

    //set effects when mario is dead
    public void die(){
        if(!isMarioDead){
            MarioBros.assetManager.get("audio/music/mario_music.ogg", Music.class).stop();
            MarioBros.assetManager.get("audio/sounds/mariodie.wav", Sound.class).play();
            isMarioDead = true;

            //set mario's maskBits to nothing when killed
            Filter filter = new Filter();
            filter.maskBits = MarioBros.NOTHING_BIT;
            for (Fixture fixture : body.getFixtureList()) {
                fixture.setFilterData(filter);
            }

            //apply an impulse when killed
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
        }
    }

    //set jump when mario is not jumping
    public void jump(){
        if(currentState != State.JUMPING){
            body.applyLinearImpulse(new Vector2(0, 4f), body.getWorldCenter(), true);
            currentState = State.JUMPING;
        }
    }

    //create fireball when mario need to fire
    public void fire(){
        fireBalls.add(new FireBall(screen, body.getPosition().x, body.getPosition().y,
                    runningRight ? true : false));
    }

    public void draw(Batch batch){
        super.draw(batch);
        for(FireBall ball : fireBalls){
            ball.draw(batch);
        }
    }

    //
    public void onHit(Enemy enemy){

        //if enemy is standing shell turtle, let it be moving shell
        if(enemy instanceof Turtle &&
                ((Turtle) enemy).getCurrentState() == Turtle.State.STANDING_SHELL){
            ((Turtle) enemy).setSpeed
                    (this.getX() <= enemy.getX() ? Turtle.KICK_RIGHT_SPEED : Turtle.KICK_LEFT_SPEED);
        }
        else {

            //FireMario is hit, change boolean type so that frames can be changed
            //Box2D body does not need to be changed
            if(marioIsFireMario){
                marioIsFireMario = false;
                marioIsBigMario = true;
                runSecondGrowAnimation = true;
                MarioBros.assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }

            //Big mario is hit, redefine to small mario (Body changed) and set bounds
            else if (marioIsBigMario) {
                marioIsBigMario = false;
                runGrowAnimation = true;
                timeToRedefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                MarioBros.assetManager.get("audio/sounds/powerdown.wav", Sound.class).play();
            }

            //small mario is hit, set it killed
            else {
                die();
            }
        }
    }

    public boolean isBigMario(){
        return marioIsBigMario;
    }

    public boolean isFireMario() {
        return marioIsFireMario;
    }

    public boolean isMarioDead(){
        return isMarioDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    public void defineBigMario(){

        //get the current position and setToDestroy the formal body,
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        // a little bit taller than before (Height)
        bodyDef.position.set(currentPosition.add(0, 10 / MarioBros.PPM));
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT | MarioBros.ENEMY_HEAD_BIT |
                MarioBros.OBJECT_BIT | MarioBros.ITEM_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        //create another circle shape and put it below the first shape
        shape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        body.createFixture(fixtureDef).setUserData(this);

        //create a line segment shape as mario's head
        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2, 6).scl(1 / MarioBros.PPM),
                new Vector2(2, 6).scl(1 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
        timeToDefineBigMario = false;
    }

    public void defineMario(){
        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT | MarioBros.ENEMY_HEAD_BIT |
                MarioBros.OBJECT_BIT | MarioBros.ITEM_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2, 6).scl(1 / MarioBros.PPM),
                new Vector2(2, 6).scl(1 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
    }

    public void redefineMario(){
        Vector2 currentPosition = body.getPosition();
        world.destroyBody(body);

        BodyDef bodyDef = new BodyDef();
        bodyDef.position.set(currentPosition);
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        body = world.createBody(bodyDef);

        FixtureDef fixtureDef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(6 / MarioBros.PPM);
        fixtureDef.filter.categoryBits = MarioBros.MARIO_BIT;
        fixtureDef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT | MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT | MarioBros.ENEMY_HEAD_BIT |
                MarioBros.OBJECT_BIT | MarioBros.ITEM_BIT;
        fixtureDef.shape = shape;
        body.createFixture(fixtureDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2, 6).scl(1 / MarioBros.PPM),
                new Vector2(2, 6).scl(1 / MarioBros.PPM));
        fixtureDef.filter.categoryBits = MarioBros.MARIO_HEAD_BIT;
        fixtureDef.shape = head;
        fixtureDef.isSensor = true;
        body.createFixture(fixtureDef).setUserData(this);
        timeToRedefineMario = false;
    }
}
