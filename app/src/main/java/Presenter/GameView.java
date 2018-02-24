package Presenter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Process;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/*This is the class that initiates the game loop on a seperate thread and then
* continously calls the functions found within the GameLoop class. Acting as the
* backbone for the game loop, keeping away from the game-specific objects.*/

public class GameView extends SurfaceView implements Runnable
{
    private SurfaceHolder m_surfaceHolder; //Surface holder for the canvas.
    private Thread m_gameLogic = null; //Thread for the game logic.
    private boolean isPaused = false;
    private GameLoop gameLoop; //Reference to the GameLoop to call Start(), Update() and Draw().


    /*GameView constructor, given a context and a screensize.*/
    public GameView(Context context, Point screenSize)
    {
        super(context);

        m_surfaceHolder = getHolder(); //Get the surface holder.

        gameLoop = new GameLoop(screenSize, context); //Initialise the game loop.

        gameLoop.Start(); //Start the game loop.
    }

    public void run()
    {
        /*This thread should run in the background.*/
        android.os.Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

        /*While the game is not paused.*/
        while(!isPaused)
        {
            /*If the surface isn't valid.*/
            if (!m_surfaceHolder.getSurface().isValid())
            {
                continue; //Skip rest of logic this loop.
            }

            Canvas canvas = m_surfaceHolder.lockCanvas(); //Lock canvas.
            gameLoop.Update(); //Update the game.
            gameLoop.Draw(canvas);   //Draw to canvas.
            m_surfaceHolder.unlockCanvasAndPost(canvas); //Unlock canvas.
        }
    }

    public void pause()
    {
        isPaused = true;
        while(true)
        {
            try{
                m_gameLogic.join();
            }
            catch(InterruptedException e)
            {
                e.printStackTrace();
            }
            break;
        }

        m_gameLogic = null;
    }

    public void resume()
    {
        isPaused = false;
        m_gameLogic = new Thread(this);
        m_gameLogic.start();
    }

    /*Listens for touch events.*/
    public boolean onTouchEvent(MotionEvent event)
    {
        gameLoop.screenTouched(event);

        return true; //Tell the system we have handled the event.
    }

}
