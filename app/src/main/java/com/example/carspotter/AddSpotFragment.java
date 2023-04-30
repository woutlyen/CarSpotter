package com.example.carspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.carspotter.model.Car;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.w3c.dom.Text;

public class AddSpotFragment extends Fragment {
    private Button addSpotLocation;
    private Button addSpotPicture;
    private TextView latData;
    private TextView longData;
    private TextView addSpotInfo;

    private ExtendedFloatingActionButton extendedFloatingActionButton;
    View view;

    public AddSpotFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_spot, container, false);

        //Get info from selected car
        Bundle bundle = this.getArguments();
        Car car = bundle.getParcelable("Car");

        addSpotInfo = (TextView) view.findViewById(R.id.addSpotInfo);
        addSpotInfo.setText(car.getBrand() + ": " + car.getModel() + " " + car.getEdition());

        extendedFloatingActionButton = (ExtendedFloatingActionButton) view.findViewById(R.id.submit_fab);
        extendedFloatingActionButton.hide();

        return view;
    }

    //TO ADD: functie om Location toe te voegen: https://www.youtube.com/watch?v=mbQd6frpC3g&ab_channel=TechnicalCoding
    //TO ADD: functie om foto te uploaden (die vervangt ook placeholder van '+' icon)
    //TO ADD: onSubmitClick stuur data naar Database en vervolgens u terug naar Home of Spotter stuurt
}