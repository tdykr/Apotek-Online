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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

import adrean.thesis.puocc.Fragment.HomeFragment;

public class DetailTransactionActivity extends AppCompatActivity {

    private String JSON_STRING,cartId,medName,medCategory,medPrice,medDesc,medQt,trxId,imgStr;
    private Bitmap medPict;
    private ListView listViewCart;
    private ListAdapter adapter;
    private Button uploadBillBtn,submitBillBtn;
    private ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();
    private SharedPreferences mPreferences;
    private Uri selectedImage;
    private Bitmap bitmap;
    private ImageView targetImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        uploadBillBtn = (Button) findViewById(R.id.uploadReceiptBtn);
        submitBillBtn = (Button) findViewById(R.id.submitBill);
        targetImage = (ImageView) findViewById(R.id.imgBill);
        Intent in = getIntent();
        trxId = in.getStringExtra("TRANS_ID");
        trxId = trxId.replace("TRX-","");
        listViewCart = (ListView) findViewById(R.id.detailList);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        getJSON();

        uploadBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in,0);
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
                getApplicationContext(), listData, R.layout.list_history_item_customer,
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
                params.put("USER",mPreferences.getString("userName",""));
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
                Intent intent = new Intent(DetailTransactionActivity.this, CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
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
}