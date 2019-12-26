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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class DetailTransactionActivity extends AppCompatActivity {

    String JSON_STRING,cartId,medName,medCategory,medPrice,medDesc,medQt,trxId;
    Bitmap medPict;
    ListView listViewCart;
    ListAdapter adapter;
    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        Intent in = getIntent();
        trxId = in.getStringExtra("TRANS_ID");
        trxId = trxId.replace("TRX-","");
        listViewCart = (ListView) findViewById(R.id.detailList);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());

        getJSON();
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
