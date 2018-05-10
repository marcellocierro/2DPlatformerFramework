package Sprites;

import Scenes.HUD;
import Screens.PlayScreen;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/19/18.
 */
public class Brick extends InteractiveTile {

    private AssetManager aManager;
    private Sound sound;

    public Brick(PlayScreen screen, MapObject object) {
        super(screen, object);
        fixture.setUserData(this);
        //sets filter for the brick bit
        aManager = new AssetManager();
        aManager.load("Effects/smb_breakblock.wav", Sound.class);
        aManager.load("Effects/smb_bump.wav", Sound.class);
        aManager.finishLoading();

        setCategoryFilter(MarioBros.BRICKFILTER);
    }

    @Override
    public void onHeadCollision(Mario mario) {
        System.out.println("Break brick");

        if(mario.isBig()){
            //set filter for the default case
            setCategoryFilter(MarioBros.DESTROYERFILTER);
            //set our broken brick to null
            getCell().setTile(null);
            HUD.updateScore(200);

            sound = aManager.get("Effects/smb_breakblock.wav", Sound.class);
            sound.play(0.3f);
        } else {
            sound = aManager.get("Effects/smb_bump.wav", Sound.class);
            sound.play(0.5f);
        }

    }


}
