package Model;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

/*The class from which all objects that wish to have a sprite that
* is drawn to, and movable around, the canvas need to inherit from.
*
* Includes functionality for moving and scaling a sprite that references
* a resources from the drawables folder.*/
public class DrawableObject
{
    private Vector2i m_pos; //The left and top of this object's bounding rectangle.
    private Vector2i m_scale; //The right and bottom of this object's bounding rectangle.

    private Drawable m_sprite; //Reference to the image passed when this object is constructed.

    public DrawableObject (Drawable image, Vector2i position, Vector2i scale)
    {
        m_pos = position;
        m_scale = scale;
        m_sprite = image;

        /*Setup the bounding rectangle of this object.*/
        m_sprite.setBounds(m_pos.x, m_pos.y, m_pos.x + m_scale.x, m_pos.y + m_scale.y);
    }

    /*Change the Drawable that this object's sprite references.*/
    public void setTexture(Drawable image) {
        m_sprite = image;
    }

    /*Set this object's position to its current positon plus x and y.*/
    public void translate(int x, int y)
    {
        setPosition(new Vector2i(m_pos.x + x, m_pos.y + y));
    }

    /*Set this object's position outright to the given Vector2.*/
    public void setPosition(Vector2i position)
    {
        m_pos = position;
        m_sprite.setBounds(m_pos.x, m_pos.y, m_pos.x + m_scale.x, m_pos.y + m_scale.y);
    }

    /*Set the width and height of this objects bounding rectangle.*/
    public void setScale(Vector2i scale)
    {
        m_scale = scale;
        m_sprite.setBounds(m_pos.x, m_pos.y, m_pos.x + m_scale.x, m_pos.y + m_scale.y);
    }

    /*Draw this object's sprite on the given canvas.*/
    public void draw(Canvas canvas)
    {
        m_sprite.draw(canvas);
    }

    public Rect getBoundingRect()
    {
        return m_sprite.getBounds();
    }
}
