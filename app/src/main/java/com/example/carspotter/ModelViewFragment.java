package com.example.carspotter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;


public class ModelViewFragment extends Fragment {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetImageFromCarId";
    private TextView textBrand;
    private TextView textModel;
    private TextView textEdition;
    private TextView textType;

    private TextView textEngineType;
    private TextView textMSRP;
    private TextView textBuildYears;
    private TextView textSeats;
    private ImageView modelImageView;
    private Bitmap decodedImage;
    private LinearProgressIndicator linearProgressIndicator;
    private ExtendedFloatingActionButton extendedFloatingActionButton;
    private SpotsFragment spotsFragment = new SpotsFragment();


    View view;

    public ModelViewFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_model_view, container, false);

//        Car car =(Car) getActivity().getIntent().getParcelableExtra("Car");
        Bundle bundle = this.getArguments();
        Car car = bundle.getParcelable("Car");

        textBrand = (TextView) view.findViewById(R.id.textBrand);
        textBrand.setText(car.getBrand());
        textModel = (TextView) view.findViewById(R.id.textModel);
        textModel.setText(car.getModel());
        textEdition = (TextView) view.findViewById(R.id.textEdition);
        textEdition.setText(car.getEdition());
        textType = (TextView) view.findViewById(R.id.textType);
        textType.setText(car.getType());
        textEngineType = (TextView) view.findViewById(R.id.textEngineType);
        textEngineType.setText(car.getEnginetype());
        textMSRP = (TextView) view.findViewById(R.id.textMSRP);
        textMSRP.setText(Integer.toString(car.getMsrp()));
        textBuildYears = (TextView) view.findViewById(R.id.textBuildYears);
        textBuildYears.setText(car.getStart_build() +" - "+ car.getEnd_build());
        textSeats = (TextView) view.findViewById(R.id.textSeats);
        textSeats.setText(car.getSeats());

        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.spots_fab);
        modelImageView = (ImageView) view.findViewById(R.id.modelImageView);
        linearProgressIndicator = (LinearProgressIndicator) view.findViewById(R.id.linearProgressIndicatorModelView);
        requestImageFromCarId(Integer.toString(car.getId()));

        extendedFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                spotsFragment.setArguments(bundle);
                bundle.putParcelable("Car", car);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.flFragment, spotsFragment ); // give your fragment container id in first parameter
                transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
                transaction.commit();
            }
        });

        return view;
    }

    private void requestImageFromCarId(String item) {
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
                        modelImageView.setImageBitmap(decodedImage);
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