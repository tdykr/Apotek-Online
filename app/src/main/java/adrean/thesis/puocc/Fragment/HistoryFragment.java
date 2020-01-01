package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import adrean.thesis.puocc.DetailTransactionActivity;
import adrean.thesis.puocc.MedicineDetailActivity;
import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import adrean.thesis.puocc.phpConf;

public class HistoryFragment extends Fragment implements ListView.OnItemClickListener {

    String JSON_STRING;
    ListView listViewTrx;
    ListAdapter adapter;
    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();
    SharedPreferences mPreferences;

    public HistoryFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);
        listViewTrx = (ListView) view.findViewById(R.id.cartList);
        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        listViewTrx.setOnItemClickListener(this);
        getJSONTrans();

        return view;
    }

    private void getListTransaction(){
        listViewTrx.setAdapter(null);
        JSONObject jsonObject = null;
        Context context = getContext();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String transId = jo.getString("ID");
                String status = jo.getString("STATUS");
                String createdDt = jo.getString("DATE");
                String amount = jo.getString("TOTAL_PRICE");

                HashMap<String,Object> trx = new HashMap<>();
                trx.put("NO",i+1 + ". ");
                trx.put("TRANS_ID","TRX-" + transId);
                trx.put("STATUS","Transaction Status : " + status);
                trx.put("DATE","Transaction Date : " + createdDt);
                trx.put("AMOUNT","Total Price : " + amount);

                listData.add(trx);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new SimpleAdapter(
                getContext(), listData, R.layout.list_transaction_history,
                new String[]{"NO","TRANS_ID","STATUS","DATE","AMOUNT"},
                new int[]{R.id.no,R.id.transactionId,R.id.trxStatus,R.id.trxDate,R.id.trxAmount});

        listViewTrx.setAdapter(adapter);
    }

    private void getJSONTrans(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(),"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListTransaction();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("USER",mPreferences.getString("userName",""));

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_GET_LIST_TRANSACTION,params);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this.getContext(), DetailTransactionActivity.class);
        HashMap<String,String> map =(HashMap)adapterView.getItemAtPosition(i);
        String trxId = map.get("TRANS_ID");
        intent.putExtra("TRANS_ID",trxId);
        startActivity(intent);
    }
}
