package Model;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import Presenter.Collision;

public class LevelBoundaries
{
    Collision collision;
    Point m_screenSize;

    /*Boundaries*/
    public Rect left;
    public Rect right;
    public Rect top;
    public Rect bottom;

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
