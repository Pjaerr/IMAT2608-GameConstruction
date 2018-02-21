package jackson.joshua.imat2608_galaga;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;


public class MainMenu extends AppCompatActivity
{
    private DisplayMetrics m_displayMetrics = new DisplayMetrics();
    private int m_width;

    private ImageView shipSprite;

    private Handler handler = new Handler();

    /*Starts the game when the Start button is clicked.*/
    public void startGame(View view)
    {
        Intent intent = new Intent(MainMenu.this, Game.class);
        startActivity(intent);
        finish();
    }

    /*Opens the Options activity.*/
    public void openOptions(View view)
    {
        Intent intent = new Intent(MainMenu.this, Options.class);
        startActivity(intent);
    }

    int x = 0;
    boolean hasReachedMaxWidth = false;
    boolean hasReachedMinWidth = true;

    private void animateShipSprite()
    {
        if (x >= m_width)
        {
            hasReachedMaxWidth = true;
            hasReachedMinWidth = false;
        }
        else if (x <= 40)
        {
            hasReachedMinWidth = true;
            hasReachedMaxWidth = false;
        }

        if (!hasReachedMaxWidth)
        {
            x+=3;
        }
        else if (!hasReachedMinWidth)
        {
            x-=3;
        }

        shipSprite.setX(x);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        getWindowManager().getDefaultDisplay().getMetrics(m_displayMetrics);
        m_width = m_displayMetrics.widthPixels;
        shipSprite = findViewById(R.id.ship);

        handler.postDelayed(new Runnable()
        {
            public void run()
            {
                animateShipSprite();
                handler.postDelayed(this, 1);
            }
        }, 1);
    }
}
