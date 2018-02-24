package Presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.view.MotionEvent;

import Model.Vector2i;
import jackson.joshua.imat2608_galaga.R;


import Model.Player;

/*The class that controls what exactly happens during each game loop but the
* workings of the game loop are abstracted away.*/
public class GameLoop
{
    private Point m_screenSize; //Reference to the screen dimensions.

    /*Player*/
    private Player player;
    private int movingDir = 0;

    /*Level Boundary Collisions*/
    private Collision collision; //Reference to Collision object, used to check for level boundary collisions.
    private Rect rightSideOfScreen;
    private Rect leftsideOfScreen;

    public GameLoop(Point screenSize, Context context)
    {
        m_screenSize = screenSize;

        collision = new Collision(); //Initialise the Collision object for level boundaries.

        /*Initialise the player with the ship sprite in the drawable folder. Place
        * the player half way along the x axis and 100 pixels above the bottom of
        * the screen with a width and height of 100.*/
        player = new Player(context.getResources().getDrawable(R.drawable.player_ship),
                 new Vector2i(m_screenSize.x / 2, m_screenSize.y - 100),
                 new Vector2i(100, 100));
    }

    /*Start() is called once before any other gameloop functions.*/
    public void Start()
    {
        setupLevelBoundaries();
    }

    /*Update() is called every loop.*/
    public void Update()
    {
        player.translate(5 * movingDir, 0);

        if (collision.RectInRect(player.getBoundingRect(), rightSideOfScreen)
                || collision.RectInRect(player.getBoundingRect(), leftsideOfScreen))
        {
            player.translate(5 * -movingDir, 0);
        }
    }

    /*Draw() is called every loop and contains a reference to the canvas that needs to be
    * drawn to.*/
    public void Draw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Draw background colour.
        player.draw(canvas);
    }

    /*Listen for touch events*/
    public void screenTouched(MotionEvent event)
    {
        int eventAction = event.getAction();

        switch (eventAction)
        {
            case MotionEvent.ACTION_MOVE:
                updatePlayerMovingDir((int)event.getX());
                break;
            case MotionEvent.ACTION_UP:
                movingDir = 0;
                break;
        }
    }

    /*If the left side of the screen is being touched, set the moving direction
    * to -1, if the right side, set it to 1.*/
    private void updatePlayerMovingDir(int touchX)
    {
        if (touchX > m_screenSize.x / 2)
        {
            movingDir = 1;
        }

        if (touchX < m_screenSize.x / 2)
        {
            movingDir = -1;
        }
    }

    private void setupLevelBoundaries()
    {
        int x = m_screenSize.x - 20;
        int y = m_screenSize.y - 100;

        rightSideOfScreen = new Rect(x, y, x + 100, y + 100);
        leftsideOfScreen = new Rect(-100, y, 0, y + 100);
    }
}
