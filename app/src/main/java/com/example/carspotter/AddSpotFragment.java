package com.example.carspotter;

import static android.app.Activity.RESULT_OK;
import static java.lang.Double.parseDouble;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Car;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.LocalTime;


public class AddSpotFragment extends Fragment {
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
    private Uri selectedImageUri;
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/AddSpot";
    View view;
    int SELECT_PICTURE = 200;
    private Car car;
    private String location;
    private String lat;
    private String lng;
    private ProgressBar progressBar;
    public AddSpotFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_spot, container, false);

        // Initiate progress bar
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        progressBar.setVisibility(view.INVISIBLE);

        // Get car information
        Bundle bundle = this.getArguments();
        car = bundle.getParcelable("Car");

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
        submitButton();

        return view;
    }

    public void setCarInformation() {
        /**
         * This code will get the information from the selected car
         * and write it on the top of the screen.
         */
        //Get info from selected car
        addSpotInfo = (TextView) view.findViewById(R.id.addSpotInfo);
        addSpotInfo.setText(car.getBrand() + ": " + car.getModel() + " " + car.getEdition());

        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.submit_fab);
        extendedFloatingActionButton.hide();

    }

    /**
     * This part of the Class is for uploading location information
     */
    protected boolean isGPSEnabled() {
        /**
         * This code will check whether the location service is enabled.
         * If it is not, it will return 'false'.
         */
        LocationManager locationManager = null;
        boolean isEnabled = false;

        if (locationManager == null) {
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
                                                lat = String.valueOf(locationResult.getLocations().get(index).getLatitude());
                                                lng = String.valueOf(locationResult.getLocations().get(index).getLongitude());

                                                // We will now display the results on the app.
                                                latData.setText(lat);
                                                longData.setText(lng);
                                                locationTranslation(lat, lng);
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
                                        Manifest.permission.ACCESS_COARSE_LOCATION, false);
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
        locationPermissionRequest.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }


    /**
     * This part of the Class is for uploading the image
     */
    protected void getImage() {
        /**
         * This code is simply for waiting for the press of the 'Add picture' button.
         * It will then save the selected image as a String, for sending it to the database.
         */
        addSpotImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageChooser();
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
                selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    // update the preview image in the layout
                    //TODO: crop image in juiste formaat
                    addSpotImage.setImageURI(selectedImageUri);
                    processImageUpload();
                }
            }
        }
    }

    protected void processImageUpload() {
        imageString = imageToString();
        imageSubmitted = true;
        if (locationSubmitted) {
            extendedFloatingActionButton.setVisibility(View.VISIBLE);
        }
    }

    protected String imageToString() {
        /**
         * To store the image in the database, we have to take the uploaded image and convert it to a BASE64 string.
         */
        InputStream inputStream = null;
        try {
            inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String base64Image = java.util.Base64.getEncoder().encodeToString(imageBytes);
        return base64Image;
    }

    /**
     * This part of the Class is for taking the given information and uploading it to the SQL-database.
     */
    private void submitButton() {
        /**
         * Check for button press and then pop up confirmation window.
         */
        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmationPopUp();
            }
        });
    }

    private void confirmationPopUp() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Submit Spot")
                .setMessage("Are you sure you want to submit this spot?")
                .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to neutral button press
                    }
                })
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to positive button press
                        progressBar.setVisibility(view.VISIBLE);
                        extendedFloatingActionButton.setVisibility(View.INVISIBLE);
                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                QUEUE_URL,
                                response -> {
                                    // Handle the response
                                    Toast.makeText(getActivity(), "New Spot Submitted", Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(view.INVISIBLE);
                                    clear();
                                },
                                error -> {
                                    // Handle the error
                                    Toast.makeText(getActivity(), ""+error, Toast.LENGTH_LONG).show();
                                    //progressIndicatorAddWikiView.hide();
                                }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                String body = null;
                                try {
                                    body = "car=" + car.getId()
                                            + "&" + "date=" + LocalDate.now()+" "+ LocalTime.now()
                                            + "&" + "location=" + location
                                            + "&" + "image=" + URLEncoder.encode(imageString, "UTF-8")
                                            + "&" + "lat=" + lat
                                            + "&" + "lng=" + lng
                                    ;
                                } catch (UnsupportedEncodingException e) {
                                    throw new RuntimeException(e);
                                }
                                return body.getBytes();
                            }

                            @Override
                            public String getBodyContentType() {
                                return "application/x-www-form-urlencoded";
                            }
                        };

                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
                        requestQueue.add(stringRequest);
                    }
                })
                .show();
    }
    private void locationTranslation(String lat,String lng) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonObjectRequest queueRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + parseDouble((String) lat) + "," +
                        parseDouble((String) lng) + "&key=AIzaSyBL5W5Y_tALAcsXnJOnK45CAhuOp4kIUio",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Toast.makeText(
                                getActivity(),
                                "Processing Location",
                                Toast.LENGTH_SHORT).show();
                        processJSONResponse(response);
                        location = String.valueOf(addSpotLocation.getText());
                        Toast.makeText(
                                getActivity(),
                                "Successfully Processed Location: " + location,
                                Toast.LENGTH_SHORT).show();

                        locationSubmitted = true;
                        if (imageSubmitted) {
                            extendedFloatingActionButton.setVisibility(View.VISIBLE);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable To Communicate With The Geocoding API, Please Retry",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONObject response) {
        String address;
        try {
            address = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
            addSpotLocation.setText(address);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private void clear(){
        imageSubmitted = false;
        locationSubmitted = false;
        addSpotImage.setImageResource(R.drawable.add);
        addSpotLocation.setText("Add location");
        latInfo.setText("*awaiting input*");
        longInfo.setText(" ");
        latData.setText(" ");
        longData.setText(" ");
    }
}