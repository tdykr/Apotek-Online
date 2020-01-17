package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    ArrayList<HashMap<String,String>> list = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listAll = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listPending = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listPaid = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listInCart = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listConfirmed = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listDone = new ArrayList<>();
    private ListView listMed;
    private String JSON_STRING, trxId;
    private Spinner sp;
    CustomAdapter listAdapter;

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
        Button btnFilter = findViewById(R.id.btnFilter);

        listAdapter = new CustomAdapter(this,list, 1);
        listMed.setAdapter(listAdapter);

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
                intent.putExtra("STATUS",map.get("STATUS"));
                intent.putExtra("TOTAL_PRICE",map.get("TOTAL_PRICE"));
                if(billImgIntent != null && !billImgIntent.equals("null") && !billImgIntent.isEmpty()) {
                    intent.putExtra("BILL_IMG", billImgIntent);
                }
                startActivity(intent);
//                    updateTransactionPaid(trxId);
            }
        });

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", (String) sp.getSelectedItem());
                if(sp.getSelectedItem().equals("INCART")){
                    changeList(listInCart);
                }else if(sp.getSelectedItem().equals("PENDING")){
                    changeList(listPending);
                }else if(sp.getSelectedItem().equals("PAID")){
                    changeList(listPaid);
                }else if(sp.getSelectedItem().equals("DONE")){
                    changeList(listDone);
                }else if(sp.getSelectedItem().equals("CONFIRMED")){
                    changeList(listConfirmed);
                }
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
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("ID");
                String createdBy = jo.getString("CREATED_BY");
                String createdDate = jo.getString("CREATED_DT");
                String totPrice = jo.getString("TOTAL_PRICE");
                String status = jo.getString("STATUS");
                String billImg = jo.getString("BILL_IMG");

                HashMap<String,String> listTrx = new HashMap<>();
                listTrx.put("ID","TRX-"+id);
                listTrx.put("CREATED_BY",createdBy);
                listTrx.put("CREATED_DT",createdDate);
                listTrx.put("STATUS",status);
                listTrx.put("BILL_IMG",billImg);
                listTrx.put("TOTAL_PRICE",totPrice);

                Log.d("tag", String.valueOf(listTrx));

                list.add(listTrx);
                listAll.add(listTrx);
                if(status.equals("INCART")){
                    listInCart.add(listTrx);
                }else if(status.equals("PENDING")){
                    listPending.add(listTrx);
                }else if(status.equals("PAID")){
                    listPaid.add(listTrx);
                }else if(status.equals("DONE")){
                    listDone.add(listTrx);
                }else if(status.equals("CONFIRMED")){
                    listConfirmed.add(listTrx);
                }
            }
            listAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeList(ArrayList<HashMap<String, String>> newList){
        Log.d("tag", String.valueOf(newList));
/*        listAdapter.updateReceiptsList(newList);*/
        list.clear();
        list.addAll(newList);
        Log.d("tag", "d");
        listAdapter.notifyDataSetChanged();
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
