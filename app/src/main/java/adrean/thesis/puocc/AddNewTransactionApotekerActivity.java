package adrean.thesis.puocc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewTransactionApotekerActivity extends AppCompatActivity implements View.OnClickListener{

    private IntentIntegrator qrScan;
    ListAdapter adapter;
    private ArrayList<Map<String,String>> listScanMedicine = new ArrayList<>();
    ListView listQrMed;
    QrAdapter listAdapter;
    List<String> listQt = new ArrayList<>();
    TextView trxPrescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction_apoteker);

        Button btnScan = (Button) findViewById(R.id.BtnScan);
        Button submitQrBtn = (Button) findViewById(R.id.submitQr);
        listQrMed = (ListView) findViewById(R.id.listScanItem);
        trxPrescription = (TextView) findViewById(R.id.trxPrescription);

        listQrMed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });

//        listAdapter = new QrAdapter(this,listScanMedicine, 1);
//        listQrMed.setAdapter(listAdapter);

        trxPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AddNewTransactionApotekerActivity.this,AddPrescriptionActivity.class);
                startActivity(in);
            }
        });

        submitQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AddNewTransactionApotekerActivity.this,ConfirmationQrTransactionActivity.class);
                in.putExtra("data",listScanMedicine);
                startActivity(in);
            }
        });

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
                    Map<String,String> medicineMap = new HashMap<>();
                    String mdName = obj.getString("name");
                    String mdPrice = obj.getString("price");
                    String mdCat = obj.getString("category");
                    String mdId = obj.getString("id");

                    medicineMap.put("MED_NAME",mdName);
                    medicineMap.put("PRICE",mdPrice);
                    medicineMap.put("CATEGORY",mdCat);
                    medicineMap.put("ID",mdId);

                    listScanMedicine.add(medicineMap);

                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }

                listAdapter = new QrAdapter(this,listScanMedicine, 1);
                listQrMed.setAdapter(listAdapter);

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
