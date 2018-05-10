package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 5/4/18.
 */
public class Mushroom extends Item {
    public Mushroom(PlayScreen screen, float x, float y) {
        super(screen, x, y);
        setRegion(screen.getAtlas().findRegion("mushroom"),0,0,16,16);
        velocity = new Vector2(0.8f,0);
    }

    @Override
    public void defineItem() {
        BodyDef bDef = new BodyDef();

        bDef.position.set(getX(), getY());
        bDef.type = BodyDef.BodyType.DynamicBody;

        body = world.createBody(bDef);

        FixtureDef fDef = new FixtureDef();
        CircleShape cShape = new CircleShape();
        cShape.setRadius(6 / MarioBros.PPM);
        fDef.filter.categoryBits = MarioBros.ITEMFILTER;
        fDef.filter.maskBits = MarioBros.MARIOFILTER |
                MarioBros.OBJECTFILTER |
                MarioBros.GROUNDFILTER |
                MarioBros.COINFILTER |
                MarioBros.BRICKFILTER;


        fDef.shape = cShape;
        body.createFixture(fDef).setUserData(this);

    }

    @Override
    public void useItem(Mario mario) {
        destroy();
        mario.growMario();
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        setPosition(body.getPosition().x - getWidth()/2, body.getPosition().y - getHeight()/2);
        velocity.y = body.getLinearVelocity().y;
        body.setLinearVelocity(velocity );
    }
}
