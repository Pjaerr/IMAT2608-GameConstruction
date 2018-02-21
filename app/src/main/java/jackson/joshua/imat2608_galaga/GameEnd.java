package jackson.joshua.imat2608_galaga;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class GameEnd extends AppCompatActivity
{
    TextView title;
    TextView wavesCompleted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_end);



       title = findViewById(R.id.title);
       wavesCompleted = findViewById(R.id.waves);


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

        wavesCompleted.setText(String.format(getResources().getString(R.string.wavesCompleted), PreferenceManager.get().wavesCompleted));
    }

    public void backToMenu(View view)
    {
        Intent intent = new Intent(GameEnd.this, MainMenu.class);
        startActivity(intent);
        finish();
    }


}