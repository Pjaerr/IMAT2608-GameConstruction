package jackson.joshua.imat2608_galaga;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

public class Options extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
    }

    public void enableSound(View view)
    {
        CheckBox checkBox = (CheckBox)view;

        PreferenceManager.get().soundIsEnabled = checkBox.isChecked();
    }
}
