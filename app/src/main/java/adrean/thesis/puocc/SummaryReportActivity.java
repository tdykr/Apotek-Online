package adrean.thesis.puocc;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
        chartList.add("-- Select Chart --");
        chartList.add("Bar Chart");
        chartList.add("Pie Chart");

        ArrayAdapter<String> reportAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, reportList);

        ArrayAdapter<String> chartAdapter = new ArrayAdapter<>(SummaryReportActivity.this,
                android.R.layout.simple_spinner_item, chartList);

        spinnerReport.setAdapter(reportAdapter);
        spinnerChart.setAdapter(chartAdapter);
        //getListYear();
        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check spinner, start date, end date
                if (spinnerReport.getSelectedItemPosition()==1){
                    if (checkDate() || spinnerChart.getSelectedItemPosition()!=0) {
                        sales();
                        if(spinnerChart.getSelectedItemPosition()==1){
                            barChart.setVisibility(View.VISIBLE);
                            pieChart.setVisibility(View.GONE);
                        }else{
                            pieChart.setVisibility(View.VISIBLE);
                            barChart.setVisibility(View.GONE);
                        }
                    }else if (spinnerChart.getSelectedItemPosition()==0){
                        Toast.makeText(SummaryReportActivity.this, "Please choose chart type!", Toast.LENGTH_SHORT).show();
                    }
                }else if(spinnerReport.getSelectedItemPosition()==2){
                    if (checkDate()) gross();
                }else{
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
                if (isStoragePermissionGranted()){
                    Log.d("tag", "create pdf");
                    createPdf(FileUtils.getAppPath(SummaryReportActivity.this) + "icha.pdf");
                }else isStoragePermissionGranted();
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

    private boolean checkDate(){
        if (startDate==null || startDate.isEmpty()){
            Toast.makeText(SummaryReportActivity.this, "Please input the start date!", Toast.LENGTH_SHORT).show();
            return false;
        }else if (endDate==null || endDate.isEmpty()){
            Toast.makeText(SummaryReportActivity.this, "Please input the end date!", Toast.LENGTH_SHORT).show();
            return false;
        }else return true;
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
                    createBarChart();
                    createPieChart();

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
                String res = rh.sendPostRequest(phpConf.URL_GET_CHART_DATA,params);
                return res;
            }
        }
        Sales add = new Sales();
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
                    income=getString(R.string.rupiah,df.format(dbIncome));

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
                String res = rh.sendPostRequest(phpConf.URL_SUMMARY,params);
                return res;
            }
        }

        Gross add = new Gross();
        add.execute();
    }

    private void createPieChart(){
        PieDataSet dataSet = new PieDataSet(listTotalPie, "Sales Comparison");
        PieData data = new PieData(listTransaction, dataSet);
        pieChart.setData(data);
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieChart.animateXY(3000, 3000);
    }
    private void createBarChart(){
        BarDataSet bardataset = new BarDataSet(listTotalBar, "Sales Comparison");
        barChart.animateY(3000);
        BarData data = new BarData(listTransaction, bardataset);
        bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
        barChart.setData(data);
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

            Toast.makeText(SummaryReportActivity.this, "PDF is created... :)", Toast.LENGTH_SHORT).show();

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
            startDate=dateFormat.format(calendar.getTime());
        } else if (tag.equals(END_DATE_TAG)) {
            tvEndDate.setText(dateFormat.format(calendar.getTime()));
            endDate=dateFormat.format(calendar.getTime());
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
