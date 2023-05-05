package com.example.carspotter;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Arrays;
import java.util.Calendar;

public class AddWikiFragment extends Fragment {

    String item_dropdown = "";
    String[] brands = {"Audi","Volkswagen","Volvo","Mazda","Porsche","Seat","BMW","Mercedes","Subaru","Bentley","Tesla","Citroën","Peugeot","Opel","Renault","Skoda","Ford"};
    String[] bodyStyles = {"Convertible","Coupé","Hatchback","Sedan","Shooting Brake","Station Wagon","SUV"};
    String[] engineTypes = {"Fuel","Electric"};

    AutoCompleteTextView brandTxt;
    TextInputLayout textBrand;
    AutoCompleteTextView bodyStylesTxt;
    TextInputLayout textBodyStyles;
    AutoCompleteTextView engineTypesTxt;
    TextInputLayout textEngineTypes;
    TextInputEditText modelTxt;
    TextInputLayout textModel;
    ExtendedFloatingActionButton add_wiki_fab;
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

        add_wiki_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (brandTxt.getText().toString().trim().length() > 0 && modelTxt.getText().toString().trim().length() > 0 && bodyStylesTxt.getText().toString().trim().length() > 0 && engineTypesTxt.getText().toString().trim().length() > 0){
                    Toast.makeText(getActivity(), "Everything is filled in.", Toast.LENGTH_SHORT).show();
                }
                else {

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