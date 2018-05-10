package Helper;

import Sprites.Enemy;
import Sprites.InteractiveTile;
import Sprites.Item;
import Sprites.Mario;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/20/18.
 */
public class WorldContactListener implements ContactListener {
    @Override
    public void beginContact(Contact contact) {
//        System.out.println("Begin Contact");
        Fixture fixtureA = contact.getFixtureA();
        Fixture fixtureB = contact.getFixtureB();

        int cDef = fixtureA.getFilterData().categoryBits | fixtureB.getFilterData().categoryBits;

//        if (fixtureA.getUserData() == "head" || fixtureB.getUserData() == "head") {
//            Fixture headFixture = fixtureA.getUserData() == "head" ? fixtureA : fixtureB;
//            Fixture objectFixture = headFixture == fixtureA ? fixtureB : fixtureA;
//
//            if (objectFixture.getUserData() instanceof InteractiveTile) {
//                ((InteractiveTile) objectFixture.getUserData()).onHeadCollision();
//            }
//        }

        switch (cDef) {
            case MarioBros.MARIOHEADFILTER | MarioBros.BRICKFILTER:

            case MarioBros.MARIOHEADFILTER | MarioBros.COINFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.MARIOHEADFILTER)
                    ((InteractiveTile) fixtureB.getUserData()).onHeadCollision((Mario)fixtureA.getUserData());
                else
                    ((InteractiveTile) fixtureA.getUserData()).onHeadCollision((Mario)fixtureB.getUserData());
                break;

            case MarioBros.ENEMYHEADFILTER | MarioBros.MARIOFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ENEMYHEADFILTER) {
                    ((Enemy) fixtureA.getUserData()).hitOnHead((Mario)fixtureB.getUserData());
                } else if (fixtureB.getFilterData().categoryBits == MarioBros.ENEMYHEADFILTER) {
                    ((Enemy) fixtureB.getUserData()).hitOnHead((Mario)fixtureA.getUserData());
                    break;
                }
            case MarioBros.ENEMYFILTER | MarioBros.OBJECTFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ENEMYFILTER) {
                    ((Enemy) fixtureA.getUserData()).reverseVelocity(true,false);
                } else if (fixtureB.getFilterData().categoryBits == MarioBros.ENEMYFILTER) {
                    ((Enemy) fixtureB.getUserData()).reverseVelocity(true,false);
                    break;
                }

            case MarioBros.ENEMYFILTER | MarioBros.ENEMYFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ENEMYFILTER) {
                    ((Enemy) fixtureA.getUserData()).reverseVelocity(true,false);
                    ((Enemy) fixtureB.getUserData()).reverseVelocity(true,false);
                } else if (fixtureB.getFilterData().categoryBits == MarioBros.ENEMYFILTER) {
                    ((Enemy) fixtureB.getUserData()).reverseVelocity(true,false);
                    ((Enemy) fixtureA.getUserData()).reverseVelocity(true,false);
                }
                break;

            case MarioBros.ITEMFILTER | MarioBros.OBJECTFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ITEMFILTER) {
                    ((Item) fixtureA.getUserData()).reverseVelocity(true,false);
                } else if (fixtureB.getFilterData().categoryBits == MarioBros.ITEMFILTER) {
                    ((Item) fixtureB.getUserData()).reverseVelocity(true,false);
                    break;
                }

            case MarioBros.ITEMFILTER | MarioBros.MARIOFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.ITEMFILTER) {
                    ((Item) fixtureA.getUserData()).useItem((Mario)fixtureB.getUserData());
                } else if (fixtureB.getFilterData().categoryBits == MarioBros.ITEMFILTER) {
                    ((Item) fixtureB.getUserData()).useItem((Mario)fixtureA.getUserData());
                    break;
                }
            case MarioBros.MARIOFILTER | MarioBros.ENEMYFILTER:
                if (fixtureA.getFilterData().categoryBits == MarioBros.MARIOFILTER)
                    ((Mario)fixtureA.getUserData()).damaged((Enemy) fixtureB.getUserData());
                else
                    ((Mario)fixtureB.getUserData()).damaged((Enemy) fixtureA.getUserData());
                break;
        }

    }

    @Override
    public void endContact(Contact contact) {
        System.out.println("End Contact");
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
