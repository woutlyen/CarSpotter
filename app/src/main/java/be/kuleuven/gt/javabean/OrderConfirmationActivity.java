package be.kuleuven.gt.javabean;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.Map;

import be.kuleuven.gt.javabean.model.CoffeeOrder;

public class OrderConfirmationActivity extends AppCompatActivity {
    private TextView txtInfo;
    private Button btnQueue;
    private static final String POST_URL = "https://studev.groept.be/api/ptdemo/order/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_confirmation);
        txtInfo = (TextView) findViewById(R.id.txtInfo);
        btnQueue = (Button) findViewById(R.id.btnQueue);
        CoffeeOrder order = (CoffeeOrder) getIntent().getParcelableExtra("Order");
        txtInfo.setText(order.toString());
        ProgressDialog progressDialog = new ProgressDialog(OrderConfirmationActivity.this);
        progressDialog.setMessage("Uploading, please wait...");
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest submitRequest = new StringRequest(
                Request.Method.POST,
                POST_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                OrderConfirmationActivity.this,
                                "Post request executed",
                                Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Toast.makeText(
                                OrderConfirmationActivity.this,
                                "Unable to place the order" + error,
                                Toast.LENGTH_LONG).show();
                    }
                }
        ) { //NOTE THIS PART: here we are passing the POST parameters to the webservice
            @Override
            protected Map<String, String> getParams() {
                /* Map<String, String> with key value pairs as data load */
                return order.getPostParameters();
            }
        };
        progressDialog.show();
        requestQueue.add(submitRequest);
    }

    public void onBtnShowQueue_Clicked(View caller) {
        Intent intent = new Intent(this, ShowQueueActivity.class);
        startActivity(intent);
    }
}