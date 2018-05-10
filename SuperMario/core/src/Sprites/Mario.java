package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/18/18.
 */
public class Mario extends Sprite {

    public enum State {STANDING, RUNNING, JUMPING, FALLING, GROWING, DEAD};
    public State currentState;
    public State previousState;

    public World world;
    public Body b2dBody;

    private TextureRegion marioStand;
    private Animation<TextureRegion> marioRun;
    private TextureRegion marioJump;
    private TextureRegion marioDead;

    private float stateTicker;
    private boolean runRight;

    private TextureRegion bigMarioStand;
    private TextureRegion bigMarioJump;
    private Animation<TextureRegion> bigMarioRun;
    private Animation<TextureRegion> growMario;

    private boolean marioGrew;
    private boolean runGrowAnimation;
    private boolean definedBigMario;
    private boolean redefineMario;
    private boolean marioDied;

    private AssetManager aManager;
    private Sound sound;



    public Mario(PlayScreen screen){
        this.world = screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTicker = 0;
        runRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for (int i = 1; i < 4; ++i)
//            frames.add(new TextureRegion(getTexture(), i * 16,11,16, 16));
            frames.add(new TextureRegion(screen.getAtlas().findRegion("little_mario"), i * 16,0,16, 16));

        marioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();

        for (int i = 1; i < 4; ++i)
            frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), i * 16,0,16, 32 ));
        bigMarioRun = new Animation<TextureRegion>(0.1f, frames);
        frames.clear();


        /* Mario Grow Animation */
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 240, 0, 16, 32));
        frames.add(new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32));
        growMario = new Animation<TextureRegion>(0.2f, frames);

        marioJump = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 80, 0, 16, 16);
        bigMarioJump = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 80, 0, 16, 32);


