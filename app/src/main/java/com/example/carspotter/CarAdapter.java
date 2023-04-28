package com.example.carspotter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carspotter.model.Car;

import java.util.List;

public class CarAdapter extends RecyclerView.Adapter<CarAdapter.ViewHolder> {
    private List<Car> carList;
    public CarAdapter(List<Car> coffeeOrderList) {
        this.carList = coffeeOrderList;
    }
    @Override
    public CarAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View orderView = layoutInflater.inflate(R.layout.car_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(orderView);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(CarAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        ((TextView) holder.carView.findViewById(R.id.model))
                .setText(car.getModel());
        ((TextView) holder.carView.findViewById(R.id.buildyear))
                .setText(car.getStart_build()+" - "+car.getEnd_build());
        ((TextView) holder.carView.findViewById(R.id.id_db))
                .setText(Integer.toString(car.getId()));
    }
    @Override
    public int getItemCount() {
        return carList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View carView;
        public ViewHolder(View carView) {
            super(carView);
            this.carView = (View) carView;
        }
    }
}