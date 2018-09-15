package com.example.casey.hacknelson;

import android.app.Dialog;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class home extends AppCompatActivity {

    TextView name;
    ImageView profile;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        name = (TextView) findViewById(R.id.welcome);
        profile = (ImageView)findViewById(R.id.profile);
        String firstName = getIntent().getStringExtra("ProfileFirstName");
        String profilePicString = getIntent().getStringExtra("ProfileURI");
        if (firstName != null) {
            name.setText("Welcome " + firstName + '.');
        }
        if(profilePicString != null)
        {
            AsyncTask getProfile;
            getProfile = new DownloadTask().execute(stringToURL(profilePicString));
        }
    }

    private URL stringToURL(String urlString){
        try{
            URL url = new URL(urlString);
            return url;
        }catch(MalformedURLException e){
            e.printStackTrace();
        }
        return null;
    }

    private class DownloadTask extends AsyncTask<URL, Void, Bitmap> {
        // Do the task in background/non UI thread
        protected Bitmap doInBackground(URL... urls) {
            URL url = urls[0];
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                // Connect the http url connection
                connection.connect();
                // Get the input stream from http url connection
                InputStream inputStream = connection.getInputStream();
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                Bitmap bmp = BitmapFactory.decodeStream(bufferedInputStream);

                // Return the downloaded bitmap
                return bmp;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                // Disconnect the http url connection
                connection.disconnect();
            }
            return null;
        }
        protected void onPostExecute(Bitmap result){

            if(result!=null){
                // Display the downloaded image into ImageView
                profile.setImageBitmap(result);

                // Save bitmap to internal storage
                Uri imageInternalUri = saveImageToInternalStorage(result);
                // Set the ImageView image from internal storage
                profile.setImageURI(imageInternalUri);
            }
        }


        // Custom method to save a bitmap into internal storage
        protected Uri saveImageToInternalStorage(Bitmap bitmap){
            // Initialize ContextWrapper
            ContextWrapper wrapper = new ContextWrapper(getApplicationContext());
            File file = wrapper.getDir("Images",MODE_PRIVATE);
            file = new File(file, "UniqueFileName"+".jpg");

            try{
                OutputStream stream = null;
                stream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream);
                stream.flush();
                stream.close();
            }catch (IOException e){
                e.printStackTrace();
            }

            Uri savedImageURI = Uri.parse(file.getAbsolutePath());

            return savedImageURI;
        }

    }

    public void viewCompetitionsJoined(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Competitions Joined").setItems(R.array.Competitions_Joined, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://nelsonweekly.co.nz/shop-n-win/"));
                    startActivity(browserIntent);
                }
            }
        });
        builder.show();

    }

    public void viewCompetitionsNotJoined(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Competitions Not Joined").setItems(R.array.Competitions_Not_Joined, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if(which == 0){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.ticketdirect.co.nz/event/season/1607"));
                    startActivity(browserIntent);
                }
            }
        });
        builder.show();

    }

    public void viewEvents(View view){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Upcoming Events").setItems(R.array.Events, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // The 'which' argument contains the index position
                // of the selected item
            }
        });
        builder.show();
    }
}
