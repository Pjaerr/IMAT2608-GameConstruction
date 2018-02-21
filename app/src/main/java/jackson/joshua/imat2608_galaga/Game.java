package jackson.joshua.imat2608_galaga;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class Game extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
    }

    public void goToEndScreen(View view)
    {
        Intent intent = new Intent(Game.this, GameEnd.class);
        startActivity(intent);
        finish();
    }
}
