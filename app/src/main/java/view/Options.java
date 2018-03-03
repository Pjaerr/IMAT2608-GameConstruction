package view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;

import model.PreferenceManager;
import jackson.joshua.imat2608_galaga.R;

public class Options extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);

        ((CheckBox)findViewById(R.id.enableSoundCheckbox)).setChecked(PreferenceManager.get().soundIsEnabled);

        ((SeekBar)findViewById(R.id.volumeSeekBar)).setProgress((int)(PreferenceManager.get().volume / 0.01));

        ((SeekBar)findViewById(R.id.volumeSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                PreferenceManager.get().volume = (i * 0.01f);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void enableSound(View view)
    {
        CheckBox checkBox = (CheckBox)view;

        PreferenceManager.get().soundIsEnabled = checkBox.isChecked();
    }
}
