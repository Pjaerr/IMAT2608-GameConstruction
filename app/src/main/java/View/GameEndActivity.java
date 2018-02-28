package View;

import android.content.Intent;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import Model.PreferenceManager;
import jackson.joshua.imat2608_galaga.R;

public class GameEndActivity extends AppCompatActivity
{
    TextView title; //Reference to the title in the XML.
    TextView wavesCompleted; //Reference to the waves completed in the XML.

    MediaPlayer endSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);

        /*Setup immersive fullscreen.*/
        getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);

       title = findViewById(R.id.title); //Store a reference to the title.
       wavesCompleted = findViewById(R.id.waves); //Store a reference to the waves completed text.

        /*When activity is loaded, check to see if the game was won. If it was, set the text and colour
        * of the title text accordingly.*/
        if (PreferenceManager.get().gameIsWon)
        {
            title.setText(getResources().getString(R.string.endStateWin));
            title.setTextColor(getResources().getColor(R.color.endColorWin));
        }
        else
        {
            title.setText(getResources().getString(R.string.endStateLose));
            title.setTextColor(getResources().getColor(R.color.endColorLose));
        }

        /*Set the text of waves completed to the number of waves completed.*/
        wavesCompleted.setText(String.format(getResources().getString(R.string.wavesCompleted), PreferenceManager.get().wavesCompleted));

        if (PreferenceManager.get().soundIsEnabled)
        {
            endSound = MediaPlayer.create(getApplicationContext(), R.raw.end);

            endSound.setVolume(0.0f, PreferenceManager.get().volume - 0.4f);

            endSound.start();
        }

    }

    /*Called via button click, returns to the main menu activity.*/
    public void backToMenu(View view)
    {
        endSound.stop();
        Intent intent = new Intent(GameEndActivity.this, MainMenu.class);
        startActivity(intent);
        finish();
    }


}