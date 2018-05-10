package Sprites;

import com.badlogic.gdx.math.Vector2;

/**
 * Created by Marcello395 on 5/4/18.
 */
public class ItemDefinition {
    public Vector2 position;
    public Class<?> type;

    public ItemDefinition(Vector2 position, Class<?> type){
        this.position = position;
        this.type = type;
    }

}
