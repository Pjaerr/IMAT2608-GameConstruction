package presenter;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import model.DrawableObject;
import model.Vector2f;

public class Controls
{
    Paint paint = new Paint(); //Used to draw to a canvas.

    Collision collision = new Collision(); //Used to check for collisions.

    Rect fireButtonTouchArea; //Reference to the button used to fire a missle.
    Rect leftButtonTouchArea; //Reference to the button used to move left.
    Rect rightButtonTouchArea; //Reference to the button used to move right.
    Rect pauseButtonTouchArea;

    DrawableObject fireButton;
    DrawableObject leftArrow;
    DrawableObject rightArrow;
    DrawableObject pauseButton;


    public Controls()
    {

    }

    /*Takes a Rect to be used as the respective buttons.*/
    public void setupPauseButton(Drawable image, Vector2f pos, Vector2f scale, Rect touchArea)
    {
        pauseButtonTouchArea = touchArea;

        pauseButton = new DrawableObject(image, pos, scale);
        pauseButton.setAlpha(127);
    }

    public void setupMoveLeftButton(Drawable image, Vector2f pos, Vector2f scale, Rect touchArea)
    {
        leftButtonTouchArea = touchArea;

        leftArrow = new DrawableObject(image, pos, scale);
        leftArrow.setAlpha(127);
    }

    public void setupMoveRightButton(Drawable image, Vector2f pos, Vector2f scale, Rect touchArea)
    {
        rightButtonTouchArea = touchArea;

        rightArrow = new DrawableObject(image, pos, scale);
        rightArrow.setAlpha(127);
    }
    public void setupFireButton(Drawable image, Vector2f pos, Vector2f scale, Rect touchArea)
    {
        fireButtonTouchArea = touchArea;

        fireButton = new DrawableObject(image, pos, scale);
        fireButton.setAlpha(127);
    }


    public void draw(Canvas canvas)
        {
            leftArrow.draw(canvas);
            rightArrow.draw(canvas);
            fireButton.draw(canvas);
            pauseButton.draw(canvas);
        }





    /*Helper functions to check whether the rectangle placed where a touch has occured
    * is within the respective buttons.*/
    public boolean isTouchingPauseButton(Rect touchRect)
    {
        return (collision.RectInRect(pauseButtonTouchArea, touchRect));
    }

    public boolean isTouchingMoveLeftButton(Rect touchRect)
    {
        return (collision.RectInRect(leftButtonTouchArea, touchRect));
    }

    public boolean isTouchingMoveRightButton(Rect touchRect)
    {
        return (collision.RectInRect(rightButtonTouchArea, touchRect));
    }

    public boolean isTouchingFireButton(Rect touchRect)
    {
        return (collision.RectInRect(fireButtonTouchArea, touchRect));
    }
}
