package Model;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import Presenter.Collision;

public class Enemy extends DrawableObject
{
    private int lives = 2;
    private int movementSpeed = 5;
    private int movingDir = -1;

    private Rect nextDestination;

    private Collision collision;

    public Enemy(Drawable image, Vector2i position, Vector2i scale)
    {
        super(image, position, scale);
        collision = new Collision();
    }

    /*Moves from pointA to pointB at a given movement speed. This is to be
    * used for static enemies that move from left to right at the top of the
    * screen, therefore pointA should be to the left and pointB should be to the right.*/
    public void moveBetween(Rect pointA, Rect pointB)
    {
        if (nextDestination != pointA && nextDestination != pointB)
        {
            nextDestination = pointA;
        }

        translate(movementSpeed * movingDir, 0);

        if (collision.RectInRect(getBoundingRect(), nextDestination))
        {
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


}
