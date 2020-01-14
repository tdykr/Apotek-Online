package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SummaryReportActivity extends AppCompatActivity {

    ArrayList<String> reportList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> chartList = new ArrayList<>();
    Spinner spinnerYear;
    ArrayAdapter<String> yearAdapter;
    String JSON_STRING;
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
        spinnerYear = findViewById(R.id.sp_year);
        final Spinner spinnerChart = findViewById(R.id.sp_chart_type);
        Button btnGenerate = findViewById(R.id.btn_generate);
        Button btnDownload = findViewById(R.id.btn_download);

        reportList.add("Report");
        yearList.add("Year");
        chartList.add("Chart");

        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, reportList);
//        yearAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
//                android.R.layout.simple_spinner_item, yearList);
        ArrayAdapter<String> chartAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, chartList);

        spinnerReport.setAdapter(reportAdapter);
//        spinnerYear.setAdapter(yearAdapter);
        spinnerChart.setAdapter(chartAdapter);
        getListYear();
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

    private void getListYear() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryReportActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getYear();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_YEAR_LIST);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getYear() {
        JSONObject jsonObject = null;
        List<String> data = new ArrayList<>();
        data.add("--Select Year--");
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String category = jo.getString("YEAR");

                data.add(category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        yearAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);

        spinnerYear.setAdapter(yearAdapter);
    }
}
