package model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import presenter.Collision;

/*The class holding the level boundaries. The level boundaries are
* universal and clogged up the GameLoop class, so they are extracted out
* here and initialised via the GameLoop. This class also includes some helper
* functions for checking collision with the boundaries, limiting the number
* of Collision objects between activities.*/
public class LevelBoundaries
{
    Collision collision; //Used to check for collisions
    Point m_screenSize; //Reference to the screen size.

    /*Boundaries*/
    public Rect left; //Left side boundary.
    public Rect right; //Right side boundary.
    public Rect top; //Top boundary.
    public Rect bottom; //Bottom boundary.

    /*Takes the screen size and sets up the level boundaries using them.*/
    public LevelBoundaries(Point screenSize)
    {
        collision = new Collision();
        m_screenSize = screenSize;

        setupLevelBoundaries();
    }

    private void setupLevelBoundaries()
    {
        int x = m_screenSize.x - 40;

        left = new Rect(0, 0, 50, m_screenSize.y);
        right = new Rect(x, 0, x + 50, m_screenSize.y);
        top = new Rect(0, 0, m_screenSize.x, 100);
        bottom = new Rect(0, m_screenSize.y, m_screenSize.x, m_screenSize.y - 20);
    }

    Paint paint = new Paint();

    /*Primarily used for debugging, will draw rectangles at each boundary.*/
    public void draw(Canvas canvas)
    {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(3);
        canvas.drawRect((float)left.left, (float)left.top,
                (float)left.right, (float)left.bottom, paint);

        canvas.drawRect((float)right.left, (float)right.top,
                (float)right.right, (float)right.bottom, paint);

        canvas.drawRect((float)top.left, (float)top.top,
                (float)top.right, (float)top.bottom, paint);

        canvas.drawRect((float)bottom.left, (float)bottom.top,
                (float)bottom.right, (float)bottom.bottom, paint);
    }


    public boolean isCollidingWithLeft(Rect rect)
    {
        return (collision.RectInRect(left, rect));
    }
    public boolean isCollidingWithRight(Rect rect)
    {
        return (collision.RectInRect(right, rect));
    }
    public boolean isCollidingWithTop(Rect rect)
    {
        return (collision.RectInRect(top, rect));
    }
    public boolean isCollidingWithBottom(Rect rect)
    {
        return (collision.RectInRect(bottom, rect));
    }
}
