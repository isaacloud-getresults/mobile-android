package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;
import android.content.DialogInterface;

import getresultsapp.sointeractve.pl.getresultsapp.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(getApplicationContext(), "hello janusz", Toast.LENGTH_SHORT).show();
    }

}
