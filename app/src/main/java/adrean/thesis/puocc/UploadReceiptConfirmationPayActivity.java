package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static java.security.AccessController.getContext;

public class UploadReceiptConfirmationPayActivity extends AppCompatActivity {


    int totalPrice = 0;
    List<String> listCartId = new ArrayList<>();
//    UserPreference mUserPreference;
//    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_receipt_confirmation_pay);

        TextView txt = (TextView) findViewById(R.id.text);
        ListView listItem = (ListView) findViewById(R.id.list);
        Button confirmBtn = (Button) findViewById(R.id.btnConfirm);

        Intent in = getIntent();
        List<Map<String,String>> data = (List<Map<String, String>>) in.getSerializableExtra("data");

//        mUserPreference = new UserPreference(this);
//        userModel = mUserPreference.getUser();

        String totalMedicine = "";

        for(Map<String, String> mapData : data){
            String price = mapData.get("PRICE");
            String med = mapData.get("MED_NAME") + "\n";
            String cartId = mapData.get("CART_ID");

            listCartId.add(cartId);
            Log.d("price", price);
            totalPrice += Integer.parseInt(price);
            totalMedicine += med;
        }

        Log.d("totalPrice", String.valueOf(totalPrice));
        txt.setText("Total Price : "+ String.valueOf(totalPrice));

        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addOrder(listCartId);
            }
        });
    }

    public void addOrder(final List<String> cartID){

        class addOrder extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(UploadReceiptConfirmationPayActivity.this,"Uploading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        Toast.makeText(UploadReceiptConfirmationPayActivity.this,s, Toast.LENGTH_LONG).show();
                    }
                }, 2000);
                final Intent intent = new Intent(UploadReceiptConfirmationPayActivity.this, CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, 2000);
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> params = new HashMap<>();
                String res = "";
                RequestHandler rh = new RequestHandler();
                String totPrice = Integer.toString(totalPrice);
                UUID uuid = UUID.randomUUID();
                String id = uuid.toString().replace("-","").toUpperCase();

                HashMap<String,String> param = new HashMap<>();
                param.put("UUID",id);
//                param.put("USER",userModel.getUserName());
                param.put("TOTAL_PRICE",totPrice);
                res = rh.sendPostRequest(phpConf.URL_ADD_TRANSACTION, param);

                for(String cartData : cartID){
                    params.put("ID",cartData);
//                    params.put("USER",userModel.getUserName());
                    params.put("UUID",id);

                    res = rh.sendPostRequest(phpConf.URL_UPDATE_CART_ORDER, params);
                }
                return res;
            }
        }

        addOrder add = new addOrder();
        add.execute();
    }
}
