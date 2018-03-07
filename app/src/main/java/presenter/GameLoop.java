package presenter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.MotionEvent;

import java.util.Random;
import java.util.Vector;

import model.Bullet;
import model.Enemy;
import model.LevelBoundaries;
import model.PreferenceManager;
import model.Vector2f;
import view.GameActivity;
import view.GameEndActivity;
import jackson.joshua.imat2608_galaga.R;

import model.Player;

/*The class that controls what exactly happens during each game loop but the
* workings of the game loop are abstracted away.*/
class GameLoop {
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

    //Static Enemies
    private Vector<Enemy> staticEnemies; //Static Enemies.
    private Bullet[] staticEnemyBullets; //Array of bullets corresponding to number of static enemies.
    private boolean staticEnemiesIsEmpty = true; //Are there any static enemies left?
    private Vector2f staticEnemyPlacement = new Vector2f(0, 100); //Where to place static enemies on the X and Y.
    private int m_numberOfStaticEnemies = 4;

    private int m_enemyLives = 1;

    //Dynamic Enemies
    Vector<Enemy> dynamicEnemies;
    private boolean dynamicEnemiesIsEmpty = true;
    private int m_numberOfDynamicEnemies = 1;
    Path pathToFollowRight;
    Path pathToFollowLeft;
    Path bottomUpDiagonal;
    Path topDownDiagonal;


    Path dynamicEnemyPath;

    /*Player's Bullets*/
    private Bullet[] playerBullets;


    /*For random number generation.*/
    private Random rand = new Random();
    private int chanceToFireBullet = 450; //The chance an enemy fires a bullet in any given frame. 1 in chanceToFireBullet.


    /*Score Stuff*/
    private int waveNumber = 1;
    private int enemiesKilled = 0;


    /*Sounds*/

    MediaPlayer playerBlaster;
    MediaPlayer enemyBlaster;
    MediaPlayer enemyDeath;
    MediaPlayer playerLosesLife;

    /*Canvas Drawing*/
    private Paint paint = new Paint();
    Bitmap background;

    GameLoop(Point screenSize, Context context) {
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

        pathToFollowRight = new Path();
        pathToFollowLeft = new Path();
        bottomUpDiagonal = new Path();
        topDownDiagonal = new Path();

        setupEnemies(m_numberOfStaticEnemies, m_numberOfDynamicEnemies, m_enemyLives); //Setup the enemies.
        setupPlayerBullets(5); //Give the player 5 bullets.

        setupMediaPlayers();
    }

