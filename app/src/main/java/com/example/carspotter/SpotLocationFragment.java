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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpotLocationFragment extends Fragment {
    private GoogleMap map;
    private HeatmapTileProvider provider;
    private TileOverlay overlay;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsForMap";
    private List<LatLng> spots = new ArrayList<>();
    private Spot selectedSpot;
    private ProgressBar progressBar;
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
            Toast.makeText(
                    getActivity(),
                    "Retreiving Spots from Database",
                    Toast.LENGTH_LONG).show();
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

        requestSpotsFromCarId(String.valueOf(selectedSpot.getCar_id()));
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
                                    Toast.LENGTH_LONG).show();
                        }
                        else {
                            Toast.makeText(
                                    getActivity(),
                                    "error: there was an issue retreiving data from server",
                                    Toast.LENGTH_LONG).show();
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
                                Toast.LENGTH_LONG).show();
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