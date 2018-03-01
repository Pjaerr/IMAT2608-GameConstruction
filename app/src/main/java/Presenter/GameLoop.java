package Presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Button;

import java.util.Random;
import java.util.Vector;

import Model.Bullet;
import Model.Enemy;
import Model.LevelBoundaries;
import Model.PreferenceManager;
import Model.Vector2f;
import View.GameActivity;
import View.GameEndActivity;
import jackson.joshua.imat2608_galaga.R;

import Model.Player;

/*The class that controls what exactly happens during each game loop but the
* workings of the game loop are abstracted away.*/
class GameLoop
{
    /*Stuff taken from parent activity.*/
    private Context m_context; //Reference to the parent activity context.
    private Point m_screenSize; //Reference to the screen dimensions.



    /*Player*/
    private Player player; //Reference to the player.
    private int movingDir = 0; // Left: -1, Right: 1, Stationary: 0



    /*Level Boundaries and Collisions*/
    private LevelBoundaries levelBounds; //Contains the level boundaries and helper functions.
    private Collision collision;



    /*Controls*/
    private Controls controls;
    private Rect touchRect = new Rect(0, 0, 0, 0); //Rectangle covering what is being touched right now.



    /*Enemies*/
    private Vector<Enemy> staticEnemies; //Static Enemies.
    private Bullet[] staticEnemyBullets; //Array of bullets corresponding to number of static enemies.
    private boolean staticEnemiesIsEmpty = true; //Are there any static enemies left?
    private Vector2f staticEnemyPlacement = new Vector2f(0, 100); //Where to place static enemies on the X and Y.
    private int m_numberOfStaticEnemies = 4;
    private int m_staticEnemyLives = 1;



    /*Player's Bullets*/
    private Bullet[] playerBullets;



    /*For random number generation.*/
    private Random rand = new Random();
    private int chanceToFireBullet = 500; //The chance an enemy fires a bullet in any given frame. 1 in chanceToFireBullet.



    /*Wave Stuff*/
    private int waveNumber = 1;

    /*Sounds*/
    MediaPlayer playerBlaster;
    MediaPlayer enemyBlaster;
    MediaPlayer enemyDeath;
    MediaPlayer playerLosesLife;

