package View;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import Model.PreferenceManager;
import jackson.joshua.imat2608_galaga.R;

public class Options extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        getWindow().getDecorView().setSystemUiVisibility(PreferenceManager.get().mUIFlags);
    }

    public void enableSound(View view)
    {
        CheckBox checkBox = (CheckBox)view;

        PreferenceManager.get().soundIsEnabled = checkBox.isChecked();
    }
}
