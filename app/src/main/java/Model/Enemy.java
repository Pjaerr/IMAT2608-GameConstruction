package Model;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import Presenter.Collision;

public class Enemy extends DrawableObject
{
    private int lives = 1; //Number of lives.

    private int movementSpeed = 5; //How fast this enemy moves.

    private int movingDir = -1; //The direction this enemy moves in.

    private Rect nextDestination; //Reference to the current point being moved towards.

    private Collision collision; //Used to check for collisions.

    public Enemy(Drawable image, Vector2f position, Vector2f scale)
    {
        super(image, position, scale);

        collision = new Collision();
    }

    /*Moves from pointA to pointB at a given movement speed. This is to be
    * used for static enemies that move from left to right at the top of the
    * screen, therefore pointA should be to the left and pointB should be to the right.*/
    public void moveBetween(Rect pointA, Rect pointB, float deltaTime)
    {
        /*If this is the first time it is moving.*/
        if (nextDestination != pointA && nextDestination != pointB)
        {
            nextDestination = pointA; //Move to pointA.
        }

        translate((movementSpeed * movingDir) * deltaTime, 0); //Move in the direction of the next destination.

        /*If we have reached our destination.*/
        if (collision.RectInRect(getBoundingRect(), nextDestination))
        {
            /*Set the next destination to the other point.*/
            if (nextDestination == pointA)
            {
                nextDestination = pointB;
                movingDir = 1;
            }
            else
            {
                nextDestination = pointA;
                movingDir = -1;
            }
        }
    }


    public void removeLife()
    {
        lives--;
    }

    public boolean isDead()
    {
        return (lives <= 0);
    }

    public int getLives()
    {
        return lives;
    }

    public void setMovementSpeed(int newMovementSpeed) {
        movementSpeed = newMovementSpeed;
    }

    public void setNumberOfLives(int numberOfLives)
    {
        lives = numberOfLives;
    }


}
