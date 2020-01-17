package adrean.thesis.puocc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AddNewTransactionApotekerActivity extends AppCompatActivity implements View.OnClickListener{

    private IntentIntegrator qrScan;
    private TextView medName,medPrice,medId;
    private List<HashMap<String,String>> listScanMedicine = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction_apoteker);

        Button btnScan = (Button) findViewById(R.id.BtnScan);
        medName = (TextView) findViewById(R.id.DrugsName);
        medPrice = (TextView) findViewById(R.id.DrugsPrice);
        medId = (TextView) findViewById(R.id.id);

        qrScan = new IntentIntegrator(this);
        btnScan.setOnClickListener(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {

            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            }else{
                try {
                    JSONObject obj = new JSONObject(result.getContents());

                    medName.setText(obj.getString("name"));
                    medPrice.setText(obj.getString("price"));
                    medId.setText(obj.getString("id"));
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }
}
