package adrean.thesis.puocc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
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
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import adrean.thesis.puocc.Fragment.DatePickerFragment;

public class SummaryReportActivity extends AppCompatActivity implements DatePickerFragment.DialogDateListener {

    final String START_DATE_TAG = "StartDate";
    final String END_DATE_TAG = "EndDate";

    private ArrayList<String> reportList = new ArrayList<>();
    private ArrayList<String> yearList = new ArrayList<>();
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

    BarChart chart;
    PieChart pieChart;
    ArrayList<BarEntry> NoOfEmp2 = new ArrayList<BarEntry>();
    ArrayList<Entry> NoOfEmp = new ArrayList<Entry>();
    ArrayList<String> year = new ArrayList<String>();
    PieDataSet dataSet = new PieDataSet(NoOfEmp, "Number Of Employees");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_report);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Summary Report");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        spinnerReport = findViewById(R.id.sp_report_selection);
        spinnerChart = findViewById(R.id.sp_chart_type);
        btnGenerate = findViewById(R.id.btn_generate);
        imgDownload = findViewById(R.id.img_download);
        btnStartDate = findViewById(R.id.btn_start_date);
        btnEndDate = findViewById(R.id.btn_end_date);
        tvStartDate = findViewById(R.id.txt_start_date);
        tvEndDate = findViewById(R.id.txt_end_date);
        pieChart = findViewById(R.id.piechart);
        chart = findViewById(R.id.barchart);

        reportList.add("-- Select Report --");
        yearList.add("-- Select Year --");
        chartList.add("-- Select Chart --");

        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, reportList);

        ArrayAdapter<String> chartAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, chartList);

        spinnerReport.setAdapter(reportAdapter);
        spinnerChart.setAdapter(chartAdapter);
        getListYear();
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerChart.getSelectedItemPosition();
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
                if (isStoragePermissionGranted()){
                    Log.d("tag", "create pdf");
                    createPdf(FileUtils.getAppPath(SummaryReportActivity.this) + "icha.pdf");
                }else isStoragePermissionGranted();
            }
        });

        NoOfEmp.add(new Entry(945f, 0));
        NoOfEmp.add(new Entry(1040f, 1));
        NoOfEmp.add(new Entry(1133f, 2));
        NoOfEmp.add(new Entry(1240f, 3));
        NoOfEmp.add(new Entry(1369f, 4));
        NoOfEmp.add(new Entry(1487f, 5));
        NoOfEmp.add(new Entry(1501f, 6));
        NoOfEmp.add(new Entry(1645f, 7));
        NoOfEmp.add(new Entry(1578f, 8));
        NoOfEmp.add(new Entry(1695f, 9));

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        NoOfEmp2.add(new BarEntry(945f, 0));
        NoOfEmp2.add(new BarEntry(1040f, 1));
        NoOfEmp2.add(new BarEntry(1133f, 2));
        NoOfEmp2.add(new BarEntry(1240f, 3));
        NoOfEmp2.add(new BarEntry(1369f, 4));
        NoOfEmp2.add(new BarEntry(1487f, 5));
        NoOfEmp2.add(new BarEntry(1501f, 6));
        NoOfEmp2.add(new BarEntry(1645f, 7));
        NoOfEmp2.add(new BarEntry(1578f, 8));
        NoOfEmp2.add(new BarEntry(1695f, 9));

        year.add("2008");
        year.add("2009");
        year.add("2010");
        year.add("2011");
        year.add("2012");
        year.add("2013");
        year.add("2014");
        year.add("2015");
        year.add("2016");
        year.add("2017");

        createBarChart();
        createPieChart();
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

       /* yearAdapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);

        spinnerYear.setAdapter(yearAdapter);*/
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
                    }*/
                    }
                    if (response.equals("1") && userRole.equals("admin")) {
                        Intent apotekerAct = new Intent(SummaryReportActivity.this, ApotekerMain.class);//
                        startActivity(apotekerAct);

                    } else if (response.equals("1") && userRole.equals("user")) {
                        Intent customerAct = new Intent(SummaryReportActivity.this, CustomerMain.class);
                        startActivity(customerAct);
                    } else if (response.equals("1") && userRole.equals("owner")) {
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
                //params.put("YEAR", year);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_GET_CHART_DATA, params);
                return res;
            }
        }

        SalesReport add = new SalesReport();
        add.execute();
    }

    private void createPieChart(){
        PieData data = new PieData(year, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(5000, 5000);
    }
    private void createBarChart(){
        BarDataSet bardataset = new BarDataSet(NoOfEmp2, "No Of Employee");
        chart.animateY(5000);
        BarData data = new BarData(year, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        chart.setData(data);
    }

    private void printPdf() throws FileNotFoundException, DocumentException {
        //create
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream(new File(SummaryReportActivity.this.getFilesDir(), "myFile.xml")));
        document.open();

        // Document Settings
        document.setPageSize(PageSize.A4);
        document.addCreationDate();
        document.addAuthor("icha");
        document.addCreator("tedy");
    }

    public void createPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("icha");
            document.addCreator("tedy");

            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("res/font/brandon_medium.otf", "UTF-8", BaseFont.EMBEDDED);

            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk("Order Details", mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("Order No:", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font mOrderIdValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderIdValueChunk = new Chunk("#123123", mOrderIdValueFont);
            Paragraph mOrderIdValueParagraph = new Paragraph(mOrderIdValueChunk);
            document.add(mOrderIdValueParagraph);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Horizontal Line...
            document.add(new Chunk(lineSeparator));
            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderDateChunk = new Chunk("Order Date:", mOrderDateFont);
            Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
            document.add(mOrderDateParagraph);

            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk("06/07/2017", mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            // Fields of Order Details...
            Font mOrderAcNameFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderAcNameChunk = new Chunk("Account Name:", mOrderAcNameFont);
            Paragraph mOrderAcNameParagraph = new Paragraph(mOrderAcNameChunk);
            document.add(mOrderAcNameParagraph);

            Font mOrderAcNameValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderAcNameValueChunk = new Chunk("Pratik Butani", mOrderAcNameValueFont);
            Paragraph mOrderAcNameValueParagraph = new Paragraph(mOrderAcNameValueChunk);
            document.add(mOrderAcNameValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            document.add(new Paragraph(""));

            document.close();

            Toast.makeText(SummaryReportActivity.this, "Created... :)", Toast.LENGTH_SHORT).show();

            FileUtils.openFile(SummaryReportActivity.this, new File(dest));

        } catch (IOException | DocumentException ie) {
            Log.d("createPdf: Error " , ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(SummaryReportActivity.this, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onDialogDateSet(String tag, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        if (tag.equals(START_DATE_TAG)) {
            tvStartDate.setText(dateFormat.format(calendar.getTime()));
        } else if (tag.equals(END_DATE_TAG)) {
            tvEndDate.setText(dateFormat.format(calendar.getTime()));
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
}
