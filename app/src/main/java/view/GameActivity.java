package view;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import model.PreferenceManager;
import presenter.GameView;
import android.view.View;
import jackson.joshua.imat2608_galaga.R;

/*The class that holds the GameView itself and it used for switching between activities.
* No game logic should be put in here, it should all go through GameView and GameLoop.*/
public class GameActivity extends AppCompatActivity
{
    private GameView gameView; //Reference to the GameView.

   @Override
    protected void onCreate(Bundle savedInstanceState)
   {
       /*Setup fullscreen immersive window.*/
       getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);

       super.onCreate(savedInstanceState);


           /*Get the screensize and pass the metrics to Point screenSize.*/
           Point screenSize = new Point();
           this.getWindowManager().getDefaultDisplay().getRealSize(screenSize);


           gameView = new GameView(this, screenSize);

       /*Create the GameActivity View and set it as this Activity's view.*/
       setContentView(gameView);

   }

   public void pauseGame()
   {
       gameView.pause();

       setContentView(R.layout.activity_pause_menu);
   }
   public void resumeGame(View view)
   {
       gameView.resume();
       setContentView(gameView);
   }

   /*When this activity loses focus.*/
   @Override
   protected void onPause()
   {
       super.onPause();
       gameView.pause(); //Pause the thread the game view is running on.
   }

   /*When this activity loses focus.*/
   @Override
   protected void onResume()
   {
       super.onResume();

       getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);
       gameView.resume(); //Resume the thread the game view is running on.
   }

   public void backToMenu(View view)
   {
       Intent intent = new Intent(GameActivity.this, MainMenu.class);
       startActivity(intent);
       finish();
   }
}
