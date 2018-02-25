package Model;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import Presenter.Collision;

public class Bullet extends DrawableObject
{
    public boolean isActive = false; //Whether to draw and give this object a bounding rectangle.
    private int movementSpeed = 10; //How fast a bullet should move when fired.
    private Collision collision; //Used to check for collision.

    private int movementDir = -1;

    private Rect m_positionLimit; //The Rect that the bullet cannot pass.

    private Vector2i originalPos; //The very first position that was passed when this bullet was created.

    public Bullet(Drawable image, Vector2i position, Vector2i scale, Rect positionLimit, int movingDir)
    {
        super(image, position, scale);

        isActive = false; //Hide bullet initially.
        collision = new Collision();

        movementDir = movingDir;

        m_positionLimit = positionLimit;
    }

    /*Should be called every frame, will move a bullet if fire() has been called.*/
    public void update()
    {
        if (isActive) //If the bullet is in motion.
        {
            /*If the bullet hasn't collided with its position limit (top or bottom boundary)*/
            if (!collision.RectInRect(getBoundingRect(), m_positionLimit))
            {
                translate(0, movementSpeed * movementDir); //Move the bullet in its movement direction by movementspeed.
            }
            else //If the bullet has collided with its position limit.
            {
                isActive = false; //Hide the bullet and stop it from moving.
                setPosition(originalPos); //Move it back to its initial position.
            }
        }
    }

    /*Start moving the bullet from the given position.*/
    public void fire(Vector2i launchPoint)
    {
        if (!isActive)
        {
            setPosition(launchPoint); //Move the bullet to launch position.
            originalPos = launchPoint; //Store that position for when it has finished its fire cycle.
            isActive = true; //Show the bullet and start moving it.
        }
    }
}
