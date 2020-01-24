package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ConfirmationQrTransactionActivity extends AppCompatActivity {

    int totalPrice = 0;
    ListView medList;
    UserPreference mUserPreferences;
    UserModel userModel;
    ListAdapter adapter;
    String date,trxId,JSON_STRING;
    List<Map<String,String>> data = new ArrayList<>();
    private DecimalFormat df = new DecimalFormat("#,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_qr_transaction);

        String pattern = "dd-MM-yyyy";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        date = simpleDateFormat.format(new Date());

        medList = (ListView) findViewById(R.id.list);
        TextView totalPriceTv = (TextView) findViewById(R.id.totalPrice);
        Button submitBtn = (Button) findViewById(R.id.btnConfirm);

        Intent in = getIntent();
        data = (List<Map<String, String>>) in.getSerializableExtra("data");

        for(Map<String, String> mapData : data){

            String price = mapData.get("PRICE");
            String quantity = mapData.get("QUANTITY");

            totalPrice += Integer.parseInt(price)*Integer.parseInt(quantity);
        }

        String formattedTotalPrice=getString(R.string.rupiah,df.format(totalPrice));
        totalPriceTv.setText(formattedTotalPrice);
        mUserPreferences = new UserPreference(this);
        userModel = mUserPreferences.getUser();

        adapter = new SimpleAdapter(
                getApplicationContext(), data, R.layout.list_conf__qr_trx,
                new String[]{"CATEGORY","MED_NAME","PRICE","QUANTITY"},
                new int[]{R.id.medCategory, R.id.medName,R.id.medPrice,R.id.medQuantity});

        medList.setAdapter(adapter);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrder(data);
            }
        });
    }

    public void addOrder(final List<Map<String,String>> mapMedData){

        class addOrder extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(ConfirmationQrTransactionActivity.this,"Uploading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        Toast.makeText(ConfirmationQrTransactionActivity.this,s, Toast.LENGTH_LONG).show();
                    }
                }, 2000);
                updateQuantity();
                final Intent intent = new Intent(ConfirmationQrTransactionActivity.this, DetailTransactionActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("TRANS_ID",trxId);
                intent.putExtra("DATE",date);
                intent.putExtra("STATUS","DONE");
                intent.putExtra("TYPE","OFFLINE");
                intent.putExtra("TOTAL_PRICE",Integer.toString(totalPrice));
                startActivity(intent);

            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> params = new HashMap<>();
                String res = "";
                RequestHandler rh = new RequestHandler();
                String totPrice = Integer.toString(totalPrice);
                UUID uuid = UUID.randomUUID();
                trxId = uuid.toString().replace("-","").toUpperCase();

                HashMap<String,String> param = new HashMap<>();
                param.put("UUID",trxId);
                param.put("USER",userModel.getUserName());
                param.put("TOTAL_PRICE",totPrice);
                param.put("TYPE","OFFLINE");
                param.put("STATUS","DONE");
                res = rh.sendPostRequest(phpConf.URL_ADD_TRANSACTION, param);

                for(Map<String,String> data : mapMedData){
                    UUID uuidCart = UUID.randomUUID();
                    String cartId = uuidCart.toString().replace("-","").toUpperCase();

                    params.put("ID",cartId);
                    params.put("MED_ID",data.get("ID"));
                    params.put("TRX_ID",trxId);
                    params.put("STATUS","DONE");
                    params.put("QUANTITY",data.get("QUANTITY"));
                    params.put("USER",userModel.getUserName());

                    res = rh.sendPostRequest(phpConf.URL_QR_ADD_CART, params);
                }
                return res;
            }
        }

        addOrder add = new addOrder();
        add.execute();
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
                Toast.makeText(ConfirmationQrTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
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
}
