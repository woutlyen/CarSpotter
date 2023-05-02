package com.example.carspotter;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
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
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsForHome";
    private static final String QUEUE_URL2 = "https://studev.groept.be/api/a22pt304/GetWikisForHome";
    private final int DELAY_MS = 4000; // Delay in milliseconds
    private View view;
    private List<Spot> spots = new ArrayList<>();
    private List<Car> cars = new ArrayList<>();
    private RecyclerView newSpot;
    private SpotsAdapter adapter;
    private int currentSpot = 0;
    private final Handler spotHandler = new Handler();
    private Runnable spotRunnable;
    private boolean spotIsRunning = true;
    private RecyclerView newWiki;
    private BrandSelectAdapter adapter2;
    private int currentWiki = 0;
    private final Handler wikiHandler = new Handler();
    private Runnable wikiRunnable;
    private boolean wikiIsRunning = true;

    private RecyclerView newEvent;
    private TextView greetingTextView;

    private RelativeLayout loadingScreen;
    private CircularProgressIndicator loadingProgressBar;
    private LinearLayout recyclerviews;

//    private LinearProgressIndicator linearProgressIndicator1;
//    private LinearProgressIndicator linearProgressIndicator2;

    ModelViewFragment modelViewFragment = new ModelViewFragment();
    SpotLocationFragment spotLocationFragment = new SpotLocationFragment();

    private BottomNavigationView bottomNavigationView;
    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home, container, false);

        if(savedInstanceState != null){
            wikiIsRunning = savedInstanceState.getBoolean("wikiIsRunning");
            spotIsRunning = savedInstanceState.getBoolean("spotIsRunning");
        }

        loadingScreen = (RelativeLayout) view.findViewById(R.id.loading_screen);
        loadingProgressBar = (CircularProgressIndicator) view.findViewById(R.id.loading_progress_bar);
        recyclerviews = (LinearLayout) view.findViewById(R.id.linearLayout3);

        loadingScreen.setVisibility(View.VISIBLE);
        recyclerviews.setVisibility(View.INVISIBLE);

        newSpot = (RecyclerView) view.findViewById(R.id.newSpot);
        newWiki = (RecyclerView) view.findViewById(R.id.newWiki);
//        newEvent = (RecyclerView) view.findViewById(R.id.newEvent);

        adapter = new SpotsAdapter( spots, this );
        newSpot.setAdapter( adapter );
        newSpot.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.HORIZONTAL, false ));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(newSpot);
        newSpot.addItemDecoration(new PagerDecorator());

        adapter2 = new BrandSelectAdapter( cars, this );
        newWiki.setAdapter( adapter2 );
        newWiki.setLayoutManager( new LinearLayoutManager( getActivity(), LinearLayoutManager.HORIZONTAL, false ));

        PagerSnapHelper snapHelper2 = new PagerSnapHelper();
        snapHelper2.attachToRecyclerView(newWiki);
        newWiki.addItemDecoration(new PagerDecorator());

        bottomNavigationView = getActivity().findViewById(R.id.bottomNavigationView);
//        linearProgressIndicator1 = view.findViewById(R.id.linearProgressIndicator1);
//        linearProgressIndicator2 = view.findViewById(R.id.linearProgressIndicator2);

        // Find the greeting TextView by ID
        greetingTextView = view.findViewById(R.id.greeting_text_view);

        // Get the current time
        Calendar c = Calendar.getInstance();
        int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        // Set the greeting message based on the time
        String greeting;
        if (timeOfDay < 12) {
            greeting = "Good morning!";
        } else if (timeOfDay < 18) {
            greeting = "Good afternoon!";
        } else {
            greeting = "Good evening! ";
        }

        greeting += " \uD83D\uDC4B";

        // Display the greeting message
        greetingTextView.setText(greeting);

        requestSpotsForHome();
        requestWikisForHome();

        newWiki.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopWikiAutoScroll();
                return false;
            }
        });
        newSpot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                stopSpotsAutoScroll();
                return false;
            }
        });

        return view;
    }



    private void requestSpotsForHome() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse(response);
                        newSpot.getAdapter().notifyDataSetChanged();
//                        linearProgressIndicator1.setVisibility(View.INVISIBLE);
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
        spots.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Spot spot = new Spot(response.getJSONObject(i));
                spots.add(spot);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(spots);
    }

    private void requestWikisForHome() {
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL2,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse2(response);
                        newWiki.getAdapter().notifyDataSetChanged();
                        if(spotIsRunning){
                            startSpotsAutoScroll();
                            spotIsRunning = false;
                        }
                        if (wikiIsRunning){
                            startWikiAutoScroll();
                            wikiIsRunning = false;
                        }
//                        linearProgressIndicator2.setVisibility(View.INVISIBLE);
                        loadingScreen.setVisibility(View.GONE);
                        recyclerviews.setVisibility(View.VISIBLE);
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

    private void processJSONResponse2(JSONArray response) {
        cars.clear();
        for (int i = 0; i < response.length(); i++) {
            try {
                Car car = new Car(response.getJSONObject(i));
                cars.add(car);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Collections.reverse(cars);
    }


    @Override
    public void onItemClick(int position, String type) {
        Bundle bundle = new Bundle();
        bottomNavigationView.setSelectedItemId(R.id.item_2);

        if (type.equals("Car")){
            modelViewFragment.setArguments(bundle);
            bundle.putParcelable("Car", cars.get(position));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, modelViewFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }

        if (type.equals("Spot")){
            spotLocationFragment.setArguments(bundle);
            bundle.putParcelable("Spot", spots.get(position));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, spotLocationFragment ); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }

    }

    // Start the auto-scrolling process
    private void startWikiAutoScroll() {
        // Create a new runnable to change the current page
        wikiRunnable = new Runnable() {
            public void run() {
                currentWiki = (currentWiki + 1) % adapter2.getItemCount();
                newWiki.smoothScrollToPosition(currentWiki);
                wikiHandler.postDelayed(this, DELAY_MS);
            }
        };

        // Post the runnable to the message queue with the delay
        wikiHandler.postDelayed(wikiRunnable, DELAY_MS);
    }

    // Start the auto-scrolling process
    private void startSpotsAutoScroll() {
        // Create a new runnable to change the current page
        spotRunnable = new Runnable() {
            public void run() {
                currentSpot = (currentSpot + 1) % adapter.getItemCount();
                newSpot.smoothScrollToPosition(currentSpot);
                spotHandler.postDelayed(this, DELAY_MS);
            }
        };

        // Post the runnable to the message queue with the delay
        spotHandler.postDelayed(spotRunnable, DELAY_MS);
    }

    private void stopWikiAutoScroll(){
        wikiHandler.removeCallbacks(wikiRunnable);
    }

    private void stopSpotsAutoScroll(){
        spotHandler.removeCallbacks(spotRunnable);
    }

    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);

        outState.putBoolean("wikiIsRunning", wikiIsRunning);
        outState.putBoolean("spotIsRunning", spotIsRunning);
    }

}