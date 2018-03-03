package model;

import android.graphics.drawable.Drawable;

/*The DrawableObject representing the player ship that sits at the bottom
* of the screen and can fire bullets and take on collision.*/
public class Player extends DrawableObject
{
    private int lives = 5;

    /*Player constructor, sets up the parent constructor of DrawableObject.*/
    public Player(Drawable image, Vector2f position, Vector2f scale)
    {
        super(image, position, scale);
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
}
