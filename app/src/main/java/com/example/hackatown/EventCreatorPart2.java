package com.example.hackatown;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class EventCreatorPart2 extends AppCompatActivity {


    private Boolean isOthersSelected=false;
    //Paramètre et méthodes pour la prise de photo
    String mCurrentPhotoPath;
    private File createImageFile() {
        // Create an image file name
        String imageFileName = "JPEG_PICTURE";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = null;
        try {
            image = File.createTempFile(
                    imageFileName,  /* prefix */
                    ".jpg",         /* suffix */
                    storageDir      /* directory */
            );
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent() throws IOException {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            photoFile = createImageFile();
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
                System.out.println("YESS");
            }
        } else {
            System.out.println("MABAD");
        }
    }
    //Fin paramètre et méthodes pour la prise de photo


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Constructeur
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_creator_part2);

        //Parametre
        Button sendRequestBtn = findViewById(R.id.sendRequestBtn);
        ImageButton takePictureBtn = findViewById(R.id.insertImgBtn);
        EditText editableType = findViewById(R.id.others);
        editableType.setEnabled(false);
        editableType.setVisibility(View.INVISIBLE);
        TextView pageTitle = findViewById(R.id.EventName);

        Bundle extras = getIntent().getExtras();
        Request.EventType type = Request.EventType.values()[getIntent().getIntExtra("type", 0)] ;
        String locationStrings[] = getIntent().getStringExtra("position").split(",");
        final Request.EventType typeDeRequest;
        final LatLng location = new LatLng(Double.parseDouble(locationStrings[0]), Double.parseDouble(locationStrings[1]));

        //Définir le type de l'event
        switch (type) {
            case FeuxCiruculation:  typeDeRequest = Request.EventType.FeuxCiruculation; pageTitle.setText("Feu de circulation");
            break;
            case PanneauxSiganlisation:  typeDeRequest = Request.EventType.PanneauxSiganlisation;  pageTitle.setText("Panneau de signalisation");
                break;
            case PanneauxRue:  typeDeRequest = Request.EventType.PanneauxRue;  pageTitle.setText("Panneau de nom de rue");
                break;
            case Deneigement:  typeDeRequest = Request.EventType.Deneigement;  pageTitle.setText("Déneigement");
                break;
            case NidDePoule:  typeDeRequest = Request.EventType.NidDePoule;  pageTitle.setText("Nid de poule");
                break;
            case PoubelleRecup:  typeDeRequest = Request.EventType.PoubelleRecup;  pageTitle.setText("Poubelle/Récupération remplie");
                break;
            case Stationnement:  typeDeRequest = Request.EventType.Stationnement;  pageTitle.setText("Stationnement illégal");
                break;
            case Lampadaire:  typeDeRequest = Request.EventType.Lampadaire;  pageTitle.setText("Lampadaire");
                break;
            case InfSport:  typeDeRequest = Request.EventType.InfSport;  pageTitle.setText("Infrastructure sportive");
                break;
            case AbrisBus: typeDeRequest = Request.EventType.AbrisBus;  pageTitle.setText("Abribus");
                break;
            case Autre: typeDeRequest = Request.EventType.Autre;  pageTitle.setText("Autre"); isOthersSelected = true; editableType.setEnabled(true);editableType.setVisibility(View.VISIBLE);
                break;
            default: typeDeRequest = null;
                break;
        }


        //Écouteurs de bouton
        sendRequestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //debut
                EditText editableDescription = findViewById(R.id.plain_text_input);
                String description = editableDescription.getText().toString();
                if(isOthersSelected){
                    description = "Requête de type : " + editableType.getText().toString() + ". " + description;
                }
                Date todaysDate = new Date();
                int userId = 1;
                Request request = new Request( typeDeRequest, description, location, todaysDate, userId, mCurrentPhotoPath);

                Log.d("POLY", "Creation dune request:");
                Log.d("POLY",  "Type : " +typeDeRequest + ", Description : " + description + ", Position : (" + location.latitude + ":" + location.longitude + "), Date: " + todaysDate);

                new CallAPI().execute(request);

                Intent intent = new Intent(EventCreatorPart2.this, MapsActivity.class);
                startActivity(intent);
                 //fin
            }
        });

        takePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //debut
                try {
                    dispatchTakePictureIntent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //fin
            }
        });


    }

}
