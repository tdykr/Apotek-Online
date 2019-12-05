package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import adrean.thesis.puocc.Fragment.HomeFragment;

public class OrderMedicineActivity extends AppCompatActivity {

    private TextView ordAddress,ordChangeAddress,confMedName,ordPrice,ordTotal;
    private EditText ordAmount;
    int amount,price,total;
    private String medId, qtDb, orderAmount;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    Button btnAddCart;
    SharedPreferences mPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_medicine);

        ordAddress = (TextView) findViewById(R.id.address);
        ordChangeAddress = (TextView) findViewById(R.id.changeAddress);
        confMedName = (TextView) findViewById(R.id.confMedName);
        ordAmount = (EditText) findViewById(R.id.confAmount);
        ordPrice = (TextView) findViewById(R.id.confPrice);
        ordTotal = (TextView) findViewById(R.id.totalPrice);
        btnAddCart = (Button) findViewById(R.id.btnAddCart);
        orderAmount = ordAmount.getText().toString();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        ordAddress.setText(mPreferences.getString("userAddress",""));

        Intent in = getIntent();
        Map<String,String> data = (HashMap<String, String>) in.getSerializableExtra("data");

        qtDb = data.get("QUANTITY");
        medId = data.get("ID");

        Log.d("MedicineIdCheck", "onCreate: " + medId);
        confMedName.setText(data.get("MEDICINE_NAME"));
        ordPrice.setText(data.get("PRICE"));
        ordTotal.setText(data.get("PRICE"));

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(Integer.parseInt(qtDb) >= Integer.parseInt(orderAmount)){
                    addCart();
                }else{
                    Toast.makeText(OrderMedicineActivity.this, "Item Out of Stock! Available only " + qtDb + " item(s).", Toast.LENGTH_SHORT).show();
                }
            }
        });

        final Runnable input_finish_checker = new Runnable() {
            public void run() {
                if (System.currentTimeMillis() > (last_text_edit + delay - 500)) {
                    // TODO: do what you need here
                    // ............
                    // ............
                    Log.d("total", String.valueOf(total));


                }
            }
        };

    }

    private void addCart(){

        class addCart extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderMedicineActivity.this,"Uploading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(OrderMedicineActivity.this,s,Toast.LENGTH_LONG).show();
                final Intent intent = new Intent(getApplicationContext(), CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("QUANTITY",orderAmount);
                params.put("MED_ID",medId);
                params.put("USER",mPreferences.getString("userName",""));

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_ADD_TO_CART, params);
                return res;
            }
        }

        addCart add = new addCart();
        add.execute();
    }
}
