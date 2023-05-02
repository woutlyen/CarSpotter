package com.example.carspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Arrays;
import java.util.Calendar;

public class AddWikiFragment extends Fragment {

    String item_dropdown = "";
    String[] brands = {"Audi","Volkswagen","Volvo","Mazda","Porsche","Seat","BMW","Mercedes","Subaru","Bentley","Tesla","Citroën","Peugeot","Opel","Renault","Skoda","Ford"};
    String[] bodyStyles = {"Convertible","Coupé","Hatchback","Sedan","Shooting Brake","Station Wagon","SUV"};
    String[] engineTypes = {"Fuel","Electric"};

    AutoCompleteTextView brandTxt;
    AutoCompleteTextView bodyStylesTxt;
    AutoCompleteTextView engineTypesTxt;
    TextInputEditText modelTxt;
    View view;

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

        Arrays.sort(bodyStyles);
        bodyStylesTxt = (AutoCompleteTextView) view.findViewById(R.id.bodyStylesTxt);

        Arrays.sort(engineTypes);
        engineTypesTxt = (AutoCompleteTextView) view.findViewById(R.id.engineTypeTxt);

        modelTxt = (TextInputEditText) view.findViewById(R.id.modelTxt);

        // Set up listener for validation
        modelTxt.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    String input = modelTxt.getText().toString().trim();
                    if (input.isEmpty()) {
                        modelTxt.setError("This field cannot be empty");
                    } else {
                        modelTxt.setError(null);
                    }
                }
            }
        });

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
}