package adrean.thesis.puocc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import static android.Manifest.permission.CAMERA;

public class AddNewProductActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private String mdCategory, mdName, mdPrice, mdNotes, imgStr, mdBitmapStr, JSON_STRING, id;
    private ImageView medPict;
    private static final int PIC_ID = 123;
    private Bitmap medBitmap;
    Spinner sp;
    private Uri selectedImage;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Add Medicine");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = (Spinner) findViewById(R.id.medCategory);
        final EditText medicineName = (EditText) findViewById(R.id.medName);
        final EditText medicinePrice = (EditText) findViewById(R.id.medPrice);
        final EditText medicineNotes = (EditText) findViewById(R.id.medNotes);
        Button generate = (Button) findViewById(R.id.generateBtn);
        Button takePict = (Button) findViewById(R.id.btnPict);

        medPict = (ImageView) findViewById(R.id.medicinePict);
        getJSON();

        takePict.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mdCategory = String.valueOf(sp.getSelectedItem());
                mdName = medicineName.getText().toString();
                mdPrice = medicinePrice.getText().toString();
                mdNotes = medicineNotes.getText().toString();

                if (mdName.equals("")) {
                    medicineName.setError("Please Input Medicine Name");
                    medicineName.requestFocus();
                    Toast.makeText(AddNewProductActivity.this, "Please Input Medicine Name", Toast.LENGTH_SHORT).show();
                } else if (mdPrice.equals("")) {
                    medicinePrice.setError("Please Input Price of the Medicine");
                    medicinePrice.requestFocus();
                    Toast.makeText(AddNewProductActivity.this, "Please Input Price of the Medicine", Toast.LENGTH_SHORT).show();
                } else if (mdNotes.equals("")) {
                    medicineNotes.setError("Please Input the Description");
                    medicineNotes.requestFocus();
                    Toast.makeText(AddNewProductActivity.this, "Please Input the Description", Toast.LENGTH_SHORT).show();
                } else if (medPict.getDrawable() == null) {
                    Toast.makeText(AddNewProductActivity.this, "Please Set Image of the Medicine", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        UUID uuid = UUID.randomUUID();
                        id = uuid.toString().replace("-","").toUpperCase();

                        String text = "{ \"id\":\"" + id + "\",\"category\":\"" + mdCategory + "\",\"pict\":\"" + medBitmap + "\",\"name\":\"" + mdName + "\",\"price\":\"" + mdPrice + "\"}";
                        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();

                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        Intent in = new Intent(AddNewProductActivity.this, QRCodeImageAddProductActivity.class);

                        imgStr = getStringImage(bitmap);
                        mdBitmapStr = getStringImage(medBitmap);
                        addMedicine();

//                        in.putExtra("mdPict", medBitmap);
                        in.putExtra("id",id);
                        in.putExtra("bitmap", bitmap);
                        in.putExtra("mdName", mdName);
                        in.putExtra("mdPrice", mdPrice);
                        in.putExtra("mdDesc", mdNotes);
                        in.putExtra("mdCat", mdCategory);
                        startActivity(in);

                    } catch (WriterException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void takePhotoFromCamera() {
        if(isCameraPermissionGranted()){
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, PIC_ID);
        }else{
            Toast.makeText(AddNewProductActivity.this, "Please allow camera permission!", Toast.LENGTH_SHORT).show();
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent,0);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && data != null){
            if(requestCode == 0){
                selectedImage = data.getData();
                try {
                    medBitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                    medPict.setImageBitmap(medBitmap);
                    BitmapHelper.getInstance().setBitmap(medBitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(requestCode == PIC_ID){
                medBitmap = (Bitmap) data.getExtras().get("data");
                medPict.setImageBitmap(medBitmap);
                BitmapHelper.getInstance().setBitmap(medBitmap);
            }
        }
    }

    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void addMedicine() {

        class addMedicine extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddNewProductActivity.this, "Uploading Data...", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(AddNewProductActivity.this, s, Toast.LENGTH_LONG).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("ID",id);
                params.put("CATEGORY", mdCategory);
                params.put("MEDNAME", mdName);
                params.put("PRICE", mdPrice);
                params.put("DESC", mdNotes);
                params.put("QR", imgStr);
                params.put("MEDICINE_PICT", mdBitmapStr);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_INPUT_MED, params);
                return res;
            }
        }

        addMedicine add = new addMedicine();
        add.execute();
    }

    private void getMedicineCategory() {
        JSONObject jsonObject = null;
        List<String> data = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String category = jo.getString("CATEGORY");

                data.add(category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);
        sp.setAdapter(adapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddNewProductActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getMedicineCategory();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_MEDICINE_CATEGORY);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public  boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 1);                return false;
            }
        }
        else {
            return true;
        }
    }
}
