package be.kuleuven.gt.javabean;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
public class MainActivity extends AppCompatActivity {
    private Button btnPlus;
    private Button btnMinus;
    private Button btnSubmit;
    private TextView lblQty;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btnPlus = (Button) findViewById(R.id.btnPlus);
        btnMinus = (Button) findViewById(R.id.btnMinus);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);
        lblQty = (TextView) findViewById(R.id.lblQty);
    }
    public void onBtnPlus_Clicked(View Caller) {
        int quantity = Integer.parseInt(lblQty.getText().toString()) + 1;
        lblQty.setText(Integer.toString(quantity));
        if(!lblQty.getText().equals("0")){
            btnSubmit.setEnabled(true);
        }
    }
    public void onBtnMinus_Clicked(View Caller) {
        int quantity = Integer.parseInt(lblQty.getText().toString()) - 1;
        if(quantity < 0){
            quantity = 0;
        }
        lblQty.setText(Integer.toString(quantity));
        if(lblQty.getText().equals("0")){
            btnSubmit.setEnabled(false);
        }
    }

    public void onBtnSubmit_Clicked(View Caller) {
        Intent intent = new Intent(this, OrderConfirmationActivity.class);
        startActivity(intent);
    }

}