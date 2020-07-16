package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class OrderMedicineActivity extends AppCompatActivity {

    private TextView ordAddress, confMedName, ordPrice, ordTotal,ordCat;
    private EditText ordAmount;
    int amount, price, total;
    private String medId, qtDb, orderAmount;
    long delay = 1000; // 1 seconds after user stops typing
    long last_text_edit = 0;
    Handler handler = new Handler();
    Button btnAddCart;
    UserPreference mUserPreference;
    String userName;
    Toolbar toolbar;
    double dbTotal;
    private DecimalFormat df = new DecimalFormat("#,###.##");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_medicine);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Confirmation");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();
        Map<String, String> data = (HashMap<String, String>) in.getSerializableExtra("data");

        ordAddress = (TextView) findViewById(R.id.address);
        confMedName = (TextView) findViewById(R.id.confMedName);
        ordAmount = (EditText) findViewById(R.id.confAmount);
        ordPrice = (TextView) findViewById(R.id.confPrice);
        ordTotal = (TextView) findViewById(R.id.totalPrice);
        ordCat = (TextView) findViewById(R.id.confMedCat);
        btnAddCart = (Button) findViewById(R.id.btnAddCart);

        mUserPreference = new UserPreference(this);
        UserModel userModel = mUserPreference.getUser();
        String userAddress = userModel.getUserAddress();
        userName = userModel.getUserName();

        ordAddress.setText(userAddress);
        ordCat.setText(data.get("DESCRIPTION"));

        qtDb = data.get("QUANTITY");
        medId = data.get("ID");

        Log.d("MedicineIdCheck", "onCreate: " + medId);
        confMedName.setText(data.get("MEDICINE_NAME"));


        price = Integer.parseInt(data.get("PRICE"));
        String medPrice=getString(R.string.rupiah,df.format(price));

        ordPrice.setText(medPrice);
        ordTotal.setText("Rp 0");

        ordAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0){
                    dbTotal = price * Double.parseDouble(s.toString().trim());
                    String medPrice=getString(R.string.rupiah,df.format(dbTotal));
                    ordTotal.setText(medPrice);
                }else{
                    ordTotal.setText("0");
                }

            }
        });

        btnAddCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                orderAmount = ordAmount.getText().toString();
                if(orderAmount.equals("") || orderAmount==null || Integer.parseInt(orderAmount) <=0){
                    Toast.makeText(OrderMedicineActivity.this, "Item should be greater than 0", Toast.LENGTH_SHORT).show();
                }else if (Integer.parseInt(orderAmount) <=Integer.parseInt(qtDb)) {
                    addCart();
                } else{
                    Toast.makeText(OrderMedicineActivity.this, "Item Out of Stock! Available only " + qtDb + " item(s).", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void addCart() {

        class addCart extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(OrderMedicineActivity.this, "Uploading Data...", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(OrderMedicineActivity.this, s, Toast.LENGTH_LONG).show();
                final Intent intent = new Intent(getApplicationContext(), CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("QUANTITY", orderAmount);
                params.put("MED_ID", medId);
                params.put("USER",userName);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_ADD_TO_CART, params);
                return res;
            }
        }

        addCart add = new addCart();
        add.execute();
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
