package com.folkeg.mariobros.Screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.folkeg.mariobros.Item.Flower;
import com.folkeg.mariobros.Item.Item;
import com.folkeg.mariobros.Item.ItemDef;
import com.folkeg.mariobros.Item.Mushroom;
import com.folkeg.mariobros.MarioBros;
import com.folkeg.mariobros.Scene.Hud;
import com.folkeg.mariobros.Sprites.Enemy;
import com.folkeg.mariobros.Sprites.Mario;
import com.folkeg.mariobros.Tools.Box2DWorldCreator;
import com.folkeg.mariobros.Tools.WorldContactListener;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by folkeg on 5/9/16.
 */
public class PlayScreen implements Screen {

    private MarioBros marioBros;
    private TextureAtlas basicAtlas;
    private TextureAtlas advAtlas;

    // basic play screen variables
    private OrthographicCamera camera;
    private Viewport viewport;
    private Hud hud;

    // Tiled map variables
    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer box2DDebugRenderer;
    private Box2DWorldCreator box2DWorldCreator;

    //Sprites
    private Mario player;

    //Item
    private Array<Item> items;
    private LinkedBlockingQueue<ItemDef> itemsToSpawn;

    private Music music;

    public PlayScreen(MarioBros marioBros){
        this.marioBros = marioBros;

        // if load many objects use gdx asset manager
        basicAtlas = new TextureAtlas("Mario_and_Enemies.pack");
        advAtlas = new TextureAtlas("Mario_Enemies p2.pack");

        // create camera used to follow mario through the world
        camera = new OrthographicCamera();

        // create a FitViewport to maintain virtual aspect despite screen size
        viewport = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM,
                MarioBros.V_HEIGHT / MarioBros.PPM, camera);

        // create game HUD for scores/timers/levels info
        hud = new Hud(marioBros.batch);

        // load map and setup map renderer
        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1 / MarioBros.PPM);

        // set camera to be centered correctly at the start of the map
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0 );

        //create Box2D world, set no gravity in X, -10 in Y and allow body to sleep
        world = new World(new Vector2(0, -10), true);

        //allow for debug lines of Box2D world
        box2DDebugRenderer = new Box2DDebugRenderer();

        //create box2D objects from tmx file
        box2DWorldCreator = new Box2DWorldCreator(this);

        // create Mario player
        player = new Mario(this);

        //create contactListener between Box2D objects
        world.setContactListener(new WorldContactListener());

        //load and play background music
        music = MarioBros.assetManager.get("audio/music/mario_music.ogg", Music.class);
        music.setLooping(true);
        music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingQueue<ItemDef>();
    }


    public void addSpawnItems(ItemDef itemDef){
        itemsToSpawn.add(itemDef);
    }

    public void handleSpawningItems(){
        if(!itemsToSpawn.isEmpty()){
            ItemDef itemDef = itemsToSpawn.poll();
            if(itemDef.type == Mushroom.class){
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
            else if(itemDef.type == Flower.class){
                items.add(new Flower(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    //handle keyboard input of player moving left, right, jump and fire
    public void handleInput(float dt){
        if(player.currentState != Mario.State.DEAD) {
            if(Gdx.input.isKeyJustPressed(Input.Keys.S)){
                player.fire();
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                player.jump();
            }

            //when speed not reach 1.5f, set linear impulse
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.body.getLinearVelocity().x <= 1.5f) {
                player.body.applyLinearImpulse(new Vector2(0.1f, 0), player.body.getWorldCenter(), true);
            }
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.body.getLinearVelocity().x >= -1.5f) {
                player.body.applyLinearImpulse(new Vector2(-0.1f, 0), player.body.getWorldCenter(), true);
            }
        }
    }

    //update every object, input and camera
    public void update(float dt){

        //handle user input
        handleInput(dt);

        //handle item object creation
        handleSpawningItems();

        //takes 1 step in the physics simulation(60 times per second)
        world.step(1 / 60f, 6, 2);

        //attach camera to player x position
        if(player.currentState != Mario.State.DEAD) {
            camera.position.x = player.body.getPosition().x;
        }
        //camera.position.x += dt / 3;
        camera.update();

        //if(camera.position.x <= player.body.getPosition().x){
            //player.die();
        //}

        //tell renderer to draw only the camera can see
        renderer.setView(camera);

        //update mario position, redefine, animation
        player.update(dt);

        //update time count in Hud
        hud.update(dt);

        //update enemy setToDestroy status, animation, position and velocity
        for(Enemy enemy : box2DWorldCreator.getEnemies()){
            enemy.update(dt);

            //set enemy active when player reach in a certain region
            if(enemy.getX() < player.getX() + 400 / MarioBros.PPM){
                enemy.body.setActive(true);
            }
        }

        //update item setToDestroy status
        for(Item item : items){
            item.update(dt);
        }
    }

    // render everything
    @Override
    public void render(float delta) {

        //render update information
        update(delta);

        //clear the game screen with black
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render the map
        renderer.render();

        //render Box2d debug lines
        box2DDebugRenderer.render(world, camera.combined);

        marioBros.batch.setProjectionMatrix(camera.combined);
        marioBros.batch.begin();


        //render Mario
        player.draw(marioBros.batch);

        // render Enemy
        for(Enemy enemy : box2DWorldCreator.getEnemies()){
            enemy.draw(marioBros.batch);
        }

        // render Items
        for(Item item : items){
            item.draw(marioBros.batch);
        }
        marioBros.batch.end();

        // set batch to draw what hud camera sees
        marioBros.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        //if game over, start game over screen and setToDestroy play screen
        if(isGameOver()){
            marioBros.setScreen(new GameOverScreen(marioBros));
            dispose();
        }
    }

    //if mario currentState is dead, then set isGameOver true
    public boolean isGameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 3){
            return true;
        }
        return false;
    }

    //return world for sprite's body creation
    public World getWorld(){
        return world;
    }

    //return map for retrieving map's objects
    public TiledMap getMap(){
        return map;
    }

    //return basic texture region
    public TextureAtlas getBasicAtlas(){
        return basicAtlas;
    }

    //return advanced texture region
    public TextureAtlas getAdvAtlas(){
        return advAtlas;
    }

    //update viewport
    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void show() {}

    //dispose all opened resource
    @Override
    public void dispose() {
        world.dispose();
        map.dispose();
        renderer.dispose();
        box2DDebugRenderer.dispose();
        hud.dispose();
    }
}
