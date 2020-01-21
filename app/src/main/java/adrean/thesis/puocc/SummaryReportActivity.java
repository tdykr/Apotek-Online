package adrean.thesis.puocc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import adrean.thesis.puocc.Fragment.DatePickerFragment;

public class SummaryReportActivity extends AppCompatActivity implements DatePickerFragment.DialogDateListener {

    final String START_DATE_TAG = "StartDate";
    final String END_DATE_TAG = "EndDate";

    private ArrayList<String> reportList = new ArrayList<>();

    private ArrayList<String> chartList = new ArrayList<>();

    private Spinner spinnerReport;
    private Spinner spinnerChart;
    private ImageButton btnStartDate;
    private ImageButton btnEndDate;
    private TextView tvStartDate;
    private TextView tvEndDate;
    private ImageView imgDownload;
    private Button btnGenerate;
    private String JSON_STRING;
    private Toolbar toolbar;
    private LinearLayout llGross, llSales;

    private TextView tvTotalTransaction, tvIncome, tvTotalBought, tvLeastBought, tvLeastBoughtCat;

    private String startDate, endDate;
    private DecimalFormat df = new DecimalFormat("#,###.##");

    BarChart barChart;
    PieChart pieChart;

    ArrayList<BarEntry> listTotalBar = new ArrayList<BarEntry>();
    ArrayList<Entry> listTotalPie = new ArrayList<Entry>();
    ArrayList<String> listTransaction = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Sales Report");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        llGross = findViewById(R.id.ll_gross);
        llSales = findViewById(R.id.ll_sales);
        spinnerReport = findViewById(R.id.sp_report_selection);
        spinnerChart = findViewById(R.id.sp_chart_type);
        btnGenerate = findViewById(R.id.btn_generate);
        imgDownload = findViewById(R.id.img_download);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);
        tvStartDate = findViewById(R.id.txt_start_date);
        tvEndDate = findViewById(R.id.txt_end_date);
        pieChart = findViewById(R.id.piechart);
        barChart = findViewById(R.id.barchart);
        tvTotalTransaction = findViewById(R.id.txt_total_finished_transaction);
        tvIncome = findViewById(R.id.txt_gross_income);
        tvTotalBought = findViewById(R.id.txt_total_quantity_sold_medicine);
        tvLeastBought = findViewById(R.id.txt_least_bought_medicine);
        tvLeastBoughtCat = findViewById(R.id.txt_least_bought_medicine_cateogry);

        llSales.setVisibility(View.GONE);
        llGross.setVisibility(View.GONE);

        reportList.add("-- Select Report --");
        reportList.add("Sales Report");
        reportList.add("Gross Income Report");
        reportList.add("Medicine Sales Report");
        reportList.add("Pharmacist Report");
        chartList.add("-- Select Chart --");
        chartList.add("Bar Chart");
        chartList.add("Pie Chart");

        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, reportList);

        ArrayAdapter<String> chartAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, chartList);

        spinnerReport.setAdapter(reportAdapter);
        spinnerChart.setAdapter(chartAdapter);

        spinnerReport.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position==2 || position==0){
                    spinnerChart.setVisibility(View.GONE);
                }else {
                    spinnerChart.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //getListYear();
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check spinner, start date, end date
                if (spinnerReport.getSelectedItemPosition() == 1) {
                    if (checkDate() && setChartVisibility()) {
                        sales();
                        imgDownload.setVisibility(View.VISIBLE);
                    }
                } else if (spinnerReport.getSelectedItemPosition() == 2) {
                    if (checkDate()) {
                        gross();
                        imgDownload.setVisibility(View.VISIBLE);
                    }
                } else if (spinnerReport.getSelectedItemPosition() == 3) {
                    if (checkDate() && setChartVisibility()) {
                        medSales();
                        imgDownload.setVisibility(View.VISIBLE);
                    }
                } else if (spinnerReport.getSelectedItemPosition() == 4) {
                    if (checkDate() && setChartVisibility()) {
                        pharmacist();
                        imgDownload.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(SummaryReportActivity.this, "Please choose report to be generated!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), START_DATE_TAG);
            }
        });

        btnEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerFragment datePickerFragment = new DatePickerFragment();
                datePickerFragment.show(getSupportFragmentManager(), END_DATE_TAG);
            }
        });

        imgDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStoragePermissionGranted()) {
                    Log.d("tag", "create pdf");
                    //createPdfOld(FileUtils.getAppPath(SummaryReportActivity.this) + "icha.pdf");
                    String title = spinnerReport.getSelectedItem() + " "+ spinnerChart.getSelectedItem() + System.currentTimeMillis();
                    convertView(FileUtils.getAppPath(SummaryReportActivity.this) + title +".pdf");

                } else isStoragePermissionGranted();
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

    private boolean setChartVisibility() {
        if (spinnerChart.getSelectedItemPosition() == 1) {
            barChart.setVisibility(View.VISIBLE);
            pieChart.setVisibility(View.GONE);
            return true;
        } else if (spinnerChart.getSelectedItemPosition() == 2) {
            pieChart.setVisibility(View.VISIBLE);
            barChart.setVisibility(View.GONE);
            return true;
        } else {
            Toast.makeText(SummaryReportActivity.this, "Please choose chart type!", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    private boolean checkDate() {
        if (startDate == null || startDate.isEmpty()) {
            Toast.makeText(SummaryReportActivity.this, "Please input the start date!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (endDate == null || endDate.isEmpty()) {
            Toast.makeText(SummaryReportActivity.this, "Please input the end date!", Toast.LENGTH_SHORT).show();
            return false;
        } else return true;
    }

    private void sales() {
        class Sales extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryReportActivity.this, "Loading Data...",
                        "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    listTransaction.clear();
                    listTotalPie.clear();
                    listTotalBar.clear();
                    Log.d("Json Sales", s);
                    JSONObject jsonPost = new JSONObject(s);
                    JSONArray cast = jsonPost.getJSONArray("result");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject c = cast.getJSONObject(i);

                        listTotalPie.add(new Entry(Float.parseFloat(c.getString("TOTAL")), i));
                        listTransaction.add(c.getString("TRX_TYPE"));
                        listTotalBar.add(new BarEntry(Float.parseFloat(c.getString("TOTAL")), i));
                    }

                    llSales.setVisibility(View.VISIBLE);
                    llGross.setVisibility(View.GONE);
                    createBarChart("Sales Comparison");
                    createPieChart("Sales Comparison");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("STARTDATE", startDate);
                params.put("ENDDATE", endDate);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_GET_CHART_DATA, params);
                return res;
            }
        }
        Sales add = new Sales();
        add.execute();
    }

    private void medSales() {
        class MedSales extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryReportActivity.this, "Loading Data...",
                        "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    listTransaction.clear();
                    listTotalPie.clear();
                    listTotalBar.clear();
                    Log.d("Json MedSales", s);
                    JSONObject jsonPost = new JSONObject(s);
                    JSONArray cast = jsonPost.getJSONArray("result");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject c = cast.getJSONObject(i);

                        listTotalPie.add(new Entry(Float.parseFloat(c.getString("TOTAL")), i));
                        listTransaction.add(c.getString("MEDICINE_NAME"));
                        listTotalBar.add(new BarEntry(Float.parseFloat(c.getString("TOTAL")), i));
                    }

                    llSales.setVisibility(View.VISIBLE);
                    llGross.setVisibility(View.GONE);
                    createBarChart("Medical Sales Report");
                    createPieChart("Medical Sales Report");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("STARTDATE", startDate);
                params.put("ENDDATE", endDate);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_SELLING_REPORT_MEDICINE, params);
                return res;
            }
        }
        MedSales add = new MedSales();
        add.execute();
    }

    private void pharmacist() {
        class Pharmacist extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SummaryReportActivity.this, "Loading Data...",
                        "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                try {
                    listTransaction.clear();
                    listTotalPie.clear();
                    listTotalBar.clear();
                    Log.d("Json Pharmacist", s);
                    JSONObject jsonPost = new JSONObject(s);
                    JSONArray cast = jsonPost.getJSONArray("result");
                    for (int i = 0; i < cast.length(); i++) {
                        JSONObject c = cast.getJSONObject(i);

                        listTotalPie.add(new Entry(Float.parseFloat(c.getString("TOTAL")), i));
                        listTransaction.add(c.getString("USER"));
                        listTotalBar.add(new BarEntry(Float.parseFloat(c.getString("TOTAL")), i));
                    }

                    llSales.setVisibility(View.VISIBLE);
                    llGross.setVisibility(View.GONE);
                    createBarChart("Pharmacist Sales Report");
                    createPieChart("Pharmacist Sales Report");

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("STARTDATE", startDate);
                params.put("ENDDATE", endDate);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_SELLING_REPORT_PHARMACIST, params);
                return res;
            }
        }
        Pharmacist add = new Pharmacist();
        add.execute();
    }

    private void gross() {

        class Gross extends AsyncTask<Void, Void, String> {

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
                    Log.d("Json Gross", s);
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String transactionTotal = jo.getString("TOTAL_TRANSACTION");
                    String income = jo.getString("INCOME");
                    String totalBought = jo.getString("TOTAL_BOUGHT");
                    String leastBought = jo.getString("LEAST_BOUGHT");
                    String leastBoughtCat = jo.getString("LEAST_BOUGHT_CAT");

                    double dbIncome = Double.parseDouble(income);
                    income = getString(R.string.rupiah, df.format(dbIncome));

                    tvTotalTransaction.setText(transactionTotal);
                    tvIncome.setText(income);
                    tvTotalBought.setText(totalBought);
                    tvLeastBought.setText(leastBought);
                    tvLeastBoughtCat.setText(leastBoughtCat);

                    llSales.setVisibility(View.GONE);
                    llGross.setVisibility(View.VISIBLE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("STARTDATE", startDate);
                params.put("ENDDATE", endDate);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_SUMMARY, params);
                return res;
            }
        }

        Gross add = new Gross();
        add.execute();
    }

    private void createPieChart(String title) {
        PieDataSet dataSet = new PieDataSet(listTotalPie, title);
        PieData data = new PieData(listTransaction, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.setContentDescription(null);
        pieChart.setDescription(null);
        pieChart.animateXY(3000, 3000);
    }

    private void createBarChart(String title) {
        BarDataSet bardataset = new BarDataSet(listTotalBar, title);
        barChart.animateY(3000);
        BarData data = new BarData(listTransaction, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(data);
        barChart.setDescription(null);
    }

    private void convertView(String dest) {
        if (new File(dest).exists()) {
            new File(dest).delete();
        }
        if (spinnerReport.getSelectedItemPosition()==2){
            convertCertViewToImage(dest);
        }else{
            if (barChart.getVisibility() == View.VISIBLE) {
                barChart.buildDrawingCache();
                Bitmap bm = barChart.getDrawingCache();
                createPdf(bm, dest);
            }
            if(pieChart.getVisibility() == View.VISIBLE){
                pieChart.buildDrawingCache();
                Bitmap bm = pieChart.getDrawingCache();
                createPdf(bm, dest);
            }
        }
    }

    public void convertCertViewToImage(String dest) {

        try {
            FileOutputStream fOut = new FileOutputStream(dest);

            PdfDocument document = new PdfDocument();
            LinearLayout view = findViewById(R.id.ll_gross);
            Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            view.draw(canvas);
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(bm.getWidth(), bm.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            bm.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(bm,0,0,null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();


            Toast.makeText(SummaryReportActivity.this,"PDF generated successfully", Toast.LENGTH_SHORT).show();
            FileUtils.openFile(SummaryReportActivity.this, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createPdf(Bitmap bm, String dest){
        try {
            FileOutputStream fOut = new FileOutputStream(dest);

            PdfDocument document = new PdfDocument();
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(bm.getWidth(), bm.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            bm.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(bm,0,0,null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            Toast.makeText(SummaryReportActivity.this,"PDF generated successfully", Toast.LENGTH_SHORT).show();
            FileUtils.openFile(SummaryReportActivity.this, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDialogDateSet(String tag, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (tag.equals(START_DATE_TAG)) {
            tvStartDate.setText(dateFormat.format(calendar.getTime()));
            startDate = dateFormat.format(calendar.getTime());
        } else if (tag.equals(END_DATE_TAG)) {
            tvEndDate.setText(dateFormat.format(calendar.getTime()));
            endDate = dateFormat.format(calendar.getTime());
        }
    }

    public boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        } else {
            return true;
        }
    }
}