    GameLoop(Point screenSize, Context context)
    {
        m_context = context;
        m_screenSize = screenSize;
        levelBounds = new LevelBoundaries(m_screenSize);
        collision = new Collision();

        /*Initialise the player with the ship sprite in the drawable folder. Place
        * the player half way along the x axis and 100 pixels above the bottom of
        * the screen with a width and height of 100.*/
        player = new Player(m_context.getResources().getDrawable(R.drawable.player_ship),
                 new Vector2f(m_screenSize.x / 2, m_screenSize.y - 100),
                 new Vector2f(100, 100));

        setupEnemies(m_numberOfStaticEnemies, m_staticEnemyLives); //Setup the enemies.
        setupPlayerBullets(5); //Give the player 5 bullets.

        if (PreferenceManager.get().soundIsEnabled)
        {
            playerBlaster = MediaPlayer.create(m_context, R.raw.player_blaster);
            enemyBlaster = MediaPlayer.create(m_context, R.raw.enemy_blaster);
            enemyDeath = MediaPlayer.create(m_context, R.raw.enemy1death);
            playerLosesLife = MediaPlayer.create(m_context, R.raw.explosion);

            if (playerBlaster != null)
            {
                playerBlaster.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (enemyBlaster != null)
            {
                enemyBlaster.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (enemyDeath != null)
            {
                enemyDeath.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (playerLosesLife != null)
            {
                playerLosesLife.setVolume(0.0f, PreferenceManager.get().volume);
            }
        }
    }



    /*Start() is called once before any other gameloop functions.*/
    void Start()
    {
        setupControls(); //Create the controls on screen.

        background = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.space_background);
    }


    Handler handler = new Handler();
    int count = 0;

    private boolean playerIsDead = false;

    private int frame = 0; //Increased by 1 every time Update is called.

    /*Update() is called every loop.*/
    void Update(float deltaTime)
    {
        for (int i = 0; i < staticEnemies.size(); i++)
        {
            staticEnemies.elementAt(i).startAnimation("move_anim",500);
        }

        if (staticEnemiesIsEmpty) //Can change to check for dynamic enemies in the future too.
        {
            newWave(); //Launch a new wave.
        }

        if (!playerIsDead)
        {


            /*
            * Move the player.
            * Check for collisions with level boundaries.
            * */
            updatePlayer(deltaTime);

            if (!staticEnemiesIsEmpty) //If there are still static enemies in the level.
            {
                /*
                * Moves static enemies.
                * Checks if any of the player's bullets have hit a static enemy.
                * Controls static enemies shooting their bullets.
                * */
                updateStaticEnemies(deltaTime);
            }

            /*
            * Moves each bullet if needed.
            * Checks if a static enemy bullet has hit the player.
            * */
            updateBullets(deltaTime);

            /*Only allow the player to fire a bullet everytime a certain
            * amount of frames has passed.*/
            if (frame % fireRate == 0)
            {
                okayToFire = true;
            }

            frame++; //Increase the frame count.
        }
    }

    private Paint paint = new Paint();

    Bitmap background;

    void Draw(Canvas canvas)
    {
        canvas.drawBitmap(background, null, new Rect(0, 0, m_screenSize.x, m_screenSize.y), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        canvas.drawText("Lives: " + player.getLives(), 0, m_screenSize.y / 2 - 20, paint);
        canvas.drawText("Wave: " + waveNumber, 200, m_screenSize.y / 2 - 20, paint);

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

    /*Draw all of the static enemy's bullets if they are in motion.*/
        for (int i = 0; i < staticEnemyBullets.length; i++)
        {
            if (staticEnemyBullets[i].isActive)
            {
                staticEnemyBullets[i].draw(canvas);
            }
        }

    /*Draw all of the static enemies.*/
        for (int i =0; i < staticEnemies.size(); i++)
        {
            staticEnemies.elementAt(i).draw(canvas);
        }
    }



    //----------SETUP FUNCTIONS----------

    /*Places enemies in the level. Gets called once when GameLoop is constructed.*/
    private void setupEnemies(int numberOfEnemies, int numberOfLives)
    {
        /*Start X at 0 and increase by 40 until m_screenSize.x - 200 is reached. Move along X until
        * no more space, then move down to the Y.*/
        /*Start Y at 100 and increase by 100 until 500 is reached.*/

        staticEnemies = new Vector<Enemy>(numberOfEnemies);

        staticEnemiesIsEmpty = false;

        staticEnemyBullets = new Bullet[numberOfEnemies];

        for (int i = 0; i < numberOfEnemies; i++)
        {
            Resources res = m_context.getResources();

            Bitmap bmp = BitmapFactory.decodeResource(res, R.drawable.enemy1_moveanim);

            Vector2f pos = new Vector2f (staticEnemyPlacement.x, staticEnemyPlacement.y);
            Vector2f scale = new Vector2f(100, 100);

            Enemy nextEnemy = new Enemy(res, bmp, 2, 1, "move_anim", pos, scale);

            staticEnemies.addElement(nextEnemy);

            staticEnemyBullets[i] = new Bullet(m_context.getResources().getDrawable(R.drawable.enemy_missle),
                    new Vector2f(staticEnemyPlacement.x, staticEnemyPlacement.y), new Vector2f(100, 100), levelBounds.bottom, 1);

            staticEnemies.get(i).setNumberOfLives(numberOfLives);

            if (staticEnemyPlacement.x < m_screenSize.x - 400)
            {
                staticEnemyPlacement.x += 400;
            }
            else
            {
                staticEnemyPlacement.x = 0;

                if (staticEnemyPlacement.y < 500)
                {
                    staticEnemyPlacement.y += 100;
                }
            }
        }
    }

    /*Create the bullets the player has.*/
    private void setupPlayerBullets(int numberOfBullets)
    {
        playerBullets = new Bullet[numberOfBullets];

        for (int i = 0; i < playerBullets.length; i++)
        {
            playerBullets[i] = new Bullet(m_context.getResources().getDrawable(R.drawable.player_missle),
                    player.getPos(), new Vector2f(100, 100), levelBounds.top, -1);

            playerBullets[i].setMovementSpeed(35);
        }
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


    //----------UPDATE FUNCTIONS----------

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

    private void updatePlayer(float deltaTime)
    {
        //Move the player left or right depending on their movement direction.
        player.translate((10 * movingDir) * deltaTime, 0);

        //If the player is touch the left or right boundary.
        if (levelBounds.isCollidingWithLeft(player.getBoundingRect())
                || levelBounds.isCollidingWithRight(player.getBoundingRect()))
        {
            //Move them the other direction by the same amount they moved. (Keeping it at a standstill).
            player.translate((10 * -movingDir) * deltaTime, 0);
        }
    }

    private void updateStaticEnemies(float deltaTime)
    {
        /*For every static enemy in the static enemy's vector.
        * (Loops backwards because elements have the potential to be removed.*/
        for (int i = staticEnemies.size() - 1; i >= 0; i--)
        {
            /*Generate a random number between chanceToFirebullet and 1*/
            int randomNumber = rand.nextInt(chanceToFireBullet + 1);

            /*Move every enemy between the left and right level boundaries.*/
            staticEnemies.get(i).moveBetween(levelBounds.left, levelBounds.right, deltaTime);

            //1 in 'chanceToFireBullet' odds of firing the current enemy's bullet.
            if (randomNumber == 1)
            {
                if (!staticEnemyBullets[i].isActive) //If the current enemy's bullet isn't already moving.
                {
                    //Fire it downwards from the current enemy's position.
                    staticEnemyBullets[i].fire(staticEnemies.get(i).getPos());

                    if (enemyBlaster != null)
                    {
                        enemyBlaster.start();
                    }
                }
            }

            /*For every bullet the player has. (Occurs for every static enemy)*/
            for (int j = 0; j < playerBullets.length; j++)
            {
                if (playerBullets[j].isActive) //If the current bullet is moving.
                {
                    //Has the current bullet collided with the current static enemy?
                    if (collision.RectInRect(playerBullets[j].getBoundingRect(), staticEnemies.elementAt(i).getBoundingRect()))
                    {
                        playerBullets[j].isActive = false; //Disable the bullet that has collided.

                        staticEnemies.get(i).removeLife(); //Remove a life from the enemy that was hit.

                        if (staticEnemies.get(i).isDead()) //If that enemy's lives are <= 0.
                        {
                            if (enemyDeath != null)
                            {
                                enemyDeath.start();
                            }

                            staticEnemies.removeElementAt(i); //Remove enemy from Vector of static enemies.

                            /*Exit loop as element has been removed from it.*/
                            j = playerBullets.length;
                            i = -1;

                            if (staticEnemies.size() <= 0)
                            {
                                staticEnemiesIsEmpty = true;
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateBullets(float deltaTime)
    {
        /*For every bullet the player has.*/
        for (int i = 0; i < playerBullets.length; i++)
        {
            playerBullets[i].update(deltaTime); //Move if active.
        }

        /*For every static enemy bullet.*/
        for (int i = 0; i < staticEnemyBullets.length; i++)
        {
            staticEnemyBullets[i].update(deltaTime); //Move if active.

            /*If the current bullet is active.*/
            if (staticEnemyBullets[i].isActive)
            {
                /*Is it colliding with the player?*/
                if (collision.RectInRect(staticEnemyBullets[i].getBoundingRect(), player.getBoundingRect()))
                {
                    staticEnemyBullets[i].isActive = false; //Disable the bullet that collided.

                    player.removeLife(); //Remove a life from the player.

                    playerIsDead = true;

                    handler.postDelayed(new Runnable()
                    {
                        public void run()
                        {
                            if (count < 2)
                            {
                                //Flash player.
                                player.setActive();
                                count++;
                                handler.postDelayed(this, 500);
                            }
                            else
                            {
                                playerIsDead = false;
                                count = 0;
                                handler.removeCallbacks(this);
                            }

                        }
                    }, 500);

                    if (playerLosesLife != null)
                    {
                        playerLosesLife.start();
                    }



                    if (player.isDead()) //If the player's lives are <= 0.
                    {
                        endGame(false); //End the game with a loss.
                    }
                }
            }
        }
    }





    //----------ACTION FUNCTIONS----------

    private int fireRate = 30; //How many frames between shots.
    private boolean okayToFire = true; //It's okay to fire another bullet.


    private void fireBullet(int touchX, int touchY)
    {
        touchRect = new Rect(touchX, touchY, touchX + 10, touchY + 10);

        if (controls.isTouchingFireButton(touchRect))
        {
            if (okayToFire)
            {
                if (playerBlaster != null)
                {
                    playerBlaster.start();
                }

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

    private void newWave()
    {
        waveNumber++;

        /*Update number of static enemies, and their lives.*/
        if (m_numberOfStaticEnemies <= 12)
        {
            m_numberOfStaticEnemies += 2;

            if (waveNumber == 5)
            {
                m_staticEnemyLives += 1;
                m_numberOfStaticEnemies -= 6;
            }

            if (waveNumber == 7)
            {
                chanceToFireBullet = 300;
            }
        }
        else
        {
            endGame(true); //Can put no more enemies on screen, you win.
        }



        staticEnemyPlacement = new Vector2f(0, 100);

        setupEnemies(m_numberOfStaticEnemies, m_staticEnemyLives);
    }

    private void endGame(boolean gameIsWon)
    {
        PreferenceManager.get().gameIsWon = gameIsWon;
        PreferenceManager.get().wavesCompleted = waveNumber;

        Intent intent = new Intent(m_context, GameEndActivity.class);
        m_context.startActivity(intent);
        ((Activity)m_context).finish();

    }





    //----------EVENT LISTENER FUNCTIONS----------

    /*Listen for touch events*/
    public void screenTouched(MotionEvent event)
    {
        int eventAction = event.getActionMasked();

        if (eventAction == MotionEvent.ACTION_DOWN)
        {
            //Add pause code here that checks first if action up bla bla

            updatePlayerMovingDir((int)event.getX(), (int)event.getY());
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

}
