package Presenter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.Log;
import android.widget.TextView;

public class Controls
{
    Paint paint = new Paint(); //Used to draw to a canvas.

    Collision collision = new Collision(); //Used to check for collisions.

    Rect fireButton; //Reference to the button used to fire a missle.
    Rect moveLeftButton; //Reference to the button used to move left.
    Rect moveRightButton; //Reference to the button used to move right.


    public Controls()
    {
    }

    /*Takes a Rect to be used as the respective buttons.*/

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
        /*Used until button images are put in place to draw boxes around each
        * button.*/

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





    /*Helper functions to check whether the rectangle placed where a touch has occured
    * is within the respective buttons.*/
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
