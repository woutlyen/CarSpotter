package com.example.carspotter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.carspotter.model.Spot;

import java.util.List;

public class SpotsAdapter extends RecyclerView.Adapter<SpotsAdapter.ViewHolder> {
    private List<Spot> spotList;
    private final RecyclerViewInterface recyclerViewInterface;
    public SpotsAdapter(List<Spot> spotList, RecyclerViewInterface recyclerViewInterface) {
        this.spotList = spotList;
        this.recyclerViewInterface = recyclerViewInterface;
    }
    @Override
    public SpotsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View spotView = layoutInflater.inflate(R.layout.spot_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(spotView, recyclerViewInterface);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(SpotsAdapter.ViewHolder holder, int position) {
        Spot spot = spotList.get(position);
        if (spot.getBrand() != null){
            holder.spotView.findViewById(R.id.spotCarModel).setVisibility(View.VISIBLE);
            ((TextView) holder.spotView.findViewById(R.id.spotCarModel))
                    .setText(spot.getBrand() + " " + spot.getModel() + " " + spot.getEdition());
            holder.spotView.findViewById(R.id.spotCarModel).setSelected(true);
        } else {
            holder.spotView.findViewById(R.id.spotCarModel).setVisibility(View.GONE);
        }
        ((TextView) holder.spotView.findViewById(R.id.spotWhen))
                .setText(spot.getDate());
        ((TextView) holder.spotView.findViewById(R.id.spotWhere))
                .setText(spot.getLocation());
        holder.spotView.findViewById(R.id.spotWhere).setSelected(true);
        ((ImageView) holder.spotView.findViewById(R.id.spotImage))
                .setImageBitmap(spot.getDecodedImage());
        if (!spot.getUsername().equals("null")){
            holder.spotView.findViewById(R.id.spotUsername).setVisibility(View.VISIBLE);
            ((TextView) holder.spotView.findViewById(R.id.spotUsername))
                    .setText("By "+ spot.getUsername());
        } else {
            holder.spotView.findViewById(R.id.spotUsername).setVisibility(View.GONE);
        }
    }
    @Override
    public int getItemCount() {
        return spotList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View spotView;
        public ViewHolder(View spotView, RecyclerViewInterface recyclerViewInterface) {
            super(spotView);
            this.spotView = (View) spotView;

            spotView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();
                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos, "Spot");
                        }
                    }
                }
            });
        }
    }
}