package com.sointeractive.getresults.app.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.sointeractive.getresults.app.R;
import com.sointeractive.getresults.app.activities.LoginActivity;
import com.sointeractive.getresults.app.cards.LogoutCard;
import com.sointeractive.getresults.app.cards.ProfileCard;
import com.sointeractive.getresults.app.cards.SettingsCard;
import com.sointeractive.getresults.app.cards.StatsCard;
import com.sointeractive.getresults.app.config.Settings;
import com.sointeractive.getresults.app.data.App;
import com.sointeractive.getresults.app.data.ImageHelper;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import it.gmariotti.cardslib.library.view.CardView;

public class ProfileFragment extends Fragment {

    private static final String TAG = ProfileFragment.class.getSimpleName();
    private final BroadcastReceiver receiverProfile = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Event: onReceive called");
            refreshData();

        }
    };
    private Context context;
    private ProfileCard profileCard;
    private SettingsCard settingsCard;
    private StatsCard statsCard;
    private CardView profileCardView;
    private static int RESULT_LOAD_IMAGE = 1;
    private ImageView userImage;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this.getActivity();
        profileCard = new ProfileCard(context, R.layout.profile_card_content);
        settingsCard = new SettingsCard(context);
        statsCard = new StatsCard(context);
        LocalBroadcastManager.getInstance(context).registerReceiver(receiverProfile,
                new IntentFilter(Settings.BROADCAST_INTENT_UPDATE_DATA));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        context = getActivity();
        // PROFILE CARD INIT
        profileCardView = (CardView) view.findViewById(R.id.cardProfile);

        CardView settingsCardView = (CardView) view.findViewById(R.id.cardSettings);
        CardView statsCardView = (CardView) view.findViewById(R.id.cardStats);
        CardView logoutCardView = (CardView) view.findViewById(R.id.cardLogout);
        profileCardView.setCard(this.profileCard);
        settingsCardView.setCard(this.settingsCard);
        statsCardView.setCard(this.statsCard);
        logoutCardView.setCard(new LogoutCard(context));

        logoutCardView.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent i = new Intent(context, LoginActivity.class);
                i.putExtra("logout", true);
                startActivity(i);

                getActivity().finish();
            }
        });

        userImage = (ImageView) view.findViewById(R.id.userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, RESULT_LOAD_IMAGE);
                    }
                })
                        .setMessage("Do you want to change your profile picture?")
                        .setIcon(R.drawable.ic_launcher);
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();

            }
        });

        return view;
    }


    void refreshData() {
        TextView counterLevel = (TextView) profileCardView.findViewById(R.id.counterLevel);
        TextView counterScore = (TextView) profileCardView.findViewById(R.id.counterScore);
        TextView counterAchievements = (TextView) profileCardView.findViewById(R.id.counterAchievements);
        counterLevel.setText("" + App.loadUserData().getRank());
        counterScore.setText(App.loadUserData().getScore());
        counterAchievements.setText(App.loadUserData().getGainedAchievements());

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(receiverProfile);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            BitmapFactory.Options bf = new BitmapFactory.Options();
            bf.inSampleSize = 4;
            Bitmap imageBitmap = BitmapFactory.decodeFile(picturePath);
            if((imageBitmap.getWidth() > 500) || (imageBitmap.getHeight() > 500)) imageBitmap = BitmapFactory.decodeFile(picturePath, bf);
            Log.d("Bitmap", "width: " + imageBitmap.getWidth() + " " + "height: " + imageBitmap.getHeight());
//            new SendToServerTask().execute(picturePath);
            userImage.setImageBitmap(ImageHelper.getAvatar(imageBitmap, picturePath));
        }
    }

    public class SendToServerTask extends AsyncTask<String, Object, Object> {
        boolean success = false;
        ProgressDialog dialog = ProgressDialog.show(getActivity(), "Saving profile picture", "Please wait...");

        @Override
        public Object doInBackground(String... params) {
            String url = "http://xyz.getresults.isaacloud.com/images/";

            File file = new File(params[0]);
            Log.e("SendToServerTask", "Starting to send " + file.getName());
            try {
                HttpClient httpclient = new DefaultHttpClient();

                HttpPost httppost = new HttpPost(url);

                InputStreamEntity reqEntity = new InputStreamEntity(
                        new FileInputStream(file), -1);
                reqEntity.setContentType("binary/octet-stream");
                reqEntity.setChunked(true); // Send in multiple parts if needed
                httppost.setEntity(reqEntity);
                HttpResponse response = httpclient.execute(httppost);
                success = true;
                //Do something with response...
                Log.e("HttpResponse", response.toString());

            } catch (Exception e) {
                Log.e("Ahtung!", "Could not send file");
            }
            return null;
        }

        protected void onPostExecute(Object result) {
            dialog.dismiss();
            if (success) {
                Log.e(TAG, "Success");
            } else {
                Log.e(TAG, "Failed");
            }
        }

    }
}