package be.kuleuven.gt.javabean;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import be.kuleuven.gt.javabean.model.CoffeeOrder;

public class CoffeeOrderAdapter extends RecyclerView.Adapter<CoffeeOrderAdapter.ViewHolder> {
    private List<CoffeeOrder> coffeeOrderList;
    public CoffeeOrderAdapter(List<CoffeeOrder> coffeeOrderList) {
        this.coffeeOrderList = coffeeOrderList;
    }
    @Override
    public CoffeeOrderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View orderView = layoutInflater.inflate(R.layout.order_view, parent, false);
        ViewHolder myViewHolder = new ViewHolder(orderView);
        return myViewHolder;
    }
    @Override
    public void onBindViewHolder(CoffeeOrderAdapter.ViewHolder holder, int position) {
        CoffeeOrder coffeeOrder = coffeeOrderList.get(position);
        ((TextView) holder.order.findViewById(R.id.orderName))
                .setText(coffeeOrder.getName());
        ((TextView) holder.order.findViewById(R.id.orderDetails))
                .setText(coffeeOrder.getDescription());
        ((TextView) holder.order.findViewById(R.id.orderQuantity))
                .setText(Integer.toString(coffeeOrder.getQuantity()));
    }
    @Override
    public int getItemCount() {
        return coffeeOrderList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        public View order;
        public ViewHolder(View coffeeorderView) {
            super(coffeeorderView);
            order = (View) coffeeorderView;
        }
    }
}