package Model;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import Presenter.Collision;

public class Bullet extends DrawableObject
{
    public boolean isActive = false; //Whether to draw and give this object a bounding rectangle.
    private int movementSpeed = 10;
    private Collision collision;

    private Rect m_positionLimit; //The Rect that the bullet cannot pass.

    private Vector2i originalPos;

    public Bullet(Drawable image, Vector2i position, Vector2i scale, Rect positionLimit)
    {
        super(image, position, scale);

        isActive = false;
        collision = new Collision();

        m_positionLimit = positionLimit;
    }

    public void update()
    {
        if (isActive)
        {
            if (!collision.RectInRect(getBoundingRect(), m_positionLimit))
            {
                translate(0, -movementSpeed);
            }
            else
            {
                isActive = false;
                setPosition(originalPos);
            }
        }
    }

    public void fire(Vector2i launchPoint)
    {
        if (!isActive)
        {
            setPosition(launchPoint);
            originalPos = launchPoint;
            isActive = true;
        }
    }
}
