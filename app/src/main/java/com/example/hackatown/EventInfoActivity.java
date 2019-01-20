package com.example.hackatown;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.common.api.Response;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class EventInfoActivity extends AppCompatActivity {
    private String info = "";
    private JSONObject objectInfo = new JSONObject();
    private TextView textView;
    private ImageView imageView;


    private String date = "", description = "";
    private Events.EventType type = Events.EventType.Autre;
    private int id = 0, user_id = 0;
    private LatLng position = new LatLng(0, 0);

    private boolean imageIsFullscreen = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        imageView = findViewById(R.id.imageView);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageIsFullscreen) {
                    imageIsFullscreen=false;
                    imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT));
                    imageView.setAdjustViewBounds(true);
                }else{
                    imageIsFullscreen=true;
                    imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }
            }
        });


        /*
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int param = imageIsFullscreen ? ConstraintLayout.LayoutParams.MATCH_PARENT : ConstraintLayout.LayoutParams.WRAP_CONTENT;
                ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(param);
                imageIsFullscreen = !imageIsFullscreen;
                imageView.setLayoutParams(layoutParams);
                if (!imageIsFullscreen)
                {
                    imageView.setAdjustViewBounds(!imageIsFullscreen);
                }
                else
                {
                    imageView.setScaleType(ImageView.ScaleType.FIT_XY);
                }

            }
        });*/

        textView = findViewById(R.id.txt_scrollable);


        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);


        try
        {
            loadData();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }

        switch (type)

        {
            case FeuxCirculation:
                getSupportActionBar().setTitle(R.string.feu_circulation);
                break;
            case PanneauxSignalisation:
                getSupportActionBar().setTitle(R.string.panneau_signalisation);
                break;
            case PanneauxRue:
                getSupportActionBar().setTitle(R.string.panneau_rue);
                break;
            case Deneigement:
                getSupportActionBar().setTitle(R.string.deneigement);
                break;
            case NidDePoule:
                getSupportActionBar().setTitle(R.string.nid_poule);
                break;
            case PoubelleRecup:
                getSupportActionBar().setTitle(R.string.poubelle);
                break;
            case Stationnement:
                getSupportActionBar().setTitle(R.string.stationnement);
                break;
            case AbrisBus:
                getSupportActionBar().setTitle(R.string.abris_bus);
                break;
            case Lampadaire:
                getSupportActionBar().setTitle(R.string.lampadaire);
                break;
            case InfSport:
                getSupportActionBar().setTitle(R.string.infrastructure_sportive);
                break;
            case Autre:
            default:
                getSupportActionBar().setTitle(R.string.autre);
                break;
        }

    }

    public static JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */);
        urlConnection.setConnectTimeout(15000 /* milliseconds */);
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null)
        {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();
        System.out.println("JSON: " + jsonString);

        return new JSONObject(jsonString);
    }

    public void loadData() throws JSONException {


        String url = "https://dev.concati.me/data?=" + id;


        info = "[{\"date\":\"Sat, 19 Jan 2019 20:44:52 GMT\",\"description\":\"\\u00c9cole \\u00e0 r\\u00e9nover\",\"id\":1,\"position\":\"45.504384,-73.6150716\",\"type\":1,\"user_id\":1}]\n";//TODO

        objectInfo = new JSONArray(info).getJSONObject(0);

        date = objectInfo.getString("date");
        description = objectInfo.getString("description");
        type = Events.EventType.values()[objectInfo.getInt("type")];
        user_id = objectInfo.getInt("user_id");

        String latlng = objectInfo.getString("position");

        String positionStrings[] = latlng.split(",");

        position = new LatLng(Double.parseDouble(positionStrings[0]), Double.parseDouble(positionStrings[1]));


        textView.setText("Date: " + date + "\nDescription: " + description + "\nPosition (lat, long): (" + position.latitude + ":" + position.longitude + ")\nUser: " + user_id);


        URL url2 = null;
        try
        {
            url2 = new URL("https://dev.concati.me/uploads/1.png");
        } catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        ImageDownload imageDownload = new ImageDownload(imageView);
        imageDownload.execute(url2);


    }

    private class ImageDownload extends AsyncTask<URL, Void, Bitmap> {
        private ImageView imageView;

        public ImageDownload(ImageView imageView) {

            this.imageView = imageView;
        }

        @Override
        protected Bitmap doInBackground(URL... urls) {
            URL url2 = urls[0];
            Bitmap bmp = null;
            try
            {
                bmp = BitmapFactory.decodeStream(url2.openConnection().getInputStream());
            } catch (IOException e)
            {
                e.printStackTrace();
            }

            return bmp;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageView.setImageBitmap(bitmap);
        }
    }
}
