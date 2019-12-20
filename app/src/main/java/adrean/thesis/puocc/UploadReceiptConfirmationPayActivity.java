package adrean.thesis.puocc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UploadReceiptConfirmationPayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt_confirmation_pay);

        TextView txt = (TextView) findViewById(R.id.text);

        Intent in = getIntent();
        List<Map<String,String>> data = (List<Map<String, String>>) in.getSerializableExtra("data");

        int totalPrice = 0;
        String totalMedicine = "";

        for(Map<String, String> mapData : data){
            String price = mapData.get("PRICE");
            String med = mapData.get("MED_NAME") + "\n";
            Log.d("price", price);
            totalPrice += Integer.parseInt(price);
            totalMedicine += med;
        }

        Log.d("totalPrice", String.valueOf(totalPrice));
        txt.setText(totalMedicine + "Total Price : "+ String.valueOf(totalPrice));

    }
}
