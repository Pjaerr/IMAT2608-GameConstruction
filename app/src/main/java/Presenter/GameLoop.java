package Presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.MotionEvent;

import Model.Bullet;
import Model.Enemy;
import Model.LevelBoundaries;
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
    private LevelBoundaries levelBounds;

    /*Enemies*/
    Enemy[] enemies;

    /*Player's Bullets*/
    private Bullet[] bullets = new Bullet[5];

    public GameLoop(Point screenSize, Context context)
    {
        m_screenSize = screenSize;

        collision = new Collision(); //Initialise the Collision object for level boundaries.

        levelBounds = new LevelBoundaries(m_screenSize);

        /*Initialise the player with the ship sprite in the drawable folder. Place
        * the player half way along the x axis and 100 pixels above the bottom of
        * the screen with a width and height of 100.*/
        player = new Player(context.getResources().getDrawable(R.drawable.player_ship),
                 new Vector2i(m_screenSize.x / 2, m_screenSize.y - 100),
                 new Vector2i(100, 100));

        int x = 0;
        int y = 100;

        /*Start X at 0 and increase by 40 until m_screenSize.x - 80 is reached. Move along X until
        * no more space, then move down to the Y.*/
        /*Start Y at 100 and increase by 100 until 500 is reached.*/

        enemies = new Enemy[8];

        for (int i = 0; i < enemies.length; i++)
        {
            enemies[i] = new Enemy(context.getResources().getDrawable(R.drawable.enemy1_a), new Vector2i(x, y), new Vector2i(100, 100));
            if (x < m_screenSize.x - 200)
            {
                x += 300;
            }
            else{
                x = 0;
                if (y < 500)
                {
                    y += 100;
                }
            }
        }

        for (int i = 0; i < bullets.length; i++)
        {
            bullets[i] = new Bullet(context.getResources().getDrawable(R.drawable.player_missle),
                    player.getPos(), new Vector2i(100, 100), levelBounds.top);
        }
    }

    /*Start() is called once before any other gameloop functions.*/
    public void Start()
    {

    }


    int frame = 0;
    /*Update() is called every loop.*/
    public void Update()
    {
        player.translate(5 * movingDir, 0);

        if (levelBounds.isCollidingWithLeft(player.getBoundingRect())
                || levelBounds.isCollidingWithRight(player.getBoundingRect()))
        {
            player.translate(5 * -movingDir, 0);
        }

        for (int i = 0; i < enemies.length; i++)
        {
            enemies[i].moveBetween(levelBounds.left, levelBounds.right);
        }

        for (int i = 0; i < bullets.length; i++)
        {
            bullets[i].update();
        }

        if (frame % fireRate == 0)
        {
            okayToFire = true;
        }

        frame++;
    }


    /*Draw() is called every loop and contains a reference to the canvas that needs to be
    * drawn to.*/
    public void Draw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Draw background colour.
        player.draw(canvas);

        for (int i = 0; i < bullets.length; i++)
        {
            if (bullets[i].isActive)
            {
                bullets[i].draw(canvas);
            }

        }

        for (int i =0; i < enemies.length; i++)
        {
            enemies[i].draw(canvas);
        }

        //levelBounds.draw(canvas);
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
            case MotionEvent.ACTION_DOWN:
                fireBullet();
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



    int fireRate = 30; //How many frames between shots.
    boolean okayToFire = true;

    private void fireBullet()
    {
        if (okayToFire)
        {
            for (int i = 0; i < bullets.length; i++)
            {
                if (!bullets[i].isActive)
                {
                    bullets[i].fire(player.getPos());
                    okayToFire = false;
                    i = bullets.length;
                }
            }
        }
    }
}
