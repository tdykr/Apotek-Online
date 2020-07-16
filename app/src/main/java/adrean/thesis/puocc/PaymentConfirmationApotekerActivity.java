package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.HashMap;

public class PaymentConfirmationApotekerActivity extends AppCompatActivity {

    private String JSON_STRING,trxId;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_confirmation_apoteker);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction Detail");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        Intent in = getIntent();
        trxId = in.getStringExtra("TRX-ID");
        TextView txt = (TextView) findViewById(R.id.txt);
        txt.setText(trxId);

        Button updateConfirmedTrxBtn = (Button) findViewById(R.id.updateConfirmedTrxBtn);

        updateConfirmedTrxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransactionPaid(trxId);
            }
        });
    }

    private void updateTransactionPaid(final String cartID){
        class updateTransactionPaid extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(PaymentConfirmationApotekerActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Intent in = new Intent(PaymentConfirmationApotekerActivity.this,ApotekerMain.class);
                startActivity(in);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ID",cartID);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_CART_ORDER_STATUS_CONFIRMED,params);
                return s;
            }
        }
        updateTransactionPaid gj = new updateTransactionPaid();
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
