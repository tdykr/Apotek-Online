package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView listMed;
    private String JSON_STRING, trxId;
    private Spinner sp;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction List");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listMed = (ListView) findViewById(R.id.medList);
        sp = (Spinner) findViewById(R.id.trxCategory);

        getJSON();
        getListStatus();
        listMed.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                    HashMap<String,String> map =(HashMap)adapterView.getItemAtPosition(i);
                    trxId = map.get("ID");
                    Intent intent = new Intent(getApplicationContext(), DetailTransactionActivity.class);
                    intent.putExtra("TRX-ID",trxId);
                    String billImgIntent = map.get("BILL_IMG");
                    String trxDate = map.get("DATE");
                    intent.putExtra("TRANS_ID",trxId);
                    intent.putExtra("DATE",trxDate);
                    if(billImgIntent != null && !billImgIntent.equals("null") && !billImgIntent.isEmpty()) {
                        intent.putExtra("BILL_IMG", billImgIntent);
                    }
                    startActivity(intent);
//                    updateTransactionPaid(trxId);
                }
            });
    }

    private void getTrxStatus() {
        JSONObject jsonObject = null;
        List<String> data = new ArrayList<>();
        data.add("--Select Status--");
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String category = jo.getString("STATUS");

                data.add(category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);

        sp.setAdapter(adapter);
    }

    private void getListStatus() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransactionActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getTrxStatus();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_LIST_STATUS);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getListMedicine(){
        JSONObject jsonObject = null;
        Context context = getApplicationContext();
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("ID");
                String createdBy = jo.getString("CREATED_BY");
                String createdDate = jo.getString("CREATED_DT");
                String status = jo.getString("STATUS");
                String billImg = jo.getString("BILL_IMG");

                HashMap<String,Object> listTrx = new HashMap<>();
                listTrx.put("ID","TRX-"+id);
                listTrx.put("CREATED_BY",createdBy);
                listTrx.put("CREATED_DT",createdDate);
                listTrx.put("STATUS",status);
                listTrx.put("BILL_IMG",billImg);

                Log.d("tag", String.valueOf(listTrx));

                list.add(listTrx);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                TransactionActivity.this, list, R.layout.list_transaction_admin,
                new String[]{"ID","CREATED_BY","CREATED_DT","STATUS"},
                new int[]{R.id.id, R.id.createdBy, R.id.createdDate, R.id.status});

        listMed.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListMedicine();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_LIST_ALL_TRANSACTION_APOTEKER);
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
}
