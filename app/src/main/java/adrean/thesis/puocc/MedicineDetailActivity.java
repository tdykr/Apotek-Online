package adrean.thesis.puocc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class MedicineDetailActivity extends AppCompatActivity {

    private String id,medicineName,medicinePrice,medicineCategory,medicineQt,medicineDesc;
    private TextView medName,medCategory,medDesc;
    private EditText qtVal,medPrice;
    private String updatedQt,updatedPrice;
    private OutputStream outputStream;
    private ImageView qrCode,qtMin,qtAdd,mdImg;
    private Integer tempQt;
    private Button updateBtn,saveQrBtn;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medicine Detail");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        qtMin = (ImageView) findViewById(R.id.qtMin);
        qtAdd = (ImageView) findViewById(R.id.qtAdd);
        qtVal = (EditText) findViewById(R.id.qtVal);
        medName = (TextView) findViewById(R.id.medName);
        medCategory = (TextView) findViewById(R.id.medCategory);
        medPrice = (EditText) findViewById(R.id.medPrice);
        medDesc = (TextView) findViewById(R.id.medDesc);
        qrCode = (ImageView) findViewById(R.id.qrImg);
        mdImg = (ImageView) findViewById(R.id.medImg);
        updateBtn = (Button) findViewById(R.id.btnUpdate);
        saveQrBtn = (Button) findViewById(R.id.saveQR);

        Intent intent  = getIntent();
        id = intent.getStringExtra("ID");

        getMedicine();

        qtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tempQt = tempQt+1;
                qtVal.setText(Integer.toString(tempQt));
            }
        });

        qtMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tempQt = tempQt-1;
                qtVal.setText(Integer.toString(tempQt));
            }
        });

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updatedQt = qtVal.getText().toString();
                updatedPrice = medPrice.getText().toString();
                updateQuantity();
            }
        });

        saveQrBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(MedicineDetailActivity.this);
                BitmapDrawable drawable = (BitmapDrawable) qrCode.getDrawable();
                Bitmap bp = drawable.getBitmap();

                saveImg(bp);
            }
        });
    }



    private void saveImg(Bitmap bp) {
        BitmapDrawable drawable = (BitmapDrawable) qrCode.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File dir = new File(Environment.getExternalStorageDirectory().getPath() + "/Pictures/QRObat");
        dir.mkdirs();
        File file = new File(dir, "QRImg-" + medName + ".jpg");

        outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
            outputStream.flush();
            outputStream.close();
            final Intent intent = new Intent(getApplicationContext(), ApotekerMain.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            Toast.makeText(MedicineDetailActivity.this, "Image has been Saved", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MedicineDetailActivity.this, "Failed to Save This Image!", Toast.LENGTH_SHORT).show();
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    private void getMedicine(){
        class getMedicine extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MedicineDetailActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showDetail(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("ID",id);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_GET_MEDICINE_DETAIL,data);
                return s;
            }
        }
        getMedicine ge = new getMedicine();
        ge.execute();
    }

    private void updateQuantity(){
        class updateQt extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MedicineDetailActivity.this,"Updating...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(MedicineDetailActivity.this, "Quantity Successfully Updated", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MedicineDetailActivity.this, ApotekerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("ID",id);
                data.put("QUANTITY",updatedQt);
                data.put("PRICE",updatedPrice);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_QT,data);
                return s;
            }
        }
        updateQt ge = new updateQt();
        ge.execute();
    }

    private void showDetail(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject c = result.getJSONObject(0);
            medicineName = c.getString("MEDICINE_NAME");
            medicineCategory = c.getString("CATEGORY");
            medicinePrice = c.getString("PRICE");
            medicineQt= c.getString("QUANTITY");
            medicineDesc = c.getString("DESCRIPTION");

            String encodedQrString = c.getString("QR_CODE");
            String encodedMedPictString = c.getString("MEDICINE_PICT");
            tempQt = parseInt(medicineQt);

            medName.setText(medicineName);
            medCategory.setText(medicineCategory);
            medPrice.setText(medicinePrice);
            medDesc.setText(medicineDesc);
            qtVal.setText(medicineQt);

            mdImg.setImageBitmap(encodedStringImage(encodedMedPictString));
            qrCode.setImageBitmap(encodedStringImage(encodedQrString));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
