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
    requestWindowFeature(Window.FEATURE_NO_TITLE);
    final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
    alertDialog.setTitle("Successful registration");
    alertDialog.setMessage("Hello Janusz");
    alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            Toast.makeText(getApplicationContext(), "You clicked OK", Toast.LENGTH_SHORT).show();
        }
    });
    alertDialog.show();

}
}
