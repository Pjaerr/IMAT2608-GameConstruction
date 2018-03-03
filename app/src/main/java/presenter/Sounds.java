package presenter;


import android.content.Context;
import android.media.MediaPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

/*Manages all sounds in the game. Stops media players bunching up.*/
public class Sounds
{
    Vector<MediaPlayer> sounds;
    Map soundMap;
    int numberOfSounds = 0;

    Context m_context;

    float m_volume = 0.0f;

    public Sounds(Context context, float volume)
    {
        m_context = context;
        soundMap = new HashMap();
        m_volume = volume;
    }

    public void createSound(String name, int id)
    {
        sounds.add(MediaPlayer.create(m_context, id));
        soundMap.put(name, numberOfSounds);
        sounds.get(numberOfSounds).setVolume(0.0f, m_volume);
        numberOfSounds++;
    }

    public void play(String name)
    {
        if (soundMap.containsKey(name))
        {
            sounds.get((int)soundMap.get(name)).start();
        }
    }
}
