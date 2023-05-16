package com.example.carspotter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.carspotter.model.Event;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

public class EventViewFragment extends Fragment {
    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetImageFromEventId";
    private TextView textName;
    private TextView textDate;
    private TextView textHours;
    private TextView textType;
    private TextView textFee;
    private TextView textDesc;
    private ImageView eventImageView;
    private Bitmap decodedImage;
    private LinearProgressIndicator linearProgressIndicator;
    View view;


    public EventViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_event_view, container, false);

        Bundle bundle = this.getArguments();
        Event event = bundle.getParcelable("Event");

        textName = (TextView) view.findViewById(R.id.textUsername);
        textName.setText(event.getName());
        textDate = (TextView) view.findViewById(R.id.textDate);
        textDate.setText(event.getOnlyDate());
        textHours = (TextView) view.findViewById(R.id.textHours);
        textHours.setText("From " + event.getStart_hour() + "h till " + event.getEnd_hour() + "h");
        textType = (TextView) view.findViewById(R.id.textType);
        textType.setText(event.getType());
        textFee = (TextView) view.findViewById(R.id.textFee);
        textFee.setText("€" + event.getFee());
        textDesc = (TextView) view.findViewById(R.id.textDesc);
        textDesc.setText(event.getDescription());

        eventImageView = (ImageView) view.findViewById(R.id.eventImageView);
        linearProgressIndicator = (LinearProgressIndicator) view.findViewById(R.id.linearProgressIndicatorEventView);
        requestImageFromEventId(Integer.toString(event.getId()));

        return view;
    }

    private void requestImageFromEventId(String item) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL+"/"+item,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        linearProgressIndicator.setVisibility(View.INVISIBLE);
                        eventImageView.setImageBitmap(decodedImage);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
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