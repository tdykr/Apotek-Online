package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import adrean.thesis.puocc.Fragment.HomeFragment;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class DetailTransactionActivity extends AppCompatActivity {

    private String JSON_STRING,cartId,medName,medCategory,medPrice,medDesc,medQt,trxId,imgStr,billImg,trxDate;
    private Bitmap medPict;
    private ListView listViewCart;
    private ListAdapter adapter;
    private Button uploadBillBtn,submitBillBtn,confirmTrxBtn;
    private ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();
    private UserModel userModel;
    private UserPreference mUserPreference;
    private Uri selectedImage;
    private Bitmap bitmap;
    private ImageView targetImage;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction Detail");

        mUserPreference = new UserPreference(DetailTransactionActivity.this);
        userModel = mUserPreference.getUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uploadBillBtn = (Button) findViewById(R.id.uploadReceiptBtn);
        submitBillBtn = (Button) findViewById(R.id.submitBill);
        confirmTrxBtn = (Button) findViewById(R.id.confirmTrx);
        confirmTrxBtn.setVisibility(INVISIBLE);
        targetImage = (ImageView) findViewById(R.id.imgBill);
        TextView trxIdTv = (TextView) findViewById(R.id.trxId);
        TextView trxDateTv = (TextView) findViewById(R.id.trxDate);

        Intent in = getIntent();
        trxId = in.getStringExtra("TRANS_ID");
        billImg = in.getStringExtra("BILL_IMG");
        trxDate = in.getStringExtra("DATE");

        trxIdTv.setText(trxId);

        if(userModel.getUserRole() == "admin"){
            confirmTrxBtn.setVisibility(VISIBLE);
            submitBillBtn.setVisibility(INVISIBLE);
            uploadBillBtn.setVisibility(INVISIBLE);
        }

        if(trxDate != null && !trxDate.equals("null") &&  !trxDate.isEmpty()){
            trxDateTv.setText(trxDate);
        }

        if(trxId.contains("TRX")){
            trxId = trxId.replace("TRX-","");
        }
        listViewCart = (ListView) findViewById(R.id.detailList);

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();

        getJSON();

        /*homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailTransactionActivity.this,CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });*/

        uploadBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in,0);
            }
        });

        confirmTrxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransactionPaid();
            }
        });

        submitBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetImage.getDrawable() != null){
                    imgStr = getStringImage(bitmap);
                    uploadBillTrx();
                }else{
                    Toast.makeText(DetailTransactionActivity.this, "Please Upload Your Transaction Bill First!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if(billImg != null && !billImg.equals("null") &&  !billImg.isEmpty()){
            submitBillBtn.setVisibility(INVISIBLE);
            uploadBillBtn.setVisibility(INVISIBLE);
            //homeBtn.setVisibility(INVISIBLE);
            Bitmap imgBillBp = encodedStringImage(billImg);
            targetImage.setImageBitmap(imgBillBp);
            targetImage.setVisibility(VISIBLE);
        }else{
            submitBillBtn.setVisibility(VISIBLE);
            uploadBillBtn.setVisibility(VISIBLE);
        }
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (resultCode == RESULT_OK) {
            selectedImage = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                targetImage.setImageBitmap(bitmap);
                BitmapHelper.getInstance().setBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void updateTransactionPaid(){
        class updateTransactionPaid extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Intent in = new Intent(DetailTransactionActivity.this,ApotekerMain.class);
                startActivity(in);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_CART_ORDER_STATUS_CONFIRMED,params);
                return s;
            }
        }
        updateTransactionPaid gj = new updateTransactionPaid();
        gj.execute();
    }

    private void getListMedicine(){
        listViewCart.setAdapter(null);
        JSONObject jsonObject = null;
        Context context = getApplicationContext();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                cartId = jo.getString("CART_ID");
                medName = jo.getString("MED_NAME");
                medCategory = jo.getString("CATEGORY");
                medPrice = jo.getString("PRICE");
                medQt = jo.getString("QUANTITY");
                medDesc = jo.getString("DESCRIPTION");
                medPict = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context,medPict);

                HashMap<String,Object> medicine = new HashMap<>();
                medicine.put("CART_ID",cartId);
                medicine.put("MED_NAME",medName);
                medicine.put("CATEGORY",medCategory);
                medicine.put("PRICE","Rp. " + medPrice);
                medicine.put("DESCRIPTION",medDesc);
                medicine.put("QUANTITY",medQt);
                medicine.put("MEDICINE_PICT",imgUri);
                medicine.put("isChecked","false");

                Log.d("tag", String.valueOf(medicine));

                listData.add(medicine);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(
                getApplicationContext(), listData, R.layout.list_medicine,
                new String[]{"MED_NAME","CATEGORY","MED_NAME","PRICE","QUANTITY","MEDICINE_PICT"},
                new int[]{R.id.rowCheckBox,R.id.medCategory, R.id.medName,R.id.medPrice, R.id.medQuantity, R.id.img});

        listViewCart.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListMedicine();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("USER",userModel.getUserName());
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_GET_LIST_HISTORY_SHOPPING_BY_TRXID,params);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void uploadBillTrx(){
        class uploadBillTrx extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Uploading Image","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                updateQuantity();
                Intent intent = new Intent(DetailTransactionActivity.this, CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("BILL_TRX_IMG",imgStr);
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPLOAD_BILL_TRANSACTION,params);
                return s;
            }
        }
        uploadBillTrx gj = new uploadBillTrx();
        gj.execute();
    }

    private void updateQuantity(){
        class uploadQtTrx extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_QT_AFTER_STATUS_PAID,params);
                return s;
            }
        }
        uploadQtTrx gj = new uploadQtTrx();
        gj.execute();
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
