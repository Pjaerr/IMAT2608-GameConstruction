package Presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.util.Log;
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
    private Player player; //Reference to the player.
    private int movingDir = 0; // Left: -1, Right: 1, Stationary: 0

    /*Level Boundary Collisions*/
    private LevelBoundaries levelBounds; //Contains the level boundaries and helper functions.

    /*Controls*/
    Controls controls;

    /*Enemies*/
    Enemy[] staticEnemies; //Array of static enemies.
    int xPlacement = 0; //Where to place each enemy on the X.
    int yPlacement = 100; //Where to place each enemy on the Y.

    /*Player's Bullets*/
    private Bullet[] playerBullets;

    public GameLoop(Point screenSize, Context context)
    {
        m_screenSize = screenSize;

        levelBounds = new LevelBoundaries(m_screenSize);

        /*Initialise the player with the ship sprite in the drawable folder. Place
        * the player half way along the x axis and 100 pixels above the bottom of
        * the screen with a width and height of 100.*/
        player = new Player(context.getResources().getDrawable(R.drawable.player_ship),
                 new Vector2i(m_screenSize.x / 2, m_screenSize.y - 100),
                 new Vector2i(100, 100));

        setupEnemies(context, 7);
        setupPlayerBullets(context, 5);


    }

    /*Start() is called once before any other gameloop functions.*/
    public void Start()
    {
        setupControls();
    }

    private void setupControls()
    {
        controls = new Controls();

        Rect moveLeftButton = new Rect(-20, ((m_screenSize.y - m_screenSize.y / 3)),
                -20 + m_screenSize.x / 2 - 20, ((m_screenSize.y - m_screenSize.y / 3)) + m_screenSize.y);

        Rect moveRightButton = new Rect(m_screenSize.x / 2 + 20, ((m_screenSize.y - m_screenSize.y / 3)),
                (m_screenSize.x / 2  + 20) + m_screenSize.x, ((m_screenSize.y - m_screenSize.y / 3) + m_screenSize.y));

        Rect fireButton = new Rect(0, m_screenSize.y / 2, 400,
                m_screenSize.y / 2 + 310);

        controls.setupMoveLeftButton(moveLeftButton);
        controls.setupMoveRightButton(moveRightButton);
        controls.setupFireButton(fireButton);
    }


    int frame = 0; //Increased by 1 every time Update is called.

    /*Update() is called every loop.*/
    public void Update()
    {
        player.translate(5 * movingDir, 0);

        if (levelBounds.isCollidingWithLeft(player.getBoundingRect())
                || levelBounds.isCollidingWithRight(player.getBoundingRect()))
        {
            player.translate(5 * -movingDir, 0);
        }

        for (int i = 0; i < staticEnemies.length; i++)
        {
            staticEnemies[i].moveBetween(levelBounds.left, levelBounds.right);
        }

        for (int i = 0; i < playerBullets.length; i++)
        {
            playerBullets[i].update();
        }

        if (frame % fireRate == 0)
        {
            okayToFire = true;
        }

        frame++;
    }


    Rect touchRect = new Rect(0, 0, 0, 0);
    /*Draw() is called every loop and contains a reference to the canvas that needs to be
    * drawn to.*/
    public void Draw(Canvas canvas)
    {
        canvas.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR); //Draw background colour.

        controls.draw(canvas);

        player.draw(canvas); //Draw the player.

        /*Draw all of the players bullets if they are in motion.*/
        for (int i = 0; i < playerBullets.length; i++)
        {
            if (playerBullets[i].isActive)
            {
                playerBullets[i].draw(canvas);
            }

        }

        /*Draw all of the static enemies.*/
        for (int i =0; i < staticEnemies.length; i++)
        {
            staticEnemies[i].draw(canvas);
        }


    }

    /*Listen for touch events*/
    public void screenTouched(MotionEvent event)
    {
        int eventAction = event.getActionMasked();
        int test = event.getActionIndex();

        if (eventAction == MotionEvent.ACTION_DOWN)
        {
            fireBullet((int)event.getX(), (int)event.getY());
        }

        if (eventAction == MotionEvent.ACTION_MOVE)
        {
            updatePlayerMovingDir((int)event.getX(), (int)event.getY());
        }

        if (eventAction == MotionEvent.ACTION_UP)
        {
            movingDir = 0;
        }
    }

    /*Places enemies in the level. Gets called once when GameLoop is constructed.*/
    private void setupEnemies(Context context, int numberOfEnemies)
    {
        /*Start X at 0 and increase by 40 until m_screenSize.x - 200 is reached. Move along X until
        * no more space, then move down to the Y.*/
        /*Start Y at 100 and increase by 100 until 500 is reached.*/

        staticEnemies = new Enemy[numberOfEnemies];

        for (int i = 0; i < staticEnemies.length; i++)
        {
            staticEnemies[i] = new Enemy(context.getResources().getDrawable(R.drawable.enemy1_a), new Vector2i(xPlacement, yPlacement), new Vector2i(100, 100));

            if (xPlacement < m_screenSize.x - 200)
            {
                xPlacement += 300;
            }
            else
            {
                xPlacement = 0;

                if (yPlacement < 500)
                {
                    yPlacement += 100;
                }
            }
        }
    }

    /*Create the bullets the player has.*/
    private void setupPlayerBullets(Context context, int numberOfBullets)
    {
        playerBullets = new Bullet[numberOfBullets];

        for (int i = 0; i < playerBullets.length; i++)
        {
            playerBullets[i] = new Bullet(context.getResources().getDrawable(R.drawable.player_missle),
                    player.getPos(), new Vector2i(100, 100), levelBounds.top, -1);
        }
    }

    /*If the left side of the screen is being touched, set the moving direction
    * to -1, if the right side, set it to 1.*/
    private void updatePlayerMovingDir(int touchX, int touchY)
    {
        touchRect = new Rect(touchX, touchY, touchX + 10, touchY + 10);

       if (controls.isTouchingMoveRightButton(touchRect))
       {
           movingDir = 1;
       }

        if (controls.isTouchingMoveLeftButton(touchRect))
        {
            movingDir = -1;
        }
    }



    int fireRate = 30; //How many frames between shots.
    boolean okayToFire = true; //It's okay to fire another bullet.

    private void fireBullet(int touchX, int touchY)
    {
        touchRect = new Rect(touchX, touchY, touchX + 10, touchY + 10);

        if (controls.isTouchingFireButton(touchRect))
        {
            if (okayToFire)
            {
                for (int i = 0; i < playerBullets.length; i++)
                {
                /*Loop through all of the player's bullets until a bullet
                * that is not currently in motion is found.*/
                    if (!playerBullets[i].isActive)
                    {
                        playerBullets[i].fire(player.getPos()); //Fire a bullet from the players position.

                        okayToFire = false; //Wait a set amount of time before allowing to fire again.

                        i = playerBullets.length; //Break out of the loop.
                    }
                }
            }
        }
    }
}
