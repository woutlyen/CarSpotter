package com.example.carspotter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
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
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpotLocationFragment extends Fragment {
    /**
     * This fragment is used for loading in the map with the associated spots.
     * It's used for a specific model (in the spotter tab), or all the spots from the user (in the user tab).
     */
    private GoogleMap map;
    private HeatmapTileProvider provider;
    private TileOverlay overlay;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsForMap";
    private static final String QUEUE_USER_URL = "https://studev.groept.be/api/a22pt304/GetUserSpotsForMap";
    private List<LatLng> spots = new ArrayList<>();
    private Spot selectedSpot;
    private int spotsFromUser;
    private ProgressBar progressBar;
    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Toast.makeText(
                    getActivity(),
                    "Retreiving Spots from Database",
                    Toast.LENGTH_SHORT).show();
            map = googleMap;
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_spot_location, container, false);

        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.VISIBLE);

        Bundle bundle = this.getArguments();
        selectedSpot = bundle.getParcelable("Spot");
        spotsFromUser = bundle.getInt("spotsFromUser");

        if(spotsFromUser == 1){
            requestSpotsFromUser();
        }
        else {
            requestSpotsFromCarId(String.valueOf(selectedSpot.getCar_id()));
        }
        return view;
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
                            prepMap();
                            Toast.makeText(
                                    getActivity(),
                                    "Succesfully processed all spots",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "error: there was an issue retreiving data from server",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        requestQueue.add(queueRequest);
    }
    private void requestSpotsFromUser() {
        // Retrieve spots from database with Volley
        String user = ((MainActivity) (getContext())).getUser();
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_USER_URL+"/"+user,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        if (spots.size() != 0){
                            prepMap();
                            Toast.makeText(
                                    getActivity(),
                                    "Successfully processed all spots from user",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "error: there was an issue retrieving data from server",
                                    Toast.LENGTH_SHORT).show();
                        }
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
        requestQueue.add(queueRequest);
    }
    private void processJSONResponse(JSONArray response) {
        //Add spots from database into local list
        spots.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Double lat = response.getJSONObject(i).getDouble("lat");
                Double lng = response.getJSONObject(i).getDouble("lng");
                spots.add(new LatLng(lat, lng));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    protected void prepMap(){
        map.clear();
        //HEATPMAP:
        // 1: Create a HeatmapTileProvider using the data points
        provider = new HeatmapTileProvider.Builder()
                .data(spots)
                .build();
        // 2: Add the heatmap overlay to the map
        map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

        //MARKER
        // Set the camera position to the center of the heatmap data points
        map.addMarker(new MarkerOptions().position(selectedSpot.getLatLng()).title("Selected Spot"));
        map.moveCamera(CameraUpdateFactory.newLatLng(selectedSpot.getLatLng()));
    }
}