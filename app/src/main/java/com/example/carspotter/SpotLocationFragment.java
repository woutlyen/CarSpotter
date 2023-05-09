package com.example.carspotter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Car;
import com.example.carspotter.model.Spot;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpotLocationFragment extends Fragment {
    private GoogleMap map;
    private HeatmapTileProvider provider;
    private TileOverlay overlay;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsFromCarId";
    private List<Spot> spots = new ArrayList<>();
    private OnMapReadyCallback callback = new OnMapReadyCallback() {

        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */
        @Override
        public void onMapReady(GoogleMap googleMap) {
            //TODO: maak lijst van de spots en plaats deze als markers op de map.
            // De meest recente krijgt een andere tag (misschien best voor de andere ook de datum in de marker)
            // Voor de tags kunt ge uit de database "lat" en "long" halen voor bepaalde car_id

            map = googleMap;
            // Create a list of LatLng objects representing the heatmap data points
            ArrayList<LatLng> dataPoints = new ArrayList<>();
            dataPoints.add(new LatLng(50, 5.001));
            dataPoints.add(new LatLng(50, 5.002));
            dataPoints.add(new LatLng(50.001, 5.001));
            // Add more data points as needed...

            //HEATPMAP:
            // 1: Create a HeatmapTileProvider using the data points
            provider = new HeatmapTileProvider.Builder()
                    .data(dataPoints)
                    .build();

            // 2: Add the heatmap overlay to the map
            map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

            //MARKER
            // Set the camera position to the center of the heatmap data points
            LatLng selectedSpot = new LatLng(50, 5);
            map.addMarker(new MarkerOptions().position(selectedSpot).title("Selected Spot"));
            map.moveCamera(CameraUpdateFactory.newLatLng(selectedSpot));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Bundle bundle = this.getArguments();
        Spot spot = bundle.getParcelable("Spot");

        requestSpotsFromCarId(String.valueOf(spot.getCar_id()));

        return inflater.inflate(R.layout.fragment_spot_location, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    //TODO: import all cars from car_id
    private void requestSpotsFromCarId(String item) {
        // Retrieve spots from database with Volley
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL+"/"+item,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        if (spots.size() != 0){
                            Toast.makeText(
                                    getActivity(),
                                    "Succesfully received all spots from database",
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "error: there was an issue retreiving data from server",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
        //Add spots from database into local list for recyclerview
        spots.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Spot spot = new Spot(response.getJSONObject(i));
                spots.add(spot);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(spots, (o1, o2) -> o1.getDate().compareTo(o2.getDate()));
    }
}