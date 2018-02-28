package Model;

import android.view.View;

/** A singleton class used to hold preferences and global variables
 * across activities. Shouldn't be used for anything tied to specific
 * objects in the game.*/
public class PreferenceManager
{
    private static PreferenceManager instance; //Singleton instance of this class.

    public boolean soundIsEnabled = true; //Stored preference from Options activity.

    public boolean gameIsWon = false; //Used by GameEndActivity to tailor the end screen.

    public int wavesCompleted = 0; //Used by GameEndActivity to tailor the end screen.

    public float volume = 0.3f;

    //Flags to set the to a full screen without any task bar.
    public int mUIFlags = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LOW_PROFILE
            | View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

    /*Returns the singleton instance of this class if it exists, if not, it initialises
    * the instance and then returns it. This will only occur once, every future call will
    * return the already assigned instance.*/
    public static synchronized PreferenceManager get()
    {
        if (instance == null)
        {
            instance = new PreferenceManager();
        }

        return instance;
    }
}