//        marioStand = new TextureRegion(getTexture(),1,11,16, 16);
        marioStand = new TextureRegion(screen.getAtlas().findRegion("little_mario"),1,0,16, 16);
        bigMarioStand = new TextureRegion(screen.getAtlas().findRegion("big_mario"), 0, 0, 16, 32);

        marioDead = new TextureRegion(screen.getAtlas().findRegion("little_mario"), 96, 0, 16, 16);


        setBounds(1, 11, 16 / MarioBros.PPM, 16 / MarioBros.PPM);
        setRegion(marioStand);
        defineMario();

        //sounds for interactions
        aManager = new AssetManager();
        aManager.load("Effects/smb_powerup.wav", Sound.class);
        aManager.load("Effects/smb_pipe.wav", Sound.class);
        aManager.load("Effects/smb_mariodie.wav", Sound.class);
        aManager.finishLoading();

    }

    public void update (float delta){
        if(marioGrew)
            setPosition(b2dBody.getPosition().x - getWidth() / 2, b2dBody.getPosition().y - getHeight() / 2 - 6/MarioBros.PPM);
        else
            setPosition(b2dBody.getPosition().x - getWidth() / 2, b2dBody.getPosition().y - getHeight() / 2);
        setRegion(getFrame(delta));
        if(definedBigMario){
            defineBigMario();
        }
        if(redefineMario){
            redefineMario();
        }

    }

    public TextureRegion getFrame(float delta){
        currentState = getState();

        TextureRegion tRegion;
        switch (currentState){
            case DEAD:
                tRegion = marioDead;
                break;
            case GROWING:
                tRegion = growMario.getKeyFrame(stateTicker);
                if(growMario.isAnimationFinished(stateTicker))
                    runGrowAnimation = false;
                break;
            case JUMPING:
                tRegion = marioGrew ? bigMarioJump : marioJump;
                break;
            case RUNNING:
                tRegion = marioGrew ? bigMarioRun.getKeyFrame(stateTicker, true) : marioRun.getKeyFrame(stateTicker, true);
                break;
            case FALLING:
            case STANDING:
                default:
                    tRegion = marioGrew? bigMarioStand: marioStand;
                    break;
        }
        if ((b2dBody.getLinearVelocity().x < 0 || !runRight) && !tRegion.isFlipX()){
            tRegion.flip(true, false);
            runRight = false;
        } else if((b2dBody.getLinearVelocity().x > 0 || runRight) && tRegion.isFlipX()){
            tRegion.flip(true, false);
            runRight = true;
        }
        stateTicker = currentState == previousState ? stateTicker + delta : 0;
        //update previous state
        previousState = currentState;
        //return our last frame
        return tRegion;
    }

    public State getState(){

        if(marioDied){
            return State.DEAD;
        } else if(runGrowAnimation){
            return State.GROWING;
        } else if(b2dBody.getLinearVelocity().y > 0 || (b2dBody.getLinearVelocity().y < 0 && previousState == State.JUMPING)){
            return State.JUMPING;
        } else if(b2dBody.getLinearVelocity().y < 0){
            return State.FALLING;
        } else if (b2dBody.getLinearVelocity().x != 0){
            return State.RUNNING;
        } else {
            return State.STANDING;
        }

    }

    public void defineMario(){
        BodyDef bDef = new BodyDef();

        bDef.position.set(32 / MarioBros.PPM, 32 / MarioBros.PPM);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape cShape = new CircleShape();
        cShape.setRadius(7 / MarioBros.PPM);

        //set marios bits so it can interact.
        fDef.filter.categoryBits = MarioBros.MARIOFILTER;
        fDef.filter.maskBits = MarioBros.GROUNDFILTER |
                MarioBros.COINFILTER |
                MarioBros.BRICKFILTER |
                MarioBros.ENEMYFILTER |
                MarioBros.OBJECTFILTER |
                MarioBros.ENEMYHEADFILTER |
                MarioBros.ITEMFILTER;

        fDef.shape = cShape;
        b2dBody.createFixture(fDef).setUserData(this);



        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MarioBros.PPM, 7 / MarioBros.PPM), new Vector2(2/ MarioBros.PPM, 7 / MarioBros.PPM));
        fDef.filter.categoryBits = MarioBros.MARIOHEADFILTER;
        fDef.shape = head;
        fDef.isSensor = true;

        b2dBody.createFixture(fDef).setUserData(this);
    }

    public void defineBigMario(){
        Vector2 currentPosition = b2dBody.getPosition();
        world.destroyBody(b2dBody);

        BodyDef bDef = new BodyDef();

        bDef.position.set(currentPosition.add(0,10 / MarioBros.PPM));
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape cShape = new CircleShape();
        cShape.setRadius(7 / MarioBros.PPM);

        //set marios bits so it can interact.
        fDef.filter.categoryBits = MarioBros.MARIOFILTER;
        fDef.filter.maskBits = MarioBros.GROUNDFILTER |
                MarioBros.COINFILTER |
                MarioBros.BRICKFILTER |
                MarioBros.ENEMYFILTER |
                MarioBros.OBJECTFILTER |
                MarioBros.ENEMYHEADFILTER |
                MarioBros.ITEMFILTER;

        fDef.shape = cShape;
        b2dBody.createFixture(fDef).setUserData(this);
        cShape.setPosition(new Vector2(0, -14 / MarioBros.PPM));
        b2dBody.createFixture(fDef).setUserData(this);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MarioBros.PPM, 7 / MarioBros.PPM), new Vector2(2/ MarioBros.PPM, 7 / MarioBros.PPM));
        fDef.filter.categoryBits = MarioBros.MARIOHEADFILTER;
        fDef.shape = head;
        fDef.isSensor = true;

        b2dBody.createFixture(fDef).setUserData(this);
        definedBigMario = false;
    }

    public void redefineMario(){
        Vector2 position = b2dBody.getPosition();
        world.destroyBody(b2dBody);

        BodyDef bDef = new BodyDef();
        bDef.position.set(position);
        bDef.type = BodyDef.BodyType.DynamicBody;

        b2dBody = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape cShape = new CircleShape();
        cShape.setRadius(7 / MarioBros.PPM);

        //set marios bits so it can interact.
        fDef.filter.categoryBits = MarioBros.MARIOFILTER;
        fDef.filter.maskBits = MarioBros.GROUNDFILTER |
                MarioBros.COINFILTER |
                MarioBros.BRICKFILTER |
                MarioBros.ENEMYFILTER |
                MarioBros.OBJECTFILTER |
                MarioBros.ENEMYHEADFILTER |
                MarioBros.ITEMFILTER;

        fDef.shape = cShape;
        b2dBody.createFixture(fDef).setUserData(this);



        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2/ MarioBros.PPM, 7 / MarioBros.PPM), new Vector2(2/ MarioBros.PPM, 7 / MarioBros.PPM));
        fDef.filter.categoryBits = MarioBros.MARIOHEADFILTER;
        fDef.shape = head;
        fDef.isSensor = true;

        b2dBody.createFixture(fDef).setUserData(this);

        redefineMario = false;

    }

    public void growMario() {
        runGrowAnimation = true;
        marioGrew = true;
        definedBigMario = true;
        setBounds(getX(), getY(), getWidth(), getHeight() * 2);
        sound = aManager.get("Effects/smb_powerup.wav", Sound.class);
        sound.play(0.05f);
    }

    public boolean isBig(){
        return marioGrew;
    }

    public boolean isDead(){
        return marioDied;
    }

    public float getStateTicker(){
        return stateTicker;
    }

    public void damaged(Enemy enemy) {
        //if we land on the left side of the koopa we kick right. Else we kick left.
        if (enemy instanceof Koopa && ((Koopa) enemy).getCurrentState() == Koopa.State.Still_Shell) {
            ((Koopa) enemy).kickKoopa(this.getX() < enemy.getX() ? Koopa.kickRight : Koopa.kickLeft);
        } else {
            if (marioGrew) {
                marioGrew = false;
                redefineMario = true;
                setBounds(getX(), getY(), getWidth(), getHeight() / 2);
                sound = aManager.get("Effects/smb_pipe.wav", Sound.class);
                sound.play(0.1f);
            } else {
                sound = aManager.get("Effects/smb_mariodie.wav", Sound.class);
                sound.play(0.1f);

                marioDied = true;
                Filter filter = new Filter();
                filter.maskBits = MarioBros.GAMEOVERFILTER;
                for (Fixture fixture : b2dBody.getFixtureList()) {
                    fixture.setFilterData(filter);
                }
                b2dBody.applyLinearImpulse(new Vector2(0, 5f), b2dBody.getWorldCenter(), true);
            }
        }
    }
}
