package View;

import android.content.Intent;
import android.graphics.Point;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import Model.PreferenceManager;
import Presenter.GameView;

/*The class that holds the GameView itself and it used for switching between activities.
* No game logic should be put in here, it should all go through GameView and GameLoop.*/
public class GameActivity extends AppCompatActivity
{
    private GameView gameView; //Reference to the GameView.

   @Override
    protected void onCreate(Bundle savedInstanceState)
   {
       super.onCreate(savedInstanceState);

       /*Setup fullscreen immersive window.*/
       getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);

       /*Get the screensize and pass the metrics to Point screenSize.*/
       Point screenSize = new Point();
       this.getWindowManager().getDefaultDisplay().getRealSize(screenSize);

        /*Create the GameActivity View and set it as this Activity's view.*/
       gameView = new GameView(this, screenSize);
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
       gameView.resume(); //Resume the thread the game view is running on.
   }



   /*Should be called when the game has ended. Causing the activity to switch to the
   * end screen.*/
    public void goToEndScreen(View view)
    {
        Intent intent = new Intent(GameActivity.this, GameEndActivity.class);
        startActivity(intent);
        finish();
    }
}