package model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/*The class from which all objects that wish to have a sprite that
* is drawn to, and movable around, the canvas need to inherit from.
*
* Includes functionality for moving and scaling a sprite that references
* a resources from the drawables folder.*/
public class DrawableObject
{
    protected Vector2f m_pos; //The left and top of this object's bounding rectangle.
    private Vector2f m_scale; //The right and bottom of this object's bounding rectangle.

    private Drawable m_sprite; //Reference to the image passed when this object is constructed.

    private int numberOfAnimations = 0;
    private Vector<Drawable[]> animations;
    private Map animationIndices;
    private int currentAnimationIndex = 0;

    private int currentFrame = 0;

    private boolean isAnimated = false;

    Resources resources;

    public DrawableObject (Resources res, Bitmap bmp, int columns, int rows, String animationName, Vector2f position, Vector2f scale)
    {
        resources = res;

        isAnimated = true;

        m_pos = position;
        m_scale = scale;

        animations = new Vector<Drawable[]>(1);
        animationIndices = new HashMap();

        createAnimation(bmp, columns, rows, animationName);
    }


    public void createAnimation(Bitmap spritesheet, int columns, int rows, String animationName)
    {
        Drawable[] anim = new Drawable[columns * rows]; //Number of sprites is cols x rows.

        int width = spritesheet.getWidth() / columns; //The width of a sprite is the width of the entire bitmap / the number of columns.
        int height = spritesheet.getHeight() / rows; //The height of a sprite is the height of the entire bitmap / the number of rows.

        int currentColumn = 0;
        int currentRow = 0;

        /*For every sprite in m_sprites.*/
        for (int i = 0; i < anim.length; i++)
        {
            if (currentColumn >= columns) //If we've reached the end of the row.
            {
                currentColumn = 0; //Start the beginning of the columns.
                currentRow++; //Move to the next row.
            }

            if (currentRow >= rows)
            {
                currentRow = 0;
            }


            int x = currentColumn * width;
            int y = currentRow * height;

            anim[i] = new BitmapDrawable(resources, Bitmap.createBitmap(spritesheet, x, y, width, height));
            anim[i].setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));

            currentColumn++;
        }

        animations.add(anim); //Add the created animations to the collection of animations.
        animationIndices.put(animationName, numberOfAnimations); //Store the given name with the index of the animation.


        numberOfAnimations++;
    }



    public DrawableObject (Drawable image, Vector2f position, Vector2f scale)
    {
        m_pos = position;
        m_scale = scale;

        m_sprite = image;

        /*Setup the bounding rectangle of this object.*/
        m_sprite.setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));

    }

    private boolean isHidden = false;

    public void setActive()
    {
        isHidden = !isHidden;
    }

    /*Change the Drawable that this object's sprite references.*/
    public void setTexture(Drawable image)
    {
        m_sprite = image;
    }

    public void setTexture(Drawable image, int index)
    {
        if (index < animations.elementAt(currentAnimationIndex).length && index >= 0) {
            animations.elementAt(currentAnimationIndex)[index] = image;
        }
    }

    /*Set this object's position to its current positon plus x and y.*/
    public void translate(float x, float y)
    {
        setPosition(new Vector2f(m_pos.x + x, m_pos.y + y));
    }

    /*Set this object's position outright to the given Vector2.*/
    public void setPosition(Vector2f position)
    {
        m_pos = position;

        if (isAnimated)
        {
            for (int i = 0; i < animations.elementAt(currentAnimationIndex).length; i++)
            {
                animations.elementAt(currentAnimationIndex)[i].setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));
            }
        }
        else
        {
            m_sprite.setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));
        }

    }

    /*Set the width and height of this objects bounding rectangle.*/
    public void setScale(Vector2f scale)
    {
        m_scale = scale;

        if (isAnimated)
        {
            for (int i = 0; i < animations.elementAt(currentAnimationIndex).length; i++)
            {
                animations.elementAt(currentAnimationIndex)[i].setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));
            }
        }
        else
        {
            m_sprite.setBounds((int)m_pos.x, (int)m_pos.y, (int)(m_pos.x + m_scale.x), (int)(m_pos.y + m_scale.y));
        }
    }

    public void setAlpha(int alpha)
    {
        if (isAnimated)
        {
            animations.elementAt(currentAnimationIndex)[currentFrame].setAlpha(alpha);
        }
        else
        {
            m_sprite.setAlpha(alpha);
        }

    }

    /*Draw this object's sprite on the given canvas.*/
    public void draw(Canvas canvas)
    {
        if (!isHidden)
        {
            if (isAnimated)
            {
                animations.elementAt(currentAnimationIndex)[currentFrame].draw(canvas);
            }
            else
            {
                m_sprite.draw(canvas);
            }
        }
    }

    public Rect getBoundingRect()
    {
        if (isAnimated)
        {
            return animations.elementAt(currentAnimationIndex)[currentFrame].getBounds();
        }
        else
        {
            return m_sprite.getBounds();
        }
    }

    public Vector2f getPos()
    {
        return m_pos;
    }


    long previousTime = System.currentTimeMillis();
    public boolean animationIsCompleted = false;

    public void startAnimation(String animName, final long frameTimeInMs, boolean shouldLoop)
    {
        if (!animationIndices.containsKey(animName))
        {
            throw new IllegalArgumentException(animName + " is not a valid animation name.");
        }

        if (!animationIsCompleted || animationIsCompleted && shouldLoop)
        {
            animationIsCompleted = false;
            currentAnimationIndex = (int)animationIndices.get(animName);

            long currentTime = System.currentTimeMillis();

            if (currentTime - previousTime >= frameTimeInMs)
            {
                if (currentFrame < animations.elementAt(currentAnimationIndex).length - 1)
                {
                    currentFrame++;
                }
                else
                {
                    animationIsCompleted = true;
                    currentFrame = 0;
                }

                previousTime = currentTime;
            }
        }

    }
}
