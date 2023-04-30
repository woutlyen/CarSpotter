package com.example.carspotter;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
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
import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class BrandSelectFragment extends Fragment implements RecyclerViewInterface {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetCarsFromBrand";
    private List<Car> cars = new ArrayList<>();
    private RecyclerView carView;
    private TextView infoTxt;
    private CircularProgressIndicator circularProgressIndicatorCarView;
    private ExtendedFloatingActionButton extendedFloatingActionButton;
    View view;
    String item_dropdown = "";
    String[] item = {"Audi","Volkswagen","Volvo","Mazda","Porsche","Seat","BMW","Mercedes","Subaru","Bentley","Tesla","Citroën","Peugeot","Opel","Renault","Skoda","Ford"};

    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> adapterItems;

    ModelViewFragment modelViewFragment = new ModelViewFragment();

    public BrandSelectFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_brand_select, container, false);
        infoTxt = (TextView) view.findViewById(R.id.noCarsTxt);
        if(item_dropdown.equals("")){
            infoTxt.setText("No brand selected!");
        }
        circularProgressIndicatorCarView = (CircularProgressIndicator) view.findViewById(R.id.progressIndicatorCarView);
        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.add_wiki_fab);

        Arrays.sort(item);
        autoCompleteTextView = view.findViewById(R.id.auto_complete_txt);

        carView = view.findViewById( R.id.carView );
        BrandSelectAdapter adapter = new BrandSelectAdapter( cars, this );
        carView.setAdapter( adapter );
        carView.setLayoutManager( new LinearLayoutManager( getActivity() ));
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                //Code for dropdown menu
                item_dropdown = adapterView.getItemAtPosition(i).toString();
                cars.clear();
                carView.getAdapter().notifyDataSetChanged();
                circularProgressIndicatorCarView.setVisibility(View.VISIBLE);
                infoTxt.setText("");
                requestCarsFromBrand(item_dropdown);
//                Toast.makeText(BrandSelectActivity.this, "Item: " + item, Toast.LENGTH_SHORT).show();
            }

        });

        carView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                //Make action button invisible when scrolling
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

    @Override
    public void onResume() {
        super.onResume();

        ArrayAdapter<String> adapterItems = new ArrayAdapter<String>(requireContext(), R.layout.list_item, item);
        autoCompleteTextView.setAdapter(adapterItems);
    }


    private void requestCarsFromBrand(String item) {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL+"/"+item,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        if (cars.size() != 0){
                            infoTxt.setText("");
                        }
                        else{
                            infoTxt.setText("No cars from " + item +" added yet!");
                        }
                        carView.getAdapter().notifyDataSetChanged();
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
        cars.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Car car = new Car(response.getJSONObject(i));
                cars.add(car);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.sort(cars, (o1, o2) -> o1.getModel().compareTo(o2.getModel()));
//        cars.add(cars.get(0));
    }

    @Override
    public void onItemClick(int position) {
//        Intent intent = new Intent(BrandSelectFragment.this.getActivity(), MainActivity.class);
//        intent.putExtra("Car", cars.get(position));
//        getActivity().startActivity(intent);

        Bundle bundle = new Bundle();
        modelViewFragment.setArguments(bundle);
        bundle.putParcelable("Car", cars.get(position));
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.flFragment, modelViewFragment ); // give your fragment container id in first parameter
        transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
        transaction.commit();
    }

}