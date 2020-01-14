package adrean.thesis.puocc;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.ArrayList;

public class SummaryReportActivity extends AppCompatActivity {

    ArrayList<String> reportList = new ArrayList<>();
    ArrayList<String> yearList = new ArrayList<>();
    ArrayList<String> chartList = new ArrayList<>();

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
}
