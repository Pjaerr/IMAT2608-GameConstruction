package Presenter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;

public class Controls
{
    Paint paint = new Paint();

    Collision collision = new Collision();

    Rect fireButton;
    Rect moveLeftButton;
    Rect moveRightButton;

    public Controls()
    {
    }

    public void setupMoveLeftButton(Rect rect)
    {
        moveLeftButton = rect;
    }
    public void setupMoveRightButton(Rect rect)
    {
        moveRightButton = rect;
    }
    public void setupFireButton(Rect rect)
    {
        fireButton = rect;
    }


    public void draw(Canvas canvas)
    {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(3);
        paint.setAlpha(80);

        paint.setColor(Color.RED);
        canvas.drawRect((float)fireButton.left, (float)fireButton.top,
                (float)fireButton.right, (float)fireButton.bottom, paint);

        paint.setColor(Color.BLUE);
        canvas.drawRect((float)moveLeftButton.left, (float)moveLeftButton.top,
                (float)moveLeftButton.right, (float)moveLeftButton.bottom, paint);

        canvas.drawRect((float)moveRightButton.left, (float)moveRightButton.top,
                (float)moveRightButton.right, (float)moveRightButton.bottom, paint);
    }

    public boolean isTouchingMoveLeftButton(Rect touchRect)
    {
        return (collision.RectInRect(moveLeftButton, touchRect));
    }

    public boolean isTouchingMoveRightButton(Rect touchRect)
    {
        return (collision.RectInRect(moveRightButton, touchRect));
    }

    public boolean isTouchingFireButton(Rect touchRect)
    {
        return (collision.RectInRect(fireButton, touchRect));
    }
}