    /*Start() is called once before any other gameloop functions.*/
    void Start() {
        setupControls(); //Create the controls on screen.

        background = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.space_background);
    }

    Handler handler = new Handler();
    int count = 0;

    private boolean playerIsDead = false;

    /*Update() is called every loop.*/
    void Update(float deltaTime, float frame)
    {
        for (int i = 0; i < staticEnemies.size(); i++)
        {
            staticEnemies.elementAt(i).startAnimation("move_anim", 500, true);
        }

        for (int i = 0; i < dynamicEnemies.size(); i++)
        {
            dynamicEnemies.elementAt(i).startAnimation("move_anim", 500, true);
        }

        if (staticEnemiesIsEmpty && dynamicEnemiesIsEmpty) //Can change to check for dynamic enemies in the future too.
        {
            newWave(); //Launch a new wave.
        }

        if (!playerIsDead) {


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

            if (!dynamicEnemiesIsEmpty) {
                updateDynamicEnemies(deltaTime);
            }

            /*
            * Moves each bullet if needed.
            * Checks if a static enemy bullet has hit the player.
            * */
            updateBullets(deltaTime);
        }

        /*Dynamic enemies will still do everything they are supposed to, even if the player has died. This is
        * because they don't shoot bullets and so whilst the player is inactive, they won't get damaged  and it allows the
        * dynamic enemy to move out of the collision area for the next frame.*/
        if (!dynamicEnemiesIsEmpty) {
            updateDynamicEnemies(deltaTime);
        }
    }

    void Draw(Canvas canvas) {
        canvas.drawBitmap(background, null, new Rect(0, 0, m_screenSize.x, m_screenSize.y), paint);

        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        canvas.drawText("Lives: " + player.getLives(), 0, m_screenSize.y / 2 - 20, paint);
        canvas.drawText("Wave: " + waveNumber, 200, m_screenSize.y / 2 - 20, paint);

        controls.draw(canvas);

        player.draw(canvas); //Draw the player.

    /*Draw all of the players bullets if they are in motion.*/
        for (int i = 0; i < playerBullets.length; i++) {
            if (playerBullets[i].isActive) {
                playerBullets[i].draw(canvas);
            }
        }

    /*Draw all of the static enemy's bullets if they are in motion.*/
        for (int i = 0; i < staticEnemyBullets.length; i++) {
            if (staticEnemyBullets[i].isActive) {
                staticEnemyBullets[i].draw(canvas);
            }
        }

    /*Draw all of the static enemies.*/
        for (int i = 0; i < staticEnemies.size(); i++) {
            staticEnemies.elementAt(i).draw(canvas);
        }

        /*Draw all of the dynamic enemies.*/
        for (int i = dynamicEnemies.size() - 1; i >= 0; i--)
        {
            dynamicEnemies.elementAt(i).draw(canvas);
        }
    }

    //----------SETUP FUNCTIONS----------

    /*Places enemies in the level. Gets called once when GameLoop is constructed.*/
    private void setupEnemies(int numberOfStaticEnemies, int numberOfDynamicEnemies, int numberOfLives) {
        setupStaticEnemies(numberOfStaticEnemies, numberOfLives);
        setupDynamicEnemies(numberOfDynamicEnemies, numberOfLives);
    }

    private void setupStaticEnemies(int numberOfEnemies, int numberOfLives) {
        /*Start X at 0 and increase by 40 until m_screenSize.x - 200 is reached. Move along X until
        * no more space, then move down to the Y.*/
        /*Start Y at 100 and increase by 100 until 500 is reached.*/

        staticEnemies = new Vector<Enemy>(numberOfEnemies);

        staticEnemiesIsEmpty = false;

        staticEnemyBullets = new Bullet[numberOfEnemies];

        int xOffset = 2;
        int yOffset = 2;

        for (int i = 0; i < numberOfEnemies; i++) {
            Bitmap moveAnimBmp = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.enemy1_moveanim);

            Vector2f pos;

            int randomNumber = rand.nextInt(0 + 2);

            if (randomNumber == 1)
            {
                 pos = new Vector2f(staticEnemyPlacement.x + xOffset, staticEnemyPlacement.y + yOffset);
            }
            else
            {
                pos = new Vector2f(staticEnemyPlacement.x, staticEnemyPlacement.y);
            }

            xOffset += 8;
            yOffset += 4;

            Vector2f scale = new Vector2f(100, 100);

            Enemy nextEnemy = new Enemy(m_context.getResources(), moveAnimBmp, 2, 1, "move_anim", pos, scale);


            staticEnemies.addElement(nextEnemy);

            staticEnemyBullets[i] = new Bullet(m_context.getResources().getDrawable(R.drawable.enemy_missle),
                    new Vector2f(staticEnemyPlacement.x, staticEnemyPlacement.y), new Vector2f(100, 100), levelBounds.bottom, 1);

            staticEnemies.get(i).setNumberOfLives(numberOfLives);

            if (staticEnemyPlacement.x < m_screenSize.x - 400) {
                staticEnemyPlacement.x += 400;
            } else {
                staticEnemyPlacement.x = 0;

                if (staticEnemyPlacement.y < 500) {
                    staticEnemyPlacement.y += 100;
                }
            }
        }
    }

    private void setupDynamicEnemies(int numberOfEnemies, int numberOfLives)
    {
        setupDynamicEnemyPaths();


        dynamicEnemies = new Vector<Enemy>(numberOfEnemies);

        for (int i = 0; i < numberOfEnemies; i++) {
            Bitmap moveAnimBmp = BitmapFactory.decodeResource(m_context.getResources(), R.drawable.enemy2_moveanim);

            Vector2f pos = new Vector2f(m_screenSize.x * 2, m_screenSize.y * 2);
            Vector2f scale = new Vector2f(100, 100);

            Enemy nextEnemy = new Enemy(m_context.getResources(), moveAnimBmp, 2, 1, "move_anim", pos, scale);


            dynamicEnemies.addElement(nextEnemy);
        }


        dynamicEnemiesIsEmpty = false;
        currentStep = 0;
    }

    private void setupDynamicEnemyPaths()
    {
        pathToFollowRight.addOval(new RectF(-(m_screenSize.x * 0.1f), m_screenSize.y / 2, m_screenSize.x - (m_screenSize.x * 0.2f), m_screenSize.y + (m_screenSize.y * 0.3f))
                , Path.Direction.CCW);

        pathToFollowLeft.addOval(new RectF(-(m_screenSize.x * 0.1f), m_screenSize.y / 2, m_screenSize.x - (m_screenSize.x * 0.2f), m_screenSize.y + (m_screenSize.y * 0.3f))
                , Path.Direction.CW);

        bottomUpDiagonal.moveTo(m_screenSize.x, m_screenSize.y);
        bottomUpDiagonal.lineTo(m_screenSize.x / 2, m_screenSize.y / 2);
        bottomUpDiagonal.lineTo(0, 0);
        bottomUpDiagonal.close();

        topDownDiagonal.moveTo(m_screenSize.x, 0);
        topDownDiagonal.lineTo(m_screenSize.x / 2, m_screenSize.y / 2);
        topDownDiagonal.lineTo(0, m_screenSize.y);
        topDownDiagonal.close();

        int randomNumber = rand.nextInt(0 + 5);

        if (randomNumber == 0)
        {
            dynamicEnemyPath = pathToFollowRight;
        }
        else if (randomNumber == 1)
        {
            dynamicEnemyPath = pathToFollowLeft;
        }
        else if (randomNumber == 2)
        {
            dynamicEnemyPath = bottomUpDiagonal;
        }
        else
        {
            dynamicEnemyPath = topDownDiagonal;
        }

        pm = new PathMeasure(dynamicEnemyPath, false);

        segmentLength = pm.getLength() / numberOfPoints;
        newPos = new Vector2f(0.0f, 0.0f);
    }

    /*Create the bullets the player has.*/
    private void setupPlayerBullets(int numberOfBullets) {
        playerBullets = new Bullet[numberOfBullets];

        for (int i = 0; i < playerBullets.length; i++) {
            playerBullets[i] = new Bullet(m_context.getResources().getDrawable(R.drawable.player_missle),
                    player.getPos(), new Vector2f(100, 100), levelBounds.top, -1);

            playerBullets[i].setMovementSpeed(35);
        }
    }

    private void setupControls() {
        controls = new Controls();

        /*Setup Move Left Button*/
        Rect leftButtonTouchArea = new Rect(-20, ((m_screenSize.y - m_screenSize.y / 3)),
                -20 + m_screenSize.x / 2 - 20, ((m_screenSize.y - m_screenSize.y / 3)) + m_screenSize.y);
        Drawable leftButtonImage = m_context.getResources().getDrawable(R.drawable.arrow_left);
        Vector2f leftButtonPos = new Vector2f(-20, m_screenSize.y - m_screenSize.y / 3);
        Vector2f leftButtonScale = new Vector2f((m_screenSize.x / 2 - 20) - leftButtonPos.x, m_screenSize.y - leftButtonPos.y);

        controls.setupMoveLeftButton(leftButtonImage, leftButtonPos, leftButtonScale, leftButtonTouchArea);

        /*Setup Move Right Button*/
        Rect rightButtonTouchArea = new Rect(m_screenSize.x / 2 + 20, ((m_screenSize.y - m_screenSize.y / 3)),
                (m_screenSize.x / 2 + 20) + m_screenSize.x, ((m_screenSize.y - m_screenSize.y / 3) + m_screenSize.y));
        Drawable rightButtonImage = m_context.getResources().getDrawable(R.drawable.arrow_right);
        Vector2f rightButtonPos = new Vector2f(m_screenSize.x / 2 + 20, m_screenSize.y - m_screenSize.y / 3);
        Vector2f rightButtonScale = new Vector2f(m_screenSize.x - rightButtonPos.x, m_screenSize.y - rightButtonPos.y);

        controls.setupMoveRightButton(rightButtonImage, rightButtonPos, rightButtonScale, rightButtonTouchArea);

        /*Setup Fire Button*/
        Rect fireButtonTouchArea = new Rect(0, m_screenSize.y / 2, 400,
                m_screenSize.y / 2 + 310);
        Drawable fireButtonImage = m_context.getResources().getDrawable(R.drawable.target);
        Vector2f fireButtonPos = new Vector2f(0, m_screenSize.y / 2);
        Vector2f fireButtonScale = new Vector2f(400, 400);

        controls.setupFireButton(fireButtonImage, fireButtonPos, fireButtonScale, fireButtonTouchArea);

        /*Setup Pause Button*/

        Rect pauseButtonTouchArea = new Rect(m_screenSize.x / 2 + 20, 0,
                (m_screenSize.x / 2 + 20) + m_screenSize.x, m_screenSize.y / 3);
        Drawable pauseButtonImage = m_context.getResources().getDrawable(R.drawable.pause);
        Vector2f pauseButtonPos = new Vector2f(m_screenSize.x / 2 + m_screenSize.x / 3, 0);
        Vector2f pauseButtonScale = new Vector2f(100, 100);

        controls.setupPauseButton(pauseButtonImage, pauseButtonPos, pauseButtonScale, pauseButtonTouchArea);
    }

    private void setupMediaPlayers() {
        if (PreferenceManager.get().soundIsEnabled) {
            playerBlaster = MediaPlayer.create(m_context, R.raw.player_blaster);
            enemyBlaster = MediaPlayer.create(m_context, R.raw.enemy_blaster);
            enemyDeath = MediaPlayer.create(m_context, R.raw.enemy1death);
            playerLosesLife = MediaPlayer.create(m_context, R.raw.explosion);

            if (playerBlaster != null) {
                playerBlaster.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (enemyBlaster != null) {
                enemyBlaster.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (enemyDeath != null) {
                enemyDeath.setVolume(0.0f, PreferenceManager.get().volume);
            }

            if (playerLosesLife != null) {
                playerLosesLife.setVolume(0.0f, PreferenceManager.get().volume);
            }
        }
    }


    //----------UPDATE FUNCTIONS----------

    /*If the left side of the screen is being touched, set the moving direction
    * to -1, if the right side, set it to 1.*/
    private void updatePlayerMovingDir(int touchX, int touchY) {
        touchRect = new Rect(touchX, touchY, touchX + 10, touchY + 10);

        //Pause game if pause button is touched
        if (controls.isTouchingPauseButton(touchRect)) {
            ((GameActivity) m_context).pauseGame();
        }

        if (controls.isTouchingMoveRightButton(touchRect)) {
            movingDir = 1;
        }

        if (controls.isTouchingMoveLeftButton(touchRect)) {
            movingDir = -1;
        }
    }

    private void updatePlayer(float deltaTime) {
        //Move the player left or right depending on their movement direction.
        player.translate((10 * movingDir) * deltaTime, 0);

        //If the player is touch the left or right boundary.
        if (levelBounds.isCollidingWithLeft(player.getBoundingRect())
                || levelBounds.isCollidingWithRight(player.getBoundingRect())) {
            //Move them the other direction by the same amount they moved. (Keeping it at a standstill).
            player.translate((10 * -movingDir) * deltaTime, 0);
        }
    }

    private void updateStaticEnemies(float deltaTime) {
        /*For every static enemy in the static enemy's vector.
        * (Loops backwards because elements have the potential to be removed.*/
        for (int i = staticEnemies.size() - 1; i >= 0; i--) {
            /*Generate a random number between chanceToFirebullet and 1*/
            int randomNumber = rand.nextInt(chanceToFireBullet + 1);

            /*Move every enemy between the left and right level boundaries.*/
            staticEnemies.get(i).moveBetween(levelBounds.left, levelBounds.right, deltaTime);

            //1 in 'chanceToFireBullet' odds of firing the current enemy's bullet.
            if (randomNumber == 1) {
                if (!staticEnemyBullets[i].isActive) //If the current enemy's bullet isn't already moving.
                {
                    //Fire it downwards from the current enemy's position.
                    staticEnemyBullets[i].fire(staticEnemies.get(i).getPos());

                    if (enemyBlaster != null) {
                        enemyBlaster.start();
                    }
                }
            }

            /*For every bullet the player has. (Occurs for every static enemy)*/
            for (int j = 0; j < playerBullets.length; j++) {
                if (playerBullets[j].isActive) //If the current bullet is moving.
                {
                    //Has the current bullet collided with the current static enemy?
                    if (collision.RectInRect(playerBullets[j].getBoundingRect(), staticEnemies.elementAt(i).getBoundingRect())) {
                        playerBullets[j].isActive = false; //Disable the bullet that has collided.

                        staticEnemies.get(i).removeLife(); //Remove a life from the enemy that was hit.

                        if (staticEnemies.get(i).isDead()) //If that enemy's lives are <= 0.
                        {

                            enemiesKilled++;

                            if (enemyDeath != null) {
                                enemyDeath.start();
                            }

                            staticEnemies.removeElementAt(i); //Remove enemy from Vector of static enemies.


                            /*Exit loop as element has been removed from it.*/
                            j = playerBullets.length;
                            i = -1;

                            if (staticEnemies.size() <= 0) {
                                staticEnemiesIsEmpty = true;
                            }
                        }
                    }
                }
            }
        }
    }

    int currentStep = 0; //The current point on the line.
    int numberOfPoints = 500; //The more points, the smoother/slower the animation.
    PathMeasure pm;
    float pos[] = {0.0f, 0.0f};
    float segmentLength;
    Vector2f newPos;

    private void updateDynamicEnemies(float deltaTime)
    {
        if (currentStep <= numberOfPoints)
        {
            for (int i = dynamicEnemies.size() - 1; i >= 0; i--)
            {
            pm.getPosTan(segmentLength * currentStep, pos, null);


                newPos.x = pos[0] + (i * (m_screenSize.x * 0.06f));
                newPos.y = pos[1] + (i * (m_screenSize.y * 0.06f));

                dynamicEnemies.get(i).setPosition(newPos);


                for (int j = 0; j < playerBullets.length; j++)
                {
                    if (playerBullets[j].isActive) //If the current bullet is moving.
                    {
                        //Has the current bullet collided with the current static enemy?
                        if (collision.RectInRect(playerBullets[j].getBoundingRect(), dynamicEnemies.elementAt(i).getBoundingRect()))
                        {
                            playerBullets[j].isActive = false; //Disable the bullet that has collided.

                            dynamicEnemies.get(i).removeLife(); //Remove a life from the enemy that was hit.

                            if (dynamicEnemies.get(i).isDead()) //If that enemy's lives are <= 0.
                            {

                                enemiesKilled++;

                                if (enemyDeath != null) {
                                    enemyDeath.start();
                                }

                                dynamicEnemies.removeElementAt(i); //Remove enemy from Vector of static enemies.


                                /*Exit loop as element has been removed from it.*/
                                j = playerBullets.length;
                                i = -1;

                                if (dynamicEnemies.size() <= 0) {
                                    dynamicEnemiesIsEmpty = true;
                                }
                            }
                        }
                    }
                }

                if (!playerIsDead)
                {
                    if (i >= 0) {
                        if (collision.RectInRect(dynamicEnemies.get(i).getBoundingRect(), player.getBoundingRect()) && !playerIsDead) {
                            playerIsDead = true;

                            player.removeLife();

                            handler.postDelayed(new Runnable() {
                                public void run() {
                                    if (count < 2) {
                                        //Flash player.
                                        player.setActive();
                                        count++;
                                        handler.postDelayed(this, 500);
                                    } else {
                                        playerIsDead = false;
                                        count = 0;
                                        handler.removeCallbacks(this);
                                    }

                                }
                            }, 500);

                            if (playerLosesLife != null) {
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

            currentStep++;
        }
        else
        {
            currentStep = 0;
        }
    }

    private void updateBullets(float deltaTime)
        {
        /*For every bullet the player has.*/
        for (int i = 0; i < playerBullets.length; i++) {
            playerBullets[i].update(deltaTime); //Move if active.
        }

        /*For every static enemy bullet.*/
        for (int i = 0; i < staticEnemyBullets.length; i++) {
            staticEnemyBullets[i].update(deltaTime); //Move if active.

            /*If the current bullet is active.*/
            if (staticEnemyBullets[i].isActive) {
                /*Is it colliding with the player?*/
                if (collision.RectInRect(staticEnemyBullets[i].getBoundingRect(), player.getBoundingRect())) {
                    staticEnemyBullets[i].isActive = false; //Disable the bullet that collided.

                    player.removeLife(); //Remove a life from the player.

                    playerIsDead = true;

                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (count < 2) {
                                //Flash player.
                                player.setActive();
                                count++;
                                handler.postDelayed(this, 500);
                            } else {
                                playerIsDead = false;
                                count = 0;
                                handler.removeCallbacks(this);
                            }

                        }
                    }, 500);

                    if (playerLosesLife != null) {
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

    private int fireRate = 30 ; //How many frames between shots.
    private boolean okayToFire = true; //It's okay to fire another bullet.


    private void fireBullet(int touchX, int touchY)
    {
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

                handler.postDelayed(new Runnable()
                {
                    public void run()
                    {
                        okayToFire = true;
                    }
                }, 250);
            }


        }
    }

    private void newWave() {
        waveNumber++;

        /*Update number of static enemies, and their lives.*/
        if (m_numberOfStaticEnemies <= 12)
        {
            m_numberOfStaticEnemies += 2;
        }

        if (waveNumber == 14)
        {
            m_enemyLives += 1;
            m_numberOfStaticEnemies -= 5;
            m_numberOfDynamicEnemies -= 3;
        }

        if (waveNumber == 9)
        {
            chanceToFireBullet = 250;
        }

        /*Maximum number of dynamic enemies is 5. Start increasing by 1 enemy after after the 4th
        * wave, but only do it every other wave.*/
        if (m_numberOfDynamicEnemies <= 4 && waveNumber > 2 && waveNumber % 2 == 0)
        {
            m_numberOfDynamicEnemies += 1;
        }

        if (waveNumber == 15)
        {
            numberOfPoints = 400;
        }

        if (waveNumber > 20)
        {
            endGame(true); //Can put no more enemies on screen, you win.
        }


        staticEnemyPlacement = new Vector2f(0, 100);

        setupEnemies(m_numberOfStaticEnemies, m_numberOfDynamicEnemies, m_enemyLives);
    }

    private void endGame(boolean gameIsWon) {
        PreferenceManager.get().gameIsWon = gameIsWon;
        PreferenceManager.get().wavesCompleted = waveNumber;
        PreferenceManager.get().enemiesKilled = enemiesKilled;

        Intent intent = new Intent(m_context, GameEndActivity.class);
        m_context.startActivity(intent);
        ((Activity) m_context).finish();
    }

    //----------EVENT LISTENER FUNCTIONS----------

    /*Listen for touch events*/
    public void screenTouched(MotionEvent event) {
        int eventAction = event.getActionMasked();

        if (eventAction == MotionEvent.ACTION_DOWN) {
            updatePlayerMovingDir((int) event.getX(), (int) event.getY());
            fireBullet((int) event.getX(), (int) event.getY());
        }

        if (eventAction == MotionEvent.ACTION_MOVE) {
            updatePlayerMovingDir((int) event.getX(), (int) event.getY());
        }

        if (eventAction == MotionEvent.ACTION_UP) {
            movingDir = 0;
        }
    }
}