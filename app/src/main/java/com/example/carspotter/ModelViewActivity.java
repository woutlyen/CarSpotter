package com.example.carspotter;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Car;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.Collections;

public class ModelViewActivity extends AppCompatActivity {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetImageFromCarId";
    private TextView textBrand;
    private TextView textModel;
    private TextView textEdition;
    private TextView textType;
    private TextView textEngineType;
    private TextView textMSRP;
    private TextView textBuildYears;
    private TextView textSeats;
    private ImageView modelImageView;
    private Bitmap decodedImage;
    private LinearProgressIndicator linearProgressIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_view);

        Car car =(Car) getIntent().getParcelableExtra("Car");
        textBrand = (TextView) findViewById(R.id.textBrand);
        textBrand.setText(car.getBrand());
        textModel = (TextView) findViewById(R.id.textModel);
        textModel.setText(car.getModel());
        textModel = (TextView) findViewById(R.id.textEdition);
        textModel.setText(car.getEdition());
        textType = (TextView) findViewById(R.id.textType);
        textType.setText("Car type: " + car.getType());
        textEngineType = (TextView) findViewById(R.id.textEngineType);
        textEngineType.setText("Enginge type: " + car.getEnginetype());
        textMSRP = (TextView) findViewById(R.id.textMSRP);
        textMSRP.setText("MSRP: " + Integer.toString(car.getMsrp()));
        textBuildYears = (TextView) findViewById(R.id.textBuildYears);
        textBuildYears.setText("Production: " + car.getStart_build() +" - "+ car.getEnd_build());
        textSeats = (TextView) findViewById(R.id.textSeats);
        textSeats.setText("Seats: "+ car.getSeats());

        modelImageView = (ImageView) findViewById(R.id.modelImageView);
        linearProgressIndicator = (LinearProgressIndicator) findViewById(R.id.linearProgressIndicatorModelView);
        requestImageFromCarId(Integer.toString(car.getId()));
    }



    private void requestImageFromCarId(String item) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL+"/"+item,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        linearProgressIndicator.setVisibility(View.INVISIBLE);
                        modelImageView.setImageBitmap(decodedImage);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                ModelViewActivity.this,
                                "Unable to load the image!",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
            try {
                byte[] byteImage = Base64.decode(response.getJSONObject(0).getString("image"), Base64.DEFAULT);
                decodedImage = BitmapFactory.decodeByteArray(byteImage, 0, byteImage.length);

            } catch (JSONException e) {
                e.printStackTrace();
            }

    }
}