package Scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;

/**
 * Created by Marcello395 on 4/9/18.
 */
public class HUD implements Disposable {
    public Stage stage;
    private Viewport viewPort;

    private Integer worldTimer;
    private double timeCount;
    private static Integer score;

    private Label countDownLabel;
    private static Label scoreLabel;
    private Label timeLabel;
    private Label levelLabel;
    private Label worldLabel;
    private Label marioLabel;
    

    public HUD (SpriteBatch sb){
        worldTimer = 120;
        timeCount = 0;
        score = 0;

        viewPort = new FitViewport(MarioBros.Virtual_Width, MarioBros.Virtual_Height, new OrthographicCamera());
        stage = new Stage(viewPort, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countDownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%06d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));;
        levelLabel = new Label(String.format("1-1", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        worldLabel = new Label(String.format("WORLD", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        marioLabel = new Label(String.format("MARIO BROS", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(marioLabel).expandX().pad(10);
        table.add(worldLabel).expandX().pad(10);
        table.add(timeLabel).expandX().pad(10);
        table.row();

        table.add(scoreLabel).expandX();
        table.add(levelLabel).expandX();
        table.add(countDownLabel).expandX();

        stage.addActor(table);
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void update(float delta){
        timeCount += delta;
        if(timeCount >= 1){
            if(worldTimer >= 1) {
                --worldTimer;
                countDownLabel.setText(String.format("%03d", worldTimer));
                timeCount = 0;
            }
        }
    }

    public static void updateScore(int uScore){
        score += uScore;
        scoreLabel.setText(String.format("%06d", score));
    }
}
