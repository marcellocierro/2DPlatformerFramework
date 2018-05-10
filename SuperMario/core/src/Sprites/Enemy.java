package Sprites;

import Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by Marcello395 on 4/25/18.
 */
public abstract class Enemy extends Sprite {
    protected World world;
    protected PlayScreen screen;

    public Body b2dBody;

    public Vector2 velocity;

    public Enemy(PlayScreen screen, float x, float y){
        this.world = screen.getWorld();
        this.screen = screen;
        setPosition(x,y);
        defineEnemy();
        velocity = new Vector2(1, 0);
        b2dBody.setActive(false);
    }

    protected abstract void defineEnemy();

    public abstract void hitOnHead(Mario mario);

    public void reverseVelocity(boolean x, boolean y){
        if(x)
            velocity.x = -velocity.x;
        if(y)
            velocity.y = -velocity.y;
    }

    public abstract void update(float deltaTime);
}

