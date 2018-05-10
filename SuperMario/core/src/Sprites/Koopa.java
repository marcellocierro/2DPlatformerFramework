package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 5/8/18.
 */
public class Koopa extends Enemy {
    public enum State {Patrolling, Still_Shell, Moving_Shell }

    public static final int kickLeft = -2;
    public static final int kickRight = 2;

    public State currentState;
    public State previousState;

    private float stateTicker;
    private Animation<TextureRegion> walking;
    private Array<TextureRegion> frames;
    private TextureRegion shell;

    private boolean queuedForDestruction;
    private boolean destroyed;
    private AssetManager aManager;
    private Sound sound;

    public Koopa(PlayScreen screen, float x, float y) {
        super(screen, x, y);

        frames = new Array<TextureRegion>();
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 0, 0, 16, 24));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("turtle"), 16, 0, 16, 24));
        shell = new TextureRegion(screen.getAtlas().findRegion("turtle"), 64, 0, 16, 24);

        walking = new Animation<TextureRegion>(0.2f, frames);
        currentState = previousState = State.Patrolling;

        setBounds(getX(), getY(), 16/ MarioBros.PPM, 24/ MarioBros.PPM);

        aManager = new AssetManager();
        aManager.load("Effects/smb_stomp.wav", Sound.class);
        aManager.finishLoading();
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
        fDef.restitution = 0.6f;
        fDef.filter.categoryBits = MarioBros.ENEMYHEADFILTER;
        b2dBody.createFixture(fDef).setUserData(this);

    }


    public TextureRegion getFrame(float deltaTime){
        TextureRegion tRegion;

        switch (currentState){
            case Still_Shell:
            case Moving_Shell:
                tRegion = shell;
                break;

            case Patrolling:
            default:
                tRegion = walking.getKeyFrame(stateTicker,true);
                break;
        }
        if(velocity.x > 0 && !(tRegion.isFlipX())){
            tRegion.flip(true, false);
        }
        if(velocity.x < 0 && (tRegion.isFlipX())){
            tRegion.flip(true, false);
        }


        stateTicker = currentState == previousState ? stateTicker + deltaTime : 0;
        //update previous state
        previousState = currentState;
        //return our last frame
        return tRegion;
    }

    @Override
    public void update(float deltaTime) {
        setRegion(getFrame(deltaTime));
        if(currentState.equals(State.Still_Shell) && stateTicker > 5){
            currentState = State.Patrolling;
            velocity.x = 1;
        }

        setPosition(b2dBody.getPosition().x - getWidth()/2, b2dBody.getPosition().y - 8 / MarioBros.PPM);
        b2dBody.setLinearVelocity(velocity);

    }

    @Override
    public void hitOnHead(Mario mario) {
        if(!currentState.equals(State.Still_Shell)){
            currentState = State.Still_Shell;
            sound = aManager.get("Effects/smb_stomp.wav", Sound.class);
            sound.play(0.3f);
            velocity.x = 0;
        } else {
            kickKoopa(mario.getX() <= this.getX() ? kickRight : kickLeft);
        }

    }

    public void kickKoopa(int speed){
        velocity.x = speed;
        currentState = State.Moving_Shell;
        sound = aManager.get("Effects/smb_stomp.wav", Sound.class);
        sound.play(0.3f);
    }

    public State getCurrentState(){
        return currentState;
    }
}
