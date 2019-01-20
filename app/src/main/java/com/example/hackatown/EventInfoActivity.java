package com.example.hackatown;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.*;
import android.widget.Button;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

public class EventInfoActivity extends AppCompatActivity implements OnDataReceivedListener {
    private String info = "";
    private JSONObject objectInfo = new JSONObject();
    private TextView textView;
    private ImageView imageView;


    private String date = "", description = "";
    private Request.EventType type = Request.EventType.Autre;
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


        textView = findViewById(R.id.txt_scrollable);





        switch (type)

        {
            case FeuxCiruculation:
                getSupportActionBar().setTitle(R.string.feu_circulation);
                break;
            case PanneauxSiganlisation:
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


        GetData getData = new GetData(new OnDataReceivedListener() {
            @Override
            public void OnDataReceived(String data) {
                info = data;
                try
                {
                    loadData();
                } catch (JSONException e)
                {
                    e.printStackTrace();
                }
            }
        });
        getData.execute(getIntent().getIntExtra("id", 0));


        Button delete = findViewById(R.id.btn_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
				Log.d("DELETE IT", ""+id);
                new Delete().execute(id);
                finish();
            }
        });



    }

    @Override
    public void OnDataReceived(String data) {
        info = data;
        try
        {
            loadData();
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void loadData() throws JSONException {
        objectInfo = new JSONArray(info).getJSONObject(0);

        id = objectInfo.getInt("id");

        date = objectInfo.getString("date");
        description = objectInfo.getString("description");
        type = Request.EventType.values()[objectInfo.getInt("type")];
        user_id = objectInfo.getInt("user_id");

        String latlng = objectInfo.getString("position");

        String positionStrings[] = latlng.split(",");

        position = new LatLng(Double.parseDouble(positionStrings[0]), Double.parseDouble(positionStrings[1]));
        String typeString = null;
        switch (type)
        {
            case FeuxCiruculation:
                typeString = "Feux de signalisation";
                break;
            case PanneauxSiganlisation:
                typeString = "Panneau de signalisation";
                break;
            case PanneauxRue:
                typeString = "Panneau de nom de rue";
                break;
            case Deneigement:
                typeString = "Déneigement";
                break;
            case NidDePoule:
                typeString = "Nid de poule";
                break;
            case PoubelleRecup:
                typeString = "Poubelle/récupération remplie";
                break;
            case Stationnement:
                typeString = "Stationnement illégal";
                break;
            case Lampadaire:
                typeString = "Lampadaire";
                break;
            case InfSport:
                typeString = "Infrastructure sportive";
                break;
            case AbrisBus:
                typeString = "Abrisbus";
                break;
            case Autre:
                typeString = "Autre";
                break;
            default:
                typeString = null;
                break;
        }
        textView.setText(date + "\n\nType de requête: " + typeString + "\n\nDescription: " + description + "\n\nLatitude: " + position.latitude + "\n\nLongitude: " + position.longitude + "\n\n");
        textView.setTextColor(Color.GRAY);
        textView.setTextSize(30);
        textView.setTypeface(Typeface.DEFAULT_BOLD);

        new User(new OnDataReceivedListener() {
	        @Override
	        public void OnDataReceived(String data) {
	        	textView.setText(textView.getText() + "Émis par " + data);
	        }
        }).execute(id);

	    GlideApp.with(this).load("https://dev.concati.me/uploads/" + objectInfo.getInt("id") + ".jpg").into(imageView);
    }
}
