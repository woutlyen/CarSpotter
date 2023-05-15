package com.example.carspotter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;

public class AddEventFragment extends Fragment {

    private TextInputLayout textDate;
    private TextInputEditText dateTxt;
    private TextInputLayout textStartTime;
    private TextInputEditText startTimeTxt;
    private TextInputLayout textEndTime;
    private TextInputEditText endTimeTxt;

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

        textDate = (TextInputLayout) view.findViewById(R.id.textDate);
        dateTxt = (TextInputEditText) view.findViewById(R.id.dateTxt);
        textStartTime = (TextInputLayout) view.findViewById(R.id.textStartTime);
        startTimeTxt = (TextInputEditText) view.findViewById(R.id.startTimeTxt);
        textEndTime = (TextInputLayout) view.findViewById(R.id.textEndTime);
        endTimeTxt = (TextInputEditText) view.findViewById(R.id.endTimeTxt);


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
                if (timePicker1.getMinute() < 10){
                    startTimeTxt.setText(timePicker1.getHour() + ":0" + timePicker1.getMinute());
                }else {

                    startTimeTxt.setText(timePicker1.getHour() + ":" + timePicker1.getMinute());
                }
            }
        });

        timePicker2.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (timePicker2.getMinute() < 10){
                    endTimeTxt.setText(timePicker2.getHour() + ":0" + timePicker2.getMinute());
                }else {

                    endTimeTxt.setText(timePicker2.getHour() + ":" + timePicker2.getMinute());
                }
            }
        });

        return view;
    }
}