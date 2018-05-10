package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/19/18.
 */
public abstract class InteractiveTile {
    protected World world;
    protected TiledMap map;
    protected TiledMapTile tile;
    protected Rectangle bounds;
    protected Body body;
    protected MapObject object;

    protected PlayScreen screen;

    protected Fixture fixture;

    public InteractiveTile(PlayScreen screen, MapObject object) {
        this.screen = screen;
        this.world = screen.getWorld();
        this.map = screen.getMap();
        this.bounds = ((RectangleMapObject) object).getRectangle();
        this.object = object;

        BodyDef bDef = new BodyDef();
        FixtureDef fDef = new FixtureDef();
        PolygonShape pShape = new PolygonShape();

        bDef.type = BodyDef.BodyType.StaticBody;
        bDef.position.set((bounds.getX() + bounds.getWidth() / 2) / MarioBros.PPM, (bounds.getY() + bounds.getHeight() / 2) / MarioBros.PPM);

        body = world.createBody(bDef);

        pShape.setAsBox(bounds.getWidth() / 2 / MarioBros.PPM, bounds.getHeight() / 2 / MarioBros.PPM );
        fDef.shape = pShape;
        fixture = body.createFixture(fDef);
    }

    public abstract void onHeadCollision(Mario mario);

    //defines which filter is being used.
    public void setCategoryFilter(Short fBit){
        Filter filter = new Filter();
        filter.categoryBits = fBit;
        fixture.setFilterData(filter);
    }


    //get a specific cell
    public TiledMapTileLayer.Cell getCell(){
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(1);
        return layer.getCell((int)(body.getPosition().x * MarioBros.PPM / 16), (int)(body.getPosition().y * MarioBros.PPM /16));

    }
}
