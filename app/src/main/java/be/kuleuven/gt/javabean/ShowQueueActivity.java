package be.kuleuven.gt.javabean;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import be.kuleuven.gt.javabean.model.CoffeeOrder;

public class ShowQueueActivity extends AppCompatActivity {

    private List<CoffeeOrder> orders = new ArrayList<>();
    private RecyclerView orderQueueView;
    private static final String QUEUE_URL = "https://studev.groept.be/api/ptdemo/queue";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_queue);
        orderQueueView = findViewById( R.id.orderQueueView );
        CoffeeOrderAdapter adapter = new CoffeeOrderAdapter( orders );
        orderQueueView.setAdapter( adapter );
        orderQueueView.setLayoutManager( new LinearLayoutManager( this ));
        requestCoffeeOrderqueue();
    }
    private void requestCoffeeOrderqueue() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonArrayRequest queueRequest = new JsonArrayRequest(
                Request.Method.GET,
                QUEUE_URL,
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        // iteration 3
                        processJSONResponse(response);
                        orderQueueView.getAdapter().notifyDataSetChanged();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(
                                ShowQueueActivity.this,
                                "Unable to communicate with the server",
                                Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(queueRequest);
    }

    private void processJSONResponse(JSONArray response) {
        for (int i = 0; i < response.length(); i++) {
            try {
                CoffeeOrder order = new CoffeeOrder(response.getJSONObject(i));
                orders.add(order);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}