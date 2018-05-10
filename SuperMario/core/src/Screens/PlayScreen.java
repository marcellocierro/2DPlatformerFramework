package Screens;

import Helper.B2DWorldCreator;
import Helper.WorldContactListener;
import Scenes.HUD;
import Sprites.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.*;
import com.mygdx.game.MarioBros;

import java.util.concurrent.LinkedBlockingDeque;

/**
 * Created by Marcello395 on 4/9/18.
 */
public class PlayScreen implements Screen {

    private MarioBros game;
    private TextureAtlas tAtlas;

//    Texture texture;
    private OrthographicCamera gameCamera;
    private Viewport gamePort;
    private HUD hud;

    private TmxMapLoader mapLoader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer mapRenderer;

    private World world;
    private Box2DDebugRenderer b2dr;
    private B2DWorldCreator creator;

    private Mario player;
//    private Goomba temp_goomba;

    private AssetManager aManager;
    private Sound sound;
    private Music music;

    private Array<Item> items;
    private LinkedBlockingDeque<ItemDefinition> itemsToSpawn;

    public PlayScreen(MarioBros game){
        tAtlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;
//        texture = new Texture("badlogic.jpg");
        gameCamera = new OrthographicCamera();
//        gamePort = new StretchViewport(800, 480, gameCamera);
        gamePort = new FitViewport(MarioBros.Virtual_Width / MarioBros.PPM, MarioBros.Virtual_Height / MarioBros.PPM, gameCamera);
//        gamePort = new ScreenViewport(gameCamera);

        hud = new HUD(game.batch);

        mapLoader = new TmxMapLoader();
        map = mapLoader.load("level1.tmx");
        mapRenderer = new OrthogonalTiledMapRenderer(map, 1/ MarioBros.PPM);
        gameCamera.position.set(gamePort.getWorldWidth()/2, gamePort.getWorldHeight()/2, 0);

        world = new World(new Vector2(0,-10), true);
        b2dr = new Box2DDebugRenderer();

        player = new Mario(this);
        //temp_goomba = new Goomba(this, 1f, 32/MarioBros.PPM);
//        temp_goomba = new Goomba(this, 5.64f, .16f);

        creator = new B2DWorldCreator(this);

        //music
        aManager = new AssetManager();
        aManager.load("Music/SuperMarioBros.ogg", Music.class);
        aManager.load("Effects/smb_jump-super.wav", Sound.class);
        aManager.load("Effects/smb_jump-small.wav", Sound.class);
        aManager.finishLoading();

        music = aManager.get("Music/SuperMarioBros.ogg", Music.class);
        music.setLooping(true);
        music.setVolume(0.05f);
       // music.play();

        items = new Array<Item>();
        itemsToSpawn = new LinkedBlockingDeque<ItemDefinition>();

        world.setContactListener(new WorldContactListener());
    }


    public TextureAtlas getAtlas(){
        return tAtlas;
    }

    @Override
    public void show() {

    }

    public void handleInput(float deltaTime){
        if (!player.currentState.equals(Mario.State.DEAD)) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE))
                if (player.b2dBody.getLinearVelocity().y == 0) {
                    player.b2dBody.applyLinearImpulse(new Vector2(0, 4), player.b2dBody.getWorldCenter(), true);
                    if (player.isBig()) {
                        sound = aManager.get("Effects/smb_jump-super.wav", Sound.class);
                        sound.play(0.1f);
                    } else {
                        sound = aManager.get("Effects/smb_jump-small.wav", Sound.class);
                        sound.play(0.1f);
                    }
                }
            // gameCamera.position.x += 100 * deltaTime;
            if (Gdx.input.isKeyPressed(Input.Keys.D) && player.b2dBody.getLinearVelocity().x <= 2)
                player.b2dBody.applyLinearImpulse(new Vector2(0.1f, 0), player.b2dBody.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.A) && player.b2dBody.getLinearVelocity().x >= -2)
                player.b2dBody.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2dBody.getWorldCenter(), true);
        }
    }


    public void update(float deltaTime){
        handleInput(deltaTime);
        handleItemSpawn();

        world.step(1/60f, 6, 2);

        player.update(deltaTime);
//        temp_goomba.update(deltaTime);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.update(deltaTime);
            if(enemy.getX() < player.getX() + 2.24){
                enemy.b2dBody.setActive(true);
            }
        }

        for(Item item : items){
            item.update(deltaTime);
        }
        hud.update(deltaTime);

        if(!player.currentState.equals(Mario.State.DEAD)){
            gameCamera.position.x = player.b2dBody.getPosition().x;
        }

        gameCamera.update();
        mapRenderer.setView(gameCamera);

        aManager.update();


    }

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        //render game map
        mapRenderer.render();

        //debug lines
        b2dr.render(world, gameCamera.combined);

        //mario
        game.batch.setProjectionMatrix(gameCamera.combined);
        game.batch.begin();
        player.draw(game.batch);
//        temp_goomba.draw(game.batch);
        for(Enemy enemy : creator.getEnemies()) {
            enemy.draw(game.batch);
        }

        for(Item item : items){
            item.draw(game.batch);
        }

        game.batch.end();

        //camera to see what hud sees
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();
        }
    }

    @Override
    public void resize(int width, int height) {
        gamePort.update(width, height);

    }


    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        map.dispose();
        mapRenderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }

    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
    }

    public void spawnItem(ItemDefinition itemDef){
        itemsToSpawn.add(itemDef);
    }

    public void handleItemSpawn(){
        if(!itemsToSpawn.isEmpty()){
            ItemDefinition itemDef = itemsToSpawn.poll();
            if(itemDef.type == Mushroom.class){
                items.add(new Mushroom(this, itemDef.position.x, itemDef.position.y));
            }
        }
    }

    public boolean gameOver(){
        if(player.currentState.equals(Mario.State.DEAD) && player.getStateTicker() > 3){
            return true;
        }
        return false;
    }
}
