package be.kuleuven.gt.javabean;


import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView txtInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_confirmation_activity);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
    }
}
