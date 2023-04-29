package com.example.carspotter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carspotter.model.Car;

import java.util.List;

public class BrandSelectAdapter extends RecyclerView.Adapter<BrandSelectAdapter.ViewHolder> {
    private List<Car> carList;
    private final RecyclerViewInterface recyclerViewInterface;
    public BrandSelectAdapter(List<Car> carList, RecyclerViewInterface recyclerViewInterface) {
        this.carList = carList;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @Override
    public BrandSelectAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View carView = layoutInflater.inflate(R.layout.car_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(carView, recyclerViewInterface);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(BrandSelectAdapter.ViewHolder holder, int position) {
        Car car = carList.get(position);
        ((TextView) holder.carView.findViewById(R.id.model))
                .setText(car.getModel());
        ((TextView) holder.carView.findViewById(R.id.buildyear))
                .setText(car.getStart_build()+" - "+car.getEnd_build());
        ((ImageView) holder.carView.findViewById(R.id.image))
                .setImageBitmap(car.getDecodedImage());
    }
    @Override
    public int getItemCount() {
        return carList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View carView;
        public ViewHolder(View carView, RecyclerViewInterface recyclerViewInterface) {
            super(carView);
            this.carView = (View) carView;

            carView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });
        }
    }
}