package com.example.carspotter;

import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.getSystemService;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Looper;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.carspotter.model.Car;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;

import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import java.io.ByteArrayOutputStream;


public class AddSpotFragment extends Fragment{
    private Button addSpotLocation;
    private TextView latData;
    private TextView latInfo;
    private TextView longData;
    private TextView longInfo;
    private TextView addSpotInfo;

    private ExtendedFloatingActionButton extendedFloatingActionButton;
    private LocationRequest locationRequest;
    private ImageView addSpotImage;
    private Button addSpotImageBtn;
    private boolean imageSubmitted = false;
    private boolean locationSubmitted = false;
    private String imageString;
    View view;
    int SELECT_PICTURE = 200;

    public AddSpotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_spot, container, false);

        // Initialise all the Buttons and TextViews
        addSpotLocation = (Button) view.findViewById(R.id.addSpotLocation);
        latData = (TextView) view.findViewById(R.id.latData);
        longData = (TextView) view.findViewById(R.id.longData);
        latInfo = (TextView) view.findViewById(R.id.latInfo);
        longInfo = (TextView) view.findViewById(R.id.longInfo);

        addSpotImage = (ImageView) view.findViewById(R.id.addSpotImage);
        addSpotImageBtn = (Button) view.findViewById(R.id.addSpotImageBtn);


        // Needed for requesting location
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(2000);

        //Step 1 (loading in fragment)
        setCarInformation();

        //Step 2 (Get location)
        checkForLocationPermission();
        requestLocation();

        //Step 3 (Get image)
        getImage();

        //Step 4 (When image and location have been uploaded, upload to database
        upload();

        return view;
    }

    public void setCarInformation(){
        /**
         * This code will get the information from the selected car
         * and write it on the top of the screen.
         */
        //Get info from selected car
        Bundle bundle = this.getArguments();
        Car car = bundle.getParcelable("Car");

        addSpotInfo = (TextView) view.findViewById(R.id.addSpotInfo);
        addSpotInfo.setText(car.getBrand() + ": " + car.getModel() + " " + car.getEdition());

        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.submit_fab);
        extendedFloatingActionButton.hide();

    }

    /**
     * This part of the Class is for uploading location information
     */
    protected boolean isGPSEnabled(){
        /**
         * This code will check whether the location service is enabled.
         * If it is not, it will return 'false'.
         */
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if(locationManager == null){
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }
    protected void requestLocation() {
        addSpotLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                latInfo.setText("Latitude: ");
                longInfo.setText("Longitude: ");
                latData.setText("Loading...");
                longData.setText("Loading...");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        if (isGPSEnabled()) {
                            latInfo.setText("Latitude: ");
                            longInfo.setText("Longitude: ");
                            LocationServices.getFusedLocationProviderClient(getActivity())
                                    .requestLocationUpdates(locationRequest, new LocationCallback() {
                                        @Override
                                        public void onLocationResult(@NonNull LocationResult locationResult) {
                                            super.onLocationResult(locationResult);
                                            // This part will make sure we only get one location, instead of continuous updates.
                                            LocationServices.getFusedLocationProviderClient(getActivity())
                                                    .removeLocationUpdates(this);

                                            if (locationResult != null && locationResult.getLocations().size() > 0) {
                                                // Here we will extract the latitude and longitude from the location.
                                                int index = locationResult.getLocations().size() - 1;
                                                String latitude = String.valueOf(locationResult.getLocations().get(index).getLatitude());
                                                String longitude = String.valueOf(locationResult.getLocations().get(index).getLongitude());

                                                // We will now display the results on the app.
                                                latData.setText(latitude);
                                                longData.setText(longitude);

                                                locationSubmitted = true;
                                                if(imageSubmitted){
                                                    extendedFloatingActionButton.setVisibility(View.VISIBLE);
                                                }

                                            }
                                        }
                                    }, Looper.getMainLooper());
                        } else {
                            latInfo.setText("Error: ");
                            latData.setText("Please turn on your gps.");
                            longInfo.setText("");
                            longData.setText("");
                        }
                    }
                } else {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                }
            }
        });
    }
    protected void checkForLocationPermission() {
        /**
         * Here we will check the device's location permissions.
         * If coarse location is given but precise is not, precise location will be requested.
         * If none of the permissions are granted, both coarse and fine will be requested.
         * If both are granted, nothing will be requested.
         */

        //TODO: Weigeren dat ge kunt localiseren nadat (fine) location geweigerd is.
        ActivityResultLauncher<String[]> locationPermissionRequest =
                registerForActivityResult(new ActivityResultContracts
                                .RequestMultiplePermissions(), result -> {
                    Boolean fineLocationGranted = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        fineLocationGranted = result.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);
                    }
                    Boolean coarseLocationGranted = null;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        coarseLocationGranted = result.getOrDefault(
                                        Manifest.permission.ACCESS_COARSE_LOCATION,false);
                    }
                    if (fineLocationGranted != null && fineLocationGranted) {
                                // Precise location access granted.
                    } else if (coarseLocationGranted != null && coarseLocationGranted) {
                                // Only approximate location access granted.
                                latInfo.setText("Error: ");
                                latData.setText("Please allow usage of precise location.");
                                longInfo.setText("");
                                longData.setText("");
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            } else {
                                // No location access granted.
                                latInfo.setText("Error: ");
                                latData.setText("Please allow usage of location.");
                                longInfo.setText("");
                                longData.setText("");
                                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                            }
                        }
                );
        locationPermissionRequest.launch(new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    /**
     * This part of the Class is for uploading the image
     */
    protected void getImage(){
        /**
         * This code is simply for waiting for the press of the 'Add picture' button.
         * It will then save the selected image as a String, for sending it to the database.
         */
        addSpotImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
                imageString = imageToString();

                if(locationSubmitted){
                    extendedFloatingActionButton.setVisibility(View.VISIBLE);
                }
            }
        });
    }
    protected void imageChooser() {
        /**
         * Here we create an instance of the intent of the type image.
         */
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        // pass the constant to compare it with the returned requestCode (200)
        startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            // compare the resultCode with the
            // SELECT_PICTURE constant
            if (requestCode == SELECT_PICTURE) {
                // Get the url of the image from data
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    //TODO: crop image in juiste formaat
                    addSpotImage.setImageURI(selectedImageUri);
                }
            }
        }
    }
    protected String imageToString() {
        /**
         * To store the image in the database, we have to take the image from the ImageView and convert it to a BASE64 string.
         */
        addSpotImage.buildDrawingCache();
        Bitmap bmap = addSpotImage.getDrawingCache();

        ByteArrayOutputStream byteArrayOutputStream =  new ByteArrayOutputStream();
        bmap.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
        byte[] imgbytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(imgbytes, Base64.DEFAULT);
    }

    /**
     * This part of the Class is for taking the given information and uploading it to the SQL-database.
     */
    protected void upload(){
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
                public void onClick(View v) {
                    //TODO: onSubmitClick stuur data naar Database
                    // Add date!
                    // Tot slot terug naar Home of Spotter
                    }
                });
            }
}