package be.kuleuven.gt.javabean;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import be.kuleuven.gt.javabean.model.CoffeeOrder;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView txtInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        CoffeeOrder order = (CoffeeOrder) getIntent().getParcelableExtra("Order");
        txtInfo.setText(order.toString());
    }
}