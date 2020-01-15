package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class SummaryReportActivity extends AppCompatActivity {

    ArrayList<String> reportList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> chartList = new ArrayList<>();
    String year;

    Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Summary Report");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Spinner spinnerReport = findViewById(R.id.sp_report_selection);
        Spinner spinnerYear = findViewById(R.id.sp_year);
        final Spinner spinnerChart = findViewById(R.id.sp_chart_type);
        Button btnGenerate = findViewById(R.id.btn_generate);
        Button btnDownload = findViewById(R.id.btn_download);

        reportList.add("Report");
        yearList.add("Year");
        chartList.add("Chart");

        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, reportList);
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, yearList);
        ArrayAdapter<String> chartAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, chartList);

        spinnerReport.setAdapter(reportAdapter);
        spinnerYear.setAdapter(yearAdapter);
        spinnerChart.setAdapter(chartAdapter);

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerChart.getSelectedItemPosition() ;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void SalesReport() {

        class SalesReport extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryReportActivity.this, "Loading Data...", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                try {
                    Log.d("Json Login", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String userId = jo.getString("ID");
                    String userName = jo.getString("USERNAME");
                    String userEmail = jo.getString("EMAIL");
                    String userAddress = jo.getString("ADDRESS");
                    String userPhone = jo.getString("PHONE");
                    String userRole = jo.getString("ROLE");
                    String response = jo.getString("response");
                    String message = jo.getString("message");

                    if (response.equals("1")) {
                       /* saveUser(userId, userPass, userName, userEmail, userAddress, userPhone,
                                userRole, response, message);
                    }*/}
                    if (response.equals("1") && userRole.equals("admin")) {
                        Intent apotekerAct = new Intent(SummaryReportActivity.this, ApotekerMain.class);//
                        startActivity(apotekerAct);

                    } else if (response.equals("1") && userRole.equals("user")) {
                        Intent customerAct = new Intent(SummaryReportActivity.this, CustomerMain.class);
                        startActivity(customerAct);
                    }else if(response.equals("1") && userRole.equals("owner")){
                        Intent ownerAct = new Intent(SummaryReportActivity.this, OwnerMain.class);
                        startActivity(ownerAct);
                    }
                    Toast.makeText(SummaryReportActivity.this, message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("YEAR", year);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_GET_CHART_DATA, params);
                return res;
            }
        }

        SalesReport add = new SalesReport();
        add.execute();
    }
}
