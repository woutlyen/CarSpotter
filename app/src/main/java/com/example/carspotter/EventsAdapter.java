package com.example.carspotter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.carspotter.model.Event;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private List<Event> eventList;
    private final RecyclerViewInterface recyclerViewInterface;

    public EventsAdapter(List<Event> eventList, RecyclerViewInterface recyclerViewInterface) {
        this.eventList = eventList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @Override
    public EventsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View eventView = layoutInflater.inflate(R.layout.event_view, parent, false);
        EventsAdapter.ViewHolder myViewHolder = new EventsAdapter.ViewHolder(eventView, recyclerViewInterface);
        return myViewHolder;
    }

    @Override
    public void onBindViewHolder(EventsAdapter.ViewHolder holder, int position) {
        Event event = eventList.get(position);
        ((TextView) holder.eventView.findViewById(R.id.name))
                .setText(event.getName());
        ((TextView) holder.eventView.findViewById(R.id.date))
                .setText(event.getDate());
        ((TextView) holder.eventView.findViewById(R.id.fee))
                .setText(Integer.toString(event.getFee()));
        ((ImageView) holder.eventView.findViewById(R.id.image))
                .setImageBitmap(event.getDecodedImage());
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        public View eventView;

        public ViewHolder(View eventView, RecyclerViewInterface recyclerViewInterface) {
            super(eventView);
            this.eventView = (View) eventView;

            eventView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recyclerViewInterface != null) {
                        int pos = getAdapterPosition();
                        if (pos != RecyclerView.NO_POSITION) {
                            recyclerViewInterface.onItemClick(pos, "Event");
                        }
                    }
                }
            });
        }
    }
}