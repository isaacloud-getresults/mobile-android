package getresultsapp.sointeractve.pl.getresultsapp.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import android.content.DialogInterface;

import getresultsapp.sointeractve.pl.getresultsapp.R;

public class MainActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    public void onClick(View v) {
		showDialog(0);
	}
	/*
   protected Dialog onCreateDialog(int id) {
		switch(id) {
		case 0:
			return new AlertDialog.Builder(this).setIcon(R.drawable.ic_launcher).setTitle("Hello Janusz").setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					Toast.makeText(getBaseContext(), "OK clicked!", Toast.LENGTH_SHORT).show();
				}
			}
		}
		return null;
	}
    */
}
