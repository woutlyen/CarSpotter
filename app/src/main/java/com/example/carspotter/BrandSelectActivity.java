package com.example.carspotter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Car;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BrandSelectActivity extends AppCompatActivity {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetCarsFromBrand";
    private List<Car> cars = new ArrayList<>();
    private RecyclerView carView;
    private TextView infoTxt;
    private CircularProgressIndicator circularProgressIndicatorCarView;
    private ExtendedFloatingActionButton extendedFloatingActionButton;
    String[] item = {"Audi","Volkswagen","Volvo","Mazda","Porsche","Seat","BMW","Mercedes","Subaru","Bentley","Tesla","Citroën","Peugeot","Opel","Renault","Skoda","Ford"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_brand_select);
        infoTxt = (TextView) findViewById(R.id.noCarsTxt);
        infoTxt.setText("No brand selected!");
        circularProgressIndicatorCarView = (CircularProgressIndicator) findViewById(R.id.progressIndicatorCarView);
        extendedFloatingActionButton = (ExtendedFloatingActionButton) findViewById(R.id.extended_fab);

        Arrays.sort(item);
        autoCompleteTextView = findViewById(R.id.auto_complete_txt);
        adapterItems = new ArrayAdapter<String>(this, R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);

        carView = findViewById( R.id.carView );
        CarAdapter adapter = new CarAdapter( cars );
        carView.setAdapter( adapter );
        carView.setLayoutManager( new LinearLayoutManager( this ));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();
                cars.clear();
                carView.getAdapter().notifyDataSetChanged();
                circularProgressIndicatorCarView.setVisibility(View.VISIBLE);
                infoTxt.setText("");
                requestCarsFromBrand(item);
//                Toast.makeText(BrandSelectActivity.this, "Item: " + item, Toast.LENGTH_SHORT).show();
            }

        });

        carView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 & extendedFloatingActionButton.isShown()){
                    extendedFloatingActionButton.hide();
                }
                if (dy < -10 && !extendedFloatingActionButton.isShown()) {
                    extendedFloatingActionButton.show();
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    extendedFloatingActionButton.show();
                }
            }
        });
    }

    private void requestCarsFromBrand(String item) {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL+"/"+item,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        if (cars.size() != 0){
                            infoTxt.setText("");
                        }
                        else{
                            infoTxt.setText("No cars from " + item +" added yet!");
                        }
                        carView.getAdapter().notifyDataSetChanged();
                        circularProgressIndicatorCarView.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                BrandSelectActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
        cars.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Car car = new Car(response.getJSONObject(i));
                cars.add(car);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(cars, (o1, o2) -> o1.getModel().compareTo(o2.getModel()));
//        cars.add(cars.get(0));
    }
}