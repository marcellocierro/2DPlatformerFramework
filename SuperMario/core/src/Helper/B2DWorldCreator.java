package Helper;

import Screens.PlayScreen;
import Sprites.*;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/19/18.
 */
public class B2DWorldCreator {
    private Array<Goomba> goombas;
    private Array<Koopa> koopas;

    public B2DWorldCreator(PlayScreen screen) {
        World world = screen.getWorld();
        TiledMap map = screen.getMap();
        BodyDef bDef = new BodyDef();
        PolygonShape pShape = new PolygonShape();
        FixtureDef fDef = new FixtureDef();


        Body body;

        //map
        for(MapObject mObjects : map.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) mObjects).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            pShape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM );
            fDef.shape = pShape;
            fDef.filter.categoryBits = MarioBros.GROUNDFILTER;
            body.createFixture(fDef);
        }
        //pipe
        for(MapObject mObjects : map.getLayers().get(3).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) mObjects).getRectangle();

            bDef.type = BodyDef.BodyType.StaticBody;
            bDef.position.set((rectangle.getX() + rectangle.getWidth() / 2) / MarioBros.PPM, (rectangle.getY() + rectangle.getHeight() / 2) / MarioBros.PPM);

            body = world.createBody(bDef);

            pShape.setAsBox(rectangle.getWidth() / 2 / MarioBros.PPM, rectangle.getHeight() / 2 / MarioBros.PPM );
            fDef.shape = pShape;
            fDef.filter.categoryBits = MarioBros.OBJECTFILTER;
            body.createFixture(fDef);
        }
        //bricks
        for(MapObject mObjects : map.getLayers().get(5).getObjects().getByType(RectangleMapObject.class)){
            new Brick(screen, mObjects);

        }
        //coins
        for(MapObject mObjects : map.getLayers().get(4).getObjects().getByType(RectangleMapObject.class)){
            new Coin(screen, mObjects);
        }

        //goombas
        goombas = new Array<Goomba>();
        for(MapObject mObjects : map.getLayers().get(6).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) mObjects).getRectangle();
            goombas.add(new Goomba(screen, rectangle.getX()/MarioBros.PPM, rectangle.getY()/MarioBros.PPM));
        }

        //koopas
        koopas = new Array<Koopa>();
        for(MapObject mObjects : map.getLayers().get(7).getObjects().getByType(RectangleMapObject.class)){
            Rectangle rectangle = ((RectangleMapObject) mObjects).getRectangle();
            koopas.add(new Koopa(screen, rectangle.getX()/MarioBros.PPM, rectangle.getY()/MarioBros.PPM));
        }
    }

    public Array<Goomba> getGoombas() {
        return goombas;
    }

    public Array<Enemy> getEnemies() {
        Array<Enemy> enemies = new Array<Enemy>();
        enemies.addAll(goombas);
        enemies.addAll(koopas);

        return enemies;
    }

}
