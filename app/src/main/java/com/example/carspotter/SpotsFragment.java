package com.example.carspotter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SpotsFragment extends Fragment implements RecyclerViewInterface{

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsFromCarId";
    private List<Spot> spots = new ArrayList<>();
    private RecyclerView spotView;
    private TextView spotInfo;
    private TextView spotCar;
    private CircularProgressIndicator circularProgressIndicatorCarView;
    private ExtendedFloatingActionButton extendedFloatingActionButton;

    View view;
    Car car;

    public SpotsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_spots, container, false);
        circularProgressIndicatorCarView = (CircularProgressIndicator) view.findViewById(R.id.progressIndicatorSpotView);
        circularProgressIndicatorCarView.setVisibility(View.VISIBLE);
        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.add_spot_fab);

        Bundle bundle = this.getArguments();
        car = bundle.getParcelable("Car");

        //Text fields on fragment_spots.xml
        spotInfo = (TextView) view.findViewById(R.id.spotInfo);
        spotCar = (TextView) view.findViewById(R.id.spotCar);
        spotCar.setText(car.getBrand() + " " + car.getModel() + " " + car.getEdition());

        //Recyclerview on fragment_spots.xml
        spotView = view.findViewById( R.id.spotView);
        SpotsAdapter adapter = new SpotsAdapter( spots, this );
        spotView.setAdapter( adapter );
        spotView.setLayoutManager( new LinearLayoutManager( getActivity() ));

        spots.clear();
        spotView.getAdapter().notifyDataSetChanged();
        System.out.println(Integer.toString(car.getId()));
        requestSpotsFromCarId(Integer.toString(car.getId()));

        spotView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 10 & extendedFloatingActionButton.isShown()){
                    extendedFloatingActionButton.hide();
                }
                if (dy < -10 && !extendedFloatingActionButton.isShown()) {
                    extendedFloatingActionButton.show();
                }
                if (!recyclerView.canScrollVertically(-1)) {
                    extendedFloatingActionButton.show();
                }
                else if (!recyclerView.canScrollVertically(1)) {
                    extendedFloatingActionButton.hide();
                }
            }
        });

        return view;
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
                            spotInfo.setText("");
                        }
                        else{
                            if (car.getEdition().equals("")){
                                spotInfo.setText("No spots from " + car.getBrand() + " " + car.getModel() + " added yet!");
                            }
                            else {
                                spotInfo.setText("No spots from " + car.getBrand() + " " + car.getModel() + " " + car.getEdition() + " added yet!");
                            }
                        }
                        spotView.getAdapter().notifyDataSetChanged();
                        circularProgressIndicatorCarView.setVisibility(View.INVISIBLE);
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
//        cars.add(cars.get(0));
    }

    @Override
    public void onItemClick(int position) {

    }
}