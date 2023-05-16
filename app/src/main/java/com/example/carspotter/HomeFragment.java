package com.example.carspotter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.UiModeManager;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
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
import com.google.android.material.divider.MaterialDivider;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class HomeFragment extends Fragment implements RecyclerViewInterface {

    private static final String QUEUE_URL = "https://studev.groept.be/api/a22pt304/GetSpotsForHome2";
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
    private boolean spotsGenerated = false;
    private RecyclerView newWiki;
    private BrandSelectAdapter adapter2;
    private int currentWiki = 0;
    private final Handler wikiHandler = new Handler();
    private Runnable wikiRunnable;
    private boolean wikiIsRunning = true;
    private boolean wikisGenerated = false;

    private RecyclerView newEvent;
    private TextView greetingTextView;
    private ImageView logoTop;
    private TextView slogan2;
    private MaterialDivider divider;
    private NestedScrollView scrollView;
    private ImageView scrollUpImage;
    private TextView thanksTxt;
    private TextView thanksTxt2;

    private ConstraintLayout loadingScreen;
    private LinearProgressIndicator loadingProgressBar;
    private LinearLayout recyclerviews;
    private boolean reset;

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

        if (savedInstanceState != null) {
            wikiIsRunning = savedInstanceState.getBoolean("wikiIsRunning");
            spotIsRunning = savedInstanceState.getBoolean("spotIsRunning");
        }

        loadingScreen = (ConstraintLayout) view.findViewById(R.id.loading_screen);
        recyclerviews = (LinearLayout) view.findViewById(R.id.linearLayout3);

        loadingScreen.setVisibility(View.VISIBLE);
        recyclerviews.setVisibility(View.INVISIBLE);

        newSpot = (RecyclerView) view.findViewById(R.id.newSpot);
        newWiki = (RecyclerView) view.findViewById(R.id.newWiki);
//        newEvent = (RecyclerView) view.findViewById(R.id.newEvent);

        adapter = new SpotsAdapter(spots, this);
        newSpot.setAdapter(adapter);
        newSpot.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(newSpot);
        newSpot.addItemDecoration(new PagerDecorator());

        adapter2 = new BrandSelectAdapter(cars, this);
        newWiki.setAdapter(adapter2);
        newWiki.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));

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
        String user = "";
        if (((MainActivity) (getContext())).getUser() != null){
            user = " " + ((MainActivity) (getContext())).getUser();
        }
        if (timeOfDay < 12) {
            greeting = "Good morning"+user+"!";
        } else if (timeOfDay < 18) {
            greeting = "Good afternoon"+user+"!";
        } else {
            greeting = "Good evening"+user+"!";
        }

        greeting += " \uD83D\uDC4B";

        // Display the greeting message
        greetingTextView.setText(greeting);

        logoTop = (ImageView) view.findViewById(R.id.logoTop);
        slogan2 = (TextView) view.findViewById(R.id.slogan2);
        divider = (MaterialDivider) view.findViewById(R.id.divider);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        scrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(@NonNull NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                int logoHeight = logoTop.getHeight();
                if (scrollY > 4 * logoHeight) {
                    logoTop.setVisibility(View.VISIBLE);
                    logoTop.setAlpha(1f);
                    slogan2.setVisibility(View.VISIBLE);
                    slogan2.setAlpha(1f);
                    divider.setVisibility(View.VISIBLE);
                    divider.setAlpha(1f);
                } else if (scrollY > 3 * logoHeight) {
                    logoTop.setVisibility(View.VISIBLE);
                    logoTop.setAlpha((float) (scrollY - 3 * logoHeight) / logoHeight);
                    slogan2.setVisibility(View.VISIBLE);
                    slogan2.setAlpha((float) (scrollY - 3 * logoHeight) / logoHeight);
                    divider.setVisibility(View.VISIBLE);
                    divider.setAlpha((float) (scrollY - 3 * logoHeight) / logoHeight);
                } else {
                    logoTop.setVisibility(View.INVISIBLE);
                    slogan2.setVisibility(View.INVISIBLE);
                    divider.setVisibility(View.INVISIBLE);
                }

                if(recyclerviews.getAlpha() > 0) {
                    if (scrollY > 3 * logoHeight) {
                        greetingTextView.setVisibility(View.INVISIBLE);
                        scrollUpImage.setVisibility(View.INVISIBLE);
                    } else if (scrollY > 2 * logoHeight) {
                        greetingTextView.setVisibility(View.VISIBLE);
                        greetingTextView.setAlpha((float) 1 - (float) (scrollY - 2 * logoHeight) / logoHeight);
                        scrollUpImage.setVisibility(View.VISIBLE);
                        scrollUpImage.setAlpha((float) 1 - (float) (scrollY - 2 * logoHeight) / logoHeight);
                    } else {
                        greetingTextView.setVisibility(View.VISIBLE);
                        greetingTextView.setAlpha(1f);
                        scrollUpImage.setVisibility(View.VISIBLE);
                        scrollUpImage.setAlpha(1f);
                    }
                }
            }
        });

        requestSpotsForHome();
        requestWikisForHome();

        thanksTxt = (TextView) view.findViewById(R.id.thanksTxt);
        thanksTxt2 = (TextView) view.findViewById(R.id.thanksTxt2);
        scrollUpImage = (ImageView) view.findViewById(R.id.scrollUpImage);
        scrollUpImage.setAlpha(0f);
        recyclerviews.setVisibility(View.GONE);
        thanksTxt2.setVisibility(View.GONE);
        recyclerviews.setAlpha(0f);
        startAnimation();

        // The device is in light mode
//        int[] colors = {Color.TRANSPARENT, Color.rgb(177, 214, 255)};
        int[] colors = {Color.TRANSPARENT, Color.rgb(42, 100, 134)};

        // Create a gradient drawable with the start and end colors
        GradientDrawable gradientDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);

        // Define the bounds of the gradient (in this case, the bottom 200 pixels of the fragment view)
        int bottom = view.getHeight();
        int top = bottom - 200;
        gradientDrawable.setBounds(0, top, view.getWidth(), bottom);
        gradientDrawable.setAlpha(60);
        view.setBackground(gradientDrawable);

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

        reset = true;

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final float scale = getResources().getDisplayMetrics().density;
        int pixels = (int) (320 * scale + 0.5f);

        LinearLayout ll = (LinearLayout) view.findViewById(R.id.linearLayout3);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams)ll.getLayoutParams();
        params.setMargins(0, getResources().getDisplayMetrics().heightPixels-pixels, 0, 0); //substitute parameters for left, top, right, bottom
        ll.setLayoutParams(params);

    }

    private void startAnimation() {
        float startY = (float) 0.54 * getResources().getDisplayMetrics().heightPixels;
        float endY = startY - 100; // Move up by 500 pixels
        long duration = 1500; // Duration of animation in milliseconds

        // Create an AnimatorSet to sequence the animations
        AnimatorSet animatorSet = new AnimatorSet();

        ValueAnimator moveUpAnimator = ObjectAnimator.ofFloat(scrollUpImage, "y", startY, endY);
        moveUpAnimator.setDuration(duration / 4);
        moveUpAnimator.setInterpolator(new AccelerateInterpolator());

        // Create a ValueAnimator to slow down the image
        ValueAnimator slowDownAnimator = ObjectAnimator.ofFloat(scrollUpImage, "y", endY, endY);
        slowDownAnimator.setDuration(duration / 4);

        // Create a ValueAnimator to stop the image
        ValueAnimator stopAnimator = ObjectAnimator.ofFloat(scrollUpImage, "y", endY, endY);
        stopAnimator.setDuration(duration / 4);

        // Create a ValueAnimator to move the image down quickly
        ValueAnimator moveDownAnimator = ObjectAnimator.ofFloat(scrollUpImage, "y", endY, startY);
        moveDownAnimator.setDuration(duration / 4);
        moveDownAnimator.setInterpolator(new DecelerateInterpolator());

        // Create a ValueAnimator to slow down the image
        ValueAnimator slowDownAnimator2 = ObjectAnimator.ofFloat(scrollUpImage, "y", startY, startY);
        slowDownAnimator2.setDuration(duration / 4);

        // Add the animators to the AnimatorSet and play them sequentially
        animatorSet.playSequentially(moveUpAnimator, slowDownAnimator, stopAnimator, moveDownAnimator, slowDownAnimator2);

        animatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (spotIsRunning) {
                    startSpotsAutoScroll();
                    spotIsRunning = false;
                }
                if (wikiIsRunning) {
                    startWikiAutoScroll();
                    wikiIsRunning = false;
                }

                if(wikisGenerated && spotsGenerated && reset) {
//                    loadingScreen.setVisibility(View.GONE);
                    recyclerviews.setVisibility(View.VISIBLE);
//                    thanksTxt.setVisibility(View.INVISIBLE);
                    thanksTxt2.setVisibility(View.VISIBLE);
                    scrollUpImage.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null);
                    recyclerviews.animate()
                            .alpha(1f)
                            .setDuration(1000)
                            .setListener(null);
                    loadingScreen.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(null);
                    thanksTxt.animate()
                            .alpha(0f)
                            .setDuration(300)
                            .setListener(null);
                    reset = false;
                }
                animatorSet.start();
            }
        });

        animatorSet.start();
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

//                        scrollUpImage.animate()
//                                .alpha(1f)
//                                .setDuration(500)
//                                .setListener(null);

                        processJSONResponse(response);
                        newSpot.getAdapter().notifyDataSetChanged();
                        spotsGenerated = true;
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
                        recyclerviews.setVisibility(View.GONE);
                        loadingScreen.setVisibility(View.GONE);
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
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL2,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        processJSONResponse2(response);
                        newWiki.getAdapter().notifyDataSetChanged();
                        wikisGenerated = true;
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                getActivity(),
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                        recyclerviews.setVisibility(View.GONE);
                        loadingScreen.setVisibility(View.GONE);
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

        if (type.equals("Car")) {
            modelViewFragment.setArguments(bundle);
            bundle.putParcelable("Car", cars.get(position));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, modelViewFragment); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }

        if (type.equals("Spot")) {
            spotLocationFragment.setArguments(bundle);
            bundle.putParcelable("Spot", spots.get(position));
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.flFragment, spotLocationFragment); // give your fragment container id in first parameter
            transaction.addToBackStack(null);  // if written, this transaction will be added to backstack
            transaction.commit();
        }

    }

    // Start the auto-scrolling process
    private void startWikiAutoScroll() {
        // Create a new runnable to change the current page
        if (adapter2.getItemCount() != 0) {
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
    }

    // Start the auto-scrolling process
    private void startSpotsAutoScroll() {
        // Create a new runnable to change the current page

        if (adapter.getItemCount() != 0) {
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
    }

    private void stopWikiAutoScroll() {
        wikiHandler.removeCallbacks(wikiRunnable);
    }

    private void stopSpotsAutoScroll() {
        spotHandler.removeCallbacks(spotRunnable);
    }

    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putBoolean("wikiIsRunning", wikiIsRunning);
        outState.putBoolean("spotIsRunning", spotIsRunning);
    }

}