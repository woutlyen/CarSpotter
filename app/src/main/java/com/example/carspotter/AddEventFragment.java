package com.example.carspotter;

import static android.app.Activity.RESULT_OK;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Locale;

public class AddEventFragment extends Fragment {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/AddEvent";
    private String dateForDB;
    boolean imageSubmitted = false;
    private TextInputLayout textName;
    private TextInputEditText nameTxt;
    private TextInputLayout textDescription;
    private TextInputEditText descriptionTxt;
    private TextInputLayout textType;
    private TextInputEditText typeTxt;
    private TextInputLayout textFee;
    private TextInputEditText feeTxt;
    private TextInputLayout textDate;
    private TextInputEditText dateTxt;
    private TextInputLayout textStartTime;
    private TextInputEditText startTimeTxt;
    private TextInputLayout textEndTime;
    private TextInputEditText endTimeTxt;
    private TextInputEditText locationTxt;
    private ExtendedFloatingActionButton add_event_fab;

    Button uploadImage;
    ShapeableImageView image;
    LinearProgressIndicator progressIndicatorAddEventView;

    private String base64Uri;
    private Uri selectedImageUri;
    int SELECT_PICTURE = 200;

    private Date dateOfEvent;
    View view;

    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_event, container, false);

        textName = (TextInputLayout) view.findViewById(R.id.textUsername);
        nameTxt = (TextInputEditText) view.findViewById(R.id.nameTxt);
        textDescription = (TextInputLayout) view.findViewById(R.id.textPassword);
        descriptionTxt = (TextInputEditText) view.findViewById(R.id.descriptionTxt);
        textType = (TextInputLayout) view.findViewById(R.id.textType);
        typeTxt = (TextInputEditText) view.findViewById(R.id.typeTxt);
        textFee = (TextInputLayout) view.findViewById(R.id.textFee);
        feeTxt = (TextInputEditText) view.findViewById(R.id.feeTxt);
        textDate = (TextInputLayout) view.findViewById(R.id.textDate);
        dateTxt = (TextInputEditText) view.findViewById(R.id.dateTxt);
        textStartTime = (TextInputLayout) view.findViewById(R.id.textStartTime);
        startTimeTxt = (TextInputEditText) view.findViewById(R.id.startTimeTxt);
        textEndTime = (TextInputLayout) view.findViewById(R.id.textEndTime);
        endTimeTxt = (TextInputEditText) view.findViewById(R.id.endTimeTxt);
        locationTxt = (TextInputEditText) view.findViewById(R.id.locationTxt);

        add_event_fab = (ExtendedFloatingActionButton) view.findViewById(R.id.add_event_fab);

        uploadImage = (Button) view.findViewById(R.id.uploadImage);
        image = (ShapeableImageView) view.findViewById(R.id.image);
        progressIndicatorAddEventView = (LinearProgressIndicator) view.findViewById(R.id.progressIndicatorAddEventView);
        progressIndicatorAddEventView.hide();


        CalendarConstraints constraintsBuilder = new CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()).build();

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select D ate")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraintsBuilder)
                .build();

        boolean isSystem24Hour = android.text.format.DateFormat.is24HourFormat(getContext());
        int clockFormat = TimeFormat.CLOCK_24H;
//        if (!isSystem24Hour) {
//            clockFormat = TimeFormat.CLOCK_12H;
//        }

        MaterialTimePicker timePicker1 =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(clockFormat)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select Start Time")
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .build();

        MaterialTimePicker timePicker2 =
                new MaterialTimePicker.Builder()
                        .setTimeFormat(clockFormat)
                        .setHour(12)
                        .setMinute(0)
                        .setTitleText("Select End Time")
                        .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
                        .build();

        dateTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                datePicker.show(ft, "DatePickerDialog");
            }
        });

        startTimeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                timePicker1.show(ft, "TimePickerDialog");
            }
        });

        endTimeTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                timePicker2.show(ft, "TimePickerDialog");
            }
        });

        datePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                DateFormat obj = new SimpleDateFormat("dd/MM/yyyy");
                dateOfEvent = new Date(datePicker.getSelection());

                String date = obj.format(dateOfEvent);

                dateTxt.setText(date);
            }
        });

        timePicker1.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePicker1.getMinute() < 10) {
                    startTimeTxt.setText(timePicker1.getHour() + ":0" + timePicker1.getMinute());
                } else {

                    startTimeTxt.setText(timePicker1.getHour() + ":" + timePicker1.getMinute());
                }
            }
        });

        timePicker2.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePicker2.getMinute() < 10) {
                    endTimeTxt.setText(timePicker2.getHour() + ":0" + timePicker2.getMinute());
                } else {

                    endTimeTxt.setText(timePicker2.getHour() + ":" + timePicker2.getMinute());
                }
            }
        });

        add_event_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int count = 0;
                String input = nameTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    nameTxt.setError("This field cannot be empty");
                } else {
                    nameTxt.setError(null);
                    count += 1;
                }

                input = descriptionTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    descriptionTxt.setError("This field cannot be empty");
                } else if (input.length() > 20) {
                    descriptionTxt.setError("Too many characters");
                } else {
                    descriptionTxt.setError(null);
                    count += 1;
                }

                input = typeTxt.getText().toString().trim();
                if (input.length() > 20) {
                    typeTxt.setError("Too many characters");
                } else {
                    typeTxt.setError(null);
                    count += 1;
                }

                input = feeTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    feeTxt.setError("This field cannot be empty");
                } else {
                    feeTxt.setError(null);
                    count += 1;
                }

                input = dateTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    dateTxt.setError("This field cannot be empty");
                } else {
                    dateTxt.setError(null);
                    count += 1;
                }

                input = startTimeTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    startTimeTxt.setError("This field cannot be empty");
                } else {
                    startTimeTxt.setError(null);
                    count += 1;
                }

                input = endTimeTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    endTimeTxt.setError("This field cannot be empty");
                } else {
                    endTimeTxt.setError(null);
                    count += 1;
                }
                input = locationTxt.getText().toString().trim();
                if (input.isEmpty()) {
                    locationTxt.setError("This field cannot be empty");
                } else {
                    locationTxt.setError(null);
                    count += 1;
                }


                if (count == 8 && imageSubmitted) {
//                    Toast.makeText(getActivity(), "Everything is filled in", Toast.LENGTH_SHORT).show();

                    String inputDateStr = dateTxt.getText().toString().trim();
                    SimpleDateFormat inputDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

                    SimpleDateFormat outputDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    String outputDateStr = "";
                    try {
                        Date inputDate = inputDateFormat.parse(inputDateStr);
                        dateForDB = outputDateFormat.format(inputDate);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    showAlertDialog();
                } else if (count == 8) {
                    Toast.makeText(getActivity(), "Upload an image", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "Fill everything required in", Toast.LENGTH_SHORT).show();
//                    showAlertDialog();
                }

            }

        });

        getImage();

        return view;
    }

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
                    image.setImageURI(selectedImageUri);
                    imageSubmitted = true;
                }
            }
        }
    }

    private void showAlertDialog() {
        new MaterialAlertDialogBuilder(getContext())
                .setTitle("Submit Event")
                .setMessage("Are you sure you want to submit this event?")
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
                        // Convert the URI to a Base64 encoded string
//                        base64Uri = Base64.getEncoder().encodeToString(selectedImageUri.toString().getBytes(StandardCharsets.UTF_8));

                        progressIndicatorAddEventView.show();

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
                                    Toast.makeText(getActivity(), "Event Submitted", Toast.LENGTH_LONG).show();
                                    progressIndicatorAddEventView.hide();
                                },
                                error -> {
                                    // Handle the error
                                    Toast.makeText(getActivity(), "Something Went Wrong, Please Try Again", Toast.LENGTH_LONG).show();
                                    progressIndicatorAddEventView.hide();
                                }) {
                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                String body = null;

                                try {
                                    body = "image=" + URLEncoder.encode(base64Image, "UTF-8")
                                            + "&" + "name=" + nameTxt.getText().toString().trim()
                                            + "&" + "description=" + descriptionTxt.getText().toString().trim()
                                            + "&" + "date=" + dateForDB
                                            + "&" + "starthour=" + startTimeTxt.getText().toString().trim()
                                            + "&" + "endhour=" + endTimeTxt.getText().toString().trim()
                                            + "&" + "type=" + typeTxt.getText().toString().trim()
                                            + "&" + "fee=" + feeTxt.getText().toString().trim()
                                            + "&" + "location=" + locationTxt.getText().toString().trim()
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
}