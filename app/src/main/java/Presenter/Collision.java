package Presenter;

import android.graphics.Rect;

/*Includes functions for checking for collision.*/
public class Collision
{
    public void Collision()
    {

    }

    public boolean RectInRect(Rect a, Rect b)
    {
        if (a.right < b.left || a.left > b.right)
        {
            return false;
        }
        else if (a.bottom < b.top || a.top > b.bottom)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
