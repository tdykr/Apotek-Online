package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class AddNewProductActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private String[] doc = {"Obat Batuk","Obat Pusing"};
    private String mdCategory,mdName,mdPrice,mdNotes,imgStr;
    private String URL_INPUT_MED = "http://192.168.1.7/apotek/addMedicineDetail.php";
    boolean checkInput = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        Spinner sp = (Spinner) findViewById(R.id.medCategory);
        final EditText medicineName = (EditText) findViewById(R.id.medName);
        final EditText medicinePrice = (EditText) findViewById(R.id.medPrice);
        final EditText medicineNotes = (EditText) findViewById(R.id.medNotes);
        Button generate = (Button) findViewById(R.id.generateBtn);

        adapter = new ArrayAdapter<>(this,R.layout.support_simple_spinner_dropdown_item,doc);
        sp.setAdapter(adapter);
        sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0:
                        mdCategory = "Obat Batuk";
                        break;

                    case 1:
                        mdCategory = "Obat Pusing";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mdName = medicineName.getText().toString();
                mdPrice = medicinePrice.getText().toString();
                mdNotes = medicineNotes.getText().toString();
                String text = "{\"category\":\"" + mdCategory + "\",\"name\":\""+ mdName +"\",\"price\":\""+ mdPrice +"\"}";
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                //insert to db here, if success then try passing bitmap

                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    Intent in = new Intent(AddNewProductActivity.this,QRCodeImageAddProductActivity.class);

                    imgStr = getStringImage(bitmap);

                    addMedicine();

//                    if(checkInput){
                        in.putExtra("bitmap",bitmap);
                        in.putExtra("mdName",mdName);
                        in.putExtra("mdPrice",mdPrice);
                        in.putExtra("mdDesc",mdNotes);
                        in.putExtra("mdCat",mdCategory);
                        startActivity(in);
//                    }

                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void addMedicine(){

        class addMedicine extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddNewProductActivity.this,"Uploading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(AddNewProductActivity.this,s,Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("CATEGORY",mdCategory);
                params.put("MEDNAME",mdName);
                params.put("PRICE",mdPrice);
                params.put("DESC",mdNotes);
                params.put("QR",imgStr);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(URL_INPUT_MED, params);
                return res;
            }
        }

        addMedicine add = new addMedicine();
        add.execute();
    }
}
