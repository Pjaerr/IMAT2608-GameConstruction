package jackson.joshua.imat2608_galaga;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class Splashscreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        /*Leave the splash screen after 1 second has passed*/
        openMainMenu(1000);
    }

    private void openMainMenu(int waitTime)
    {
        /*Handler acts like a coroutine that waits a set amount of time
        * before executing the code it contains.*/
        new Handler().postDelayed(new Runnable()
        {
            /*The function to run after waitTime has passed*/
            public void run()
            {
                /*Starts the Main activity after waitTime has passed.*/
                Intent intent = new Intent(Splashscreen.this, MainMenu.class);
                startActivity(intent);
                finish();
            }
        }, waitTime);
    }
}
