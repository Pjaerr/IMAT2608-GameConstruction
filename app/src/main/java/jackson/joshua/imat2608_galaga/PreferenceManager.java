package jackson.joshua.imat2608_galaga;

/** A singleton class used to hold preference across Activities.*/
public class PreferenceManager
{
    private static PreferenceManager instance;

    public boolean soundIsEnabled = true;

    public boolean gameIsWon = false;

    public int wavesCompleted = 0;

    public static synchronized PreferenceManager get()
    {
        if (instance == null)
        {
            instance = new PreferenceManager();
        }

        return instance;
    }
}
