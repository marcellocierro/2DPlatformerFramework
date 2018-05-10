package Sprites;

import Scenes.HUD;
import Screens.PlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/19/18.
 */
public class Coin extends InteractiveTile {

    private TiledMapTileSet tSet;
    private final int blankCoin = 28;
    private boolean collided = false;
    private AssetManager aManager;
    private Sound sound;

    public Coin(PlayScreen screen, MapObject object) {
        super(screen, object);
        tSet = map.getTileSets().getTileSet("tileset_gutter");
        fixture.setUserData(this);
        setCategoryFilter(MarioBros.COINFILTER);
        aManager = new AssetManager();
        aManager.load("Effects/smb_bump.wav", Sound.class);
        aManager.load("Effects/smb_coin.wav", Sound.class);
        aManager.load("Effects/smb_powerup_appears.wav", Sound.class);
        aManager.finishLoading();
    }

    @Override
    public void onHeadCollision(Mario mario) {
        if(collided == false){
            System.out.println("Coin +100 Score");
            oneCoin();
        } else {
            sound = aManager.get("Effects/smb_bump.wav", Sound.class);
            sound.play(0.3f);
        }


    }

    public void oneCoin(){
        getCell().setTile(tSet.getTile(blankCoin));
        HUD.updateScore(100);
        sound = aManager.get("Effects/smb_coin.wav", Sound.class);
        if(object.getProperties().containsKey("mushroom")){
            screen.spawnItem(new ItemDefinition(new Vector2(body.getPosition().x,body.getPosition().y + 16 /MarioBros.PPM)
                    ,Mushroom.class));
            sound = aManager.get("Effects/smb_powerup_appears.wav", Sound.class);
            sound.play(0.05f);

        }
        sound.play(0.05f);

        collided = true;

    }

}
