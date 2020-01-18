package adrean.thesis.puocc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddNewTransactionApotekerActivity extends AppCompatActivity implements View.OnClickListener{

    private IntentIntegrator qrScan;
    ListAdapter adapter;
    private List<Map<String,Object>> listScanMedicine = new ArrayList<>();
    ListView listQrMed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_transaction_apoteker);

        Button btnScan = (Button) findViewById(R.id.BtnScan);
        listQrMed = (ListView) findViewById(R.id.listScanItem);
        TextView trxPrescription = (TextView) findViewById(R.id.trxPrescription);

        trxPrescription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(AddNewTransactionApotekerActivity.this,AddPrescriptionActivity.class);
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
                    Map<String,Object> medicineMap = new HashMap<>();
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

                adapter = new SimpleAdapter(
                        getApplicationContext(), listScanMedicine, R.layout.list_scanned_qr_medicine,
                        new String[]{"CATEGORY","MED_NAME","PRICE"},
                        new int[]{R.id.medCategory, R.id.medName,R.id.medPrice});

                listQrMed.setAdapter(adapter);
            }
        }else{
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onClick(View view) {
        qrScan.initiateScan();
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
