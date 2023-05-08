package com.example.carspotter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.carspotter.model.Car;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.HeatmapTileProvider;

import java.util.ArrayList;
import java.util.List;

public class SpotLocationFragment extends Fragment {
    private GoogleMap map;
    private HeatmapTileProvider provider;
    private TileOverlay overlay;
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
            /*
            //HEATMAP
            addHeatMap(googleMap);

            //MARKER
            LatLng selectedSpot = new LatLng(50, 5);
            googleMap.addMarker(new MarkerOptions().position(selectedSpot).title("Selected Spot"));
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(selectedSpot));
            */
            map = googleMap;
            // Create a list of LatLng objects representing the heatmap data points
            ArrayList<LatLng> dataPoints = new ArrayList<>();
            dataPoints.add(new LatLng(37.775, -122.419));
            dataPoints.add(new LatLng(37.776, -122.418));
            dataPoints.add(new LatLng(37.777, -122.417));
            // Add more data points as needed...

            // Create a HeatmapTileProvider using the data points
            provider = new HeatmapTileProvider.Builder()
                    .data(dataPoints)
                    .build();

            // Add the heatmap overlay to the map
            map.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

            // Set the camera position to the center of the heatmap data points
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.776, -122.418), 13));
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
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
}