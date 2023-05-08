package com.example.carspotter;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ScrollView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AddWikiFragment extends Fragment {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/AddWiki";
    String item_dropdown = "";
    String[] brands = {"Audi", "Volkswagen", "Volvo", "Mazda", "Porsche", "Seat", "BMW", "Mercedes", "Subaru", "Bentley", "Tesla", "Citroën", "Peugeot", "Opel", "Renault", "Skoda", "Ford"};
    String[] bodyStyles = {"Convertible", "Coupé", "Hatchback", "Sedan", "Shooting Brake", "Station Wagon", "SUV"};
    String[] engineTypes = {"Fuel", "Electric"};
    boolean imageSubmitted = false;

    AutoCompleteTextView brandTxt;
    TextInputLayout textBrand;
    AutoCompleteTextView bodyStylesTxt;
    TextInputLayout textBodyStyles;
    AutoCompleteTextView engineTypesTxt;
    TextInputLayout textEngineTypes;
    TextInputEditText modelTxt;
    TextInputLayout textModel;
    TextInputEditText editionTxt;
    TextInputLayout textEdition;
    TextInputEditText MSRPTxt;
    TextInputLayout textMSRP;
    TextInputEditText seatsTxt;
    TextInputLayout textSeats;
    TextInputEditText startDateTxt;
    TextInputLayout textStartDate;
    TextInputEditText endDateTxt;
    TextInputLayout textEndDate;
    AppCompatCheckBox now;

    ExtendedFloatingActionButton add_wiki_fab;
    View view;
    ScrollView scrollView;
    ConstraintLayout wikiLayout;
    Button uploadImage;
    ShapeableImageView image;
    LinearProgressIndicator progressIndicatorAddWikiView;

    private String base64Uri;
    private Uri selectedImageUri;
    int SELECT_PICTURE = 200;

    public AddWikiFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_wiki, container, false);

        Arrays.sort(brands);
        brandTxt = (AutoCompleteTextView) view.findViewById(R.id.brandTxt);
        textBrand = (TextInputLayout) view.findViewById(R.id.textBrand);

        Arrays.sort(bodyStyles);
        bodyStylesTxt = (AutoCompleteTextView) view.findViewById(R.id.bodyStylesTxt);
        textBodyStyles = (TextInputLayout) view.findViewById(R.id.textBodyStyles);

        Arrays.sort(engineTypes);
        engineTypesTxt = (AutoCompleteTextView) view.findViewById(R.id.engineTypeTxt);
        textEngineTypes = (TextInputLayout) view.findViewById(R.id.textEngineType);

        modelTxt = (TextInputEditText) view.findViewById(R.id.modelTxt);
        textModel = (TextInputLayout) view.findViewById(R.id.textModel);

        add_wiki_fab = (ExtendedFloatingActionButton) view.findViewById(R.id.add_wiki_fab);
        scrollView = (ScrollView) view.findViewById(R.id.scrollView);
        wikiLayout = (ConstraintLayout) view.findViewById(R.id.wikiLayout);
        uploadImage = (Button) view.findViewById(R.id.uploadImage);
        image = (ShapeableImageView) view.findViewById(R.id.image);

        MSRPTxt = (TextInputEditText) view.findViewById(R.id.MSRPTxt);
        textMSRP = (TextInputLayout) view.findViewById(R.id.textMSRP);
        seatsTxt = (TextInputEditText) view.findViewById(R.id.seatsTxt);
        textSeats = (TextInputLayout) view.findViewById(R.id.textSeats);
        startDateTxt = (TextInputEditText) view.findViewById(R.id.startDateTxt);
        textStartDate = (TextInputLayout) view.findViewById(R.id.textStartDate);
        endDateTxt = (TextInputEditText) view.findViewById(R.id.endDateTxt);
        textEndDate = (TextInputLayout) view.findViewById(R.id.textEndDate);
        editionTxt = (TextInputEditText) view.findViewById(R.id.editionTxt);
        textEdition = (TextInputLayout) view.findViewById(R.id.textEdition);

        now = (AppCompatCheckBox) view.findViewById(R.id.now);
        progressIndicatorAddWikiView = (LinearProgressIndicator) view.findViewById(R.id.progressIndicatorAddWikiView);
        progressIndicatorAddWikiView.hide();

        if (savedInstanceState != null) {
            boolean input = savedInstanceState.getBoolean("now");
            if (input) {
                textEndDate.setVisibility(View.GONE);
                endDateTxt.setText("-1");
            } else {
                textEndDate.setVisibility(View.VISIBLE);
                endDateTxt.setText("");
            }
        }

        // Set up listener for validation
//        modelTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View view, boolean hasFocus) {
//                if (!hasFocus) {
//                    String input = modelTxt.getText().toString().trim();
//                    if (input.isEmpty()) {
//                        modelTxt.setError("This field cannot be empty");
//                    } else {
//                        modelTxt.setError(null);
//                    }
//                }
//            }
//        });

        add_wiki_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String input = brandTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    brandTxt.setError("This field cannot be empty");
                } else {
                    brandTxt.setError(null);
                    count += 1;
                }

                input = modelTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    modelTxt.setError("This field cannot be empty");
                } else if (input.length() > 20) {
                    modelTxt.setError("Too many characters");
                } else {
                    modelTxt.setError(null);
                    count += 1;
                }

                input = editionTxt.getText().toString().trim();
                if (input.length() > 20) {
                    editionTxt.setError("Too many characters");
                } else {
                    editionTxt.setError(null);
                    count += 1;
                }

                input = bodyStylesTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    bodyStylesTxt.setError("This field cannot be empty");
                } else {
                    bodyStylesTxt.setError(null);
                    count += 1;
                }

                input = engineTypesTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    engineTypesTxt.setError("This field cannot be empty");
                } else {
                    engineTypesTxt.setError(null);
                    count += 1;
                }

                input = MSRPTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    MSRPTxt.setError("This field cannot be empty");
                } else {
                    MSRPTxt.setError(null);
                    count += 1;
                }

                input = seatsTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    seatsTxt.setError("This field cannot be empty");
                } else {
                    seatsTxt.setError(null);
                    count += 1;
                }

                input = startDateTxt.getText().toString().trim();
                int input2;
                if (input.isEmpty()) {
                    startDateTxt.setError("This field cannot be empty");
                } else {
                    input2 = Integer.parseInt(startDateTxt.getText().toString().trim());
                    if (input2 < 0 || input2 > LocalDate.now().getYear()) {
                        startDateTxt.setError("Value needs to be between 0 and " + LocalDate.now().getYear());
                    } else {
                        startDateTxt.setError(null);
                        count += 1;
                    }
                }

                input = endDateTxt.getText().toString().trim();
                if (now.isChecked()) {
                    count += 1;
                }
                if (input.isEmpty()) {
                    endDateTxt.setError("This field cannot be empty");
                } else {
                    input2 = Integer.parseInt(endDateTxt.getText().toString().trim());
                    if (input2 < 0 || input2 > LocalDate.now().getYear()) {
                        endDateTxt.setError("Value needs to be between 0 and " + LocalDate.now().getYear());
                    } else {
                        endDateTxt.setError(null);
                        count += 1;
                    }
                }

                if (count == 9 && imageSubmitted) {
//                    Toast.makeText(getActivity(), "Everything is filled in", Toast.LENGTH_SHORT).show();
                    showAlertDialog();
                } else if (count == 9) {
                    Toast.makeText(getActivity(), "Upload an image", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Fill everything required in", Toast.LENGTH_SHORT).show();
//                    showAlertDialog();
                }

            }

        });

        now.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (now.isChecked()) {
                    textEndDate.setVisibility(View.GONE);
                    endDateTxt.setText("-1");
                } else {
                    textEndDate.setVisibility(View.VISIBLE);
                    endDateTxt.setText("");
                }
            }
        });

//        // The device is in light mode
//        int[] colors = {Color.TRANSPARENT, Color.rgb(42, 100, 134)};
//
//        // Create a gradient drawable with the start and end colors
//        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.BR_TL, colors);
//
//        // Define the bounds of the gradient (in this case, the bottom 200 pixels of the fragment view)
//        int bottom = view.getHeight();
//        int top = bottom - 200;
//        gradientDrawable.setBounds(0, top, view.getWidth(), bottom);
//        gradientDrawable.setAlpha(50);
//        view.setBackground(gradientDrawable);

        getImage();

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> adapterBrand = new ArrayAdapter<String>(requireContext(), R.layout.list_item, brands);
        brandTxt.setAdapter(adapterBrand);

        ArrayAdapter<String> adapterBodyStyle = new ArrayAdapter<String>(requireContext(), R.layout.list_item, bodyStyles);
        bodyStylesTxt.setAdapter(adapterBodyStyle);

        ArrayAdapter<String> adapterEngineType = new ArrayAdapter<String>(requireContext(), R.layout.list_item, engineTypes);
        engineTypesTxt.setAdapter(adapterEngineType);
    }

    /**
     * This part of the Class is for uploading the image
     */
    protected void getImage() {
        /**
         * This code is simply for waiting for the press of the 'Add picture' button.
         * It will then save the selected image as a String, for sending it to the database.
         */
        uploadImage.setOnClickListener(new View.OnClickListener() {
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
                    image.setImageURI(selectedImageUri);
                    imageSubmitted = true;
                }
            }
        }
    }

    private void showAlertDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Submit Wiki")
                .setMessage("Are you sure you want to submit this wiki?")
                .setNeutralButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to neutral button press
                    }
                })
//                .setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        // Respond to negative button press
//                    }
//                })
                .setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Respond to positive button press
                        // Convert the URI to a Base64 encoded string
//                        base64Uri = Base64.getEncoder().encodeToString(selectedImageUri.toString().getBytes(StandardCharsets.UTF_8));

                        progressIndicatorAddWikiView.show();

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
                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);

                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                                QUEUE_URL,
                                response -> {
                                    // Handle the response
                                    Toast.makeText(getActivity(),"Wiki Submitted", Toast.LENGTH_LONG).show();
                                    progressIndicatorAddWikiView.hide();
                                },
                                error -> {
                                    // Handle the error
                                    Toast.makeText(getActivity(),"Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                                    progressIndicatorAddWikiView.hide();
                                }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                String body = null;

                                String endDate;
                                if (endDateTxt.getText().toString().trim().equals("-1")){
                                    endDate = "Now";
                                }else {
                                    endDate = endDateTxt.getText().toString().trim();
                                }

                                try {
                                    body = "image=" + URLEncoder.encode(base64Image, "UTF-8")
                                            + "&" + "brand=" + brandTxt.getText().toString().trim()
                                            + "&" + "model=" + modelTxt.getText().toString().trim()
                                            + "&" + "edition=" + editionTxt.getText().toString().trim()
                                            + "&" + "type=" + bodyStylesTxt.getText().toString().trim()
                                            + "&" + "enginetype=" + engineTypesTxt.getText().toString().trim()
                                            + "&" + "msrp=" + MSRPTxt.getText().toString().trim()
                                            + "&" + "startbuild=" + startDateTxt.getText().toString().trim()
                                            + "&" + "endbuild=" + endDate
                                            + "&" + "seats=" + seatsTxt.getText().toString().trim()
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


//                        RequestQueue queue = Volley.newRequestQueue(getActivity());
//
//                        InputStream inputStream = null;
//                        try {
//                            inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
//                        } catch (FileNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        // Convert the image data to Base64 format
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] imageBytes = baos.toByteArray();
//                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//                        // Create a JSON object with the image data and additional data
//                        JSONObject postData = new JSONObject();
//                        try {
//                            postData.put("brand", brandTxt.getText().toString().trim());
//                            postData.put("image", base64Image);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
////                        postData.put("model", modelTxt.getText().toString().trim());
//
//                        // Create a new JsonObjectRequest with the POST method and the JSON object as the request body
//                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, QUEUE_URL, postData,
//                                new Response.Listener<JSONObject>() {
//                                    @Override
//                                    public void onResponse(JSONObject response) {
//                                        // Handle the response
//                                        Toast.makeText(getActivity(),"Wiki Submitted", Toast.LENGTH_LONG).show();
//                                    }
//                                }, new Response.ErrorListener() {
//                            @Override
//                            public void onErrorResponse(VolleyError error) {
//                                // Handle the error
//                            }
//                        });
//
//                        // Add the request to the Volley request queue
//                        queue.add(request);


//                        InputStream inputStream = null;
//                        try {
//                            inputStream = getActivity().getContentResolver().openInputStream(selectedImageUri);
//                        } catch (FileNotFoundException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
//                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                        byte[] imageBytes = baos.toByteArray();
//                        String base64Image = Base64.getEncoder().encodeToString(imageBytes);
//
//                        // Create a JSON object to hold the key-value pairs
//                        JSONObject jsonObject = new JSONObject();
//                        try {
////                            jsonObject.put("brand", brandTxt.getText().toString().trim());
//                            jsonObject.put("image", base64Image);
//                        } catch (JSONException e) {
//                            throw new RuntimeException(e);
//                        }
//
//                        StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                                QUEUE_URL,
//                                response -> {
//                                    // Handle the response
//                                    Toast.makeText(getActivity(),"Wiki Submitted",Toast.LENGTH_LONG).show();
//                                },
//                                error -> {
//                                    // Handle the error
//                                }) {
//                            @Override
//                            public byte[] getBody() throws AuthFailureError {
//                                return jsonObject.toString().getBytes();
//                            }
//                            @Override
//                            public String getBodyContentType() {
//                                return "application/json; charset=utf-8";
//                            }
//                        };
//
//                        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
//                        requestQueue.add(stringRequest);


                    }
                })
                .show();
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("now", now.isChecked());
    }
}