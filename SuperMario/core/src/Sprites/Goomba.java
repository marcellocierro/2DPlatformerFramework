package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/25/18.
 */
public class Goomba extends Enemy {

    private float stateTicker;
    private Animation<TextureRegion> walking;
    private Array<TextureRegion> frames;
    private boolean queuedForDestruction;
    private boolean destroyed;
    private AssetManager aManager;
    private Sound sound;



    public Goomba(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        frames = new Array<TextureRegion>();
        for (int i = 0; i < 2; i++){
            frames.add(new TextureRegion(screen.getAtlas().findRegion("goomba"), i * 16,0, 16, 16));
        }
        walking = new Animation<TextureRegion>(0.4f, frames);
        stateTicker = 0;
        setBounds(getX(), getY(), 16/MarioBros.PPM, 16/MarioBros.PPM);

        queuedForDestruction = false;
        destroyed = false;

        aManager = new AssetManager();
        aManager.load("Effects/smb_stomp.wav", Sound.class);
        aManager.finishLoading();
    }

    public void update(float delta){
        stateTicker += delta;

        if(queuedForDestruction && !destroyed){
            world.destroyBody(b2dBody);
            destroyed = true;
            setRegion(new TextureRegion(screen.getAtlas().findRegion("goomba"), 32, 0 ,16, 16));
//            setBounds(getX(), getY(), 16/MarioBros.PPM, 8 / MarioBros.PPM);
            stateTicker = 0;
        }
        else if(!destroyed) {
            b2dBody.setLinearVelocity(velocity);
            setPosition(b2dBody.getPosition().x - getWidth() / 2, b2dBody.getPosition().y - getHeight() / 2);
            setRegion(walking.getKeyFrame(stateTicker, true));
        }
    }

    @Override
    protected void defineEnemy() {
        BodyDef bDef = new BodyDef();
        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;
        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape cShape = new CircleShape();
        cShape.setRadius(7 / MarioBros.PPM);

        //set marios bits so it can interact.
        fDef.filter.categoryBits = MarioBros.ENEMYFILTER;
        fDef.filter.maskBits =
                MarioBros.GROUNDFILTER |
                        MarioBros.COINFILTER |
                        MarioBros.BRICKFILTER |
                        MarioBros.ENEMYFILTER |
                        MarioBros.OBJECTFILTER |
                        MarioBros.MARIOFILTER;

        fDef.shape = cShape;
//        fDef.filter.categoryBits = MarioBros.OBJECTFILTER;
        b2dBody.createFixture(fDef).setUserData(this);

        //goomba head
        PolygonShape head = new PolygonShape();
        Vector2[] vertices = new Vector2[4];
        vertices[0] = new Vector2(-6, 9).scl(1/MarioBros.PPM);
        vertices[1] = new Vector2(6, 9).scl(1/MarioBros.PPM);
        vertices[2] = new Vector2(-3, 4).scl(1/MarioBros.PPM);
        vertices[3] = new Vector2(3, 4).scl(1/MarioBros.PPM);
        head.set(vertices);

        fDef.shape = head;
        //add bounciness
        fDef.restitution = 0.5f;
        fDef.filter.categoryBits = MarioBros.ENEMYHEADFILTER;
        b2dBody.createFixture(fDef).setUserData(this);

    }

    public void draw(Batch batch){
       if (!destroyed || stateTicker < 1){
           super.draw(batch);
       }
    }

    @Override
    public void hitOnHead(Mario mario) {
        queuedForDestruction = true;
        sound = aManager.get("Effects/smb_stomp.wav", Sound.class);
        sound.play(0.3f);
    }

}
