package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.itextpdf.text.Document;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TransactionActivity extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listAll = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listPending = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listPaid = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listConfirmed = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listDone = new ArrayList<>();
    private String JSON_STRING, trxId;
    private Spinner sp;
    private ItemAdapter itemAdapter;
    private RecyclerView rvTransaction;

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction List");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        sp = (Spinner) findViewById(R.id.trxCategory);
        Button btnFilter = findViewById(R.id.btnFilter);
        ImageView imgPrint = findViewById(R.id.img_download);

        itemAdapter = new ItemAdapter(TransactionActivity.this, list);
        itemAdapter.notifyDataSetChanged();
        rvTransaction = findViewById(R.id.medList);
        rvTransaction.setHasFixedSize(true);
        rvTransaction.setLayoutManager(new LinearLayoutManager(TransactionActivity.this));
        rvTransaction.setAdapter(itemAdapter);

        getJSON();
        getListStatus();

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", (String) sp.getSelectedItem());
                if (sp.getSelectedItem().equals("PENDING")) {
                    changeList(listPending);
                } else if (sp.getSelectedItem().equals("PAID")) {
                    changeList(listPaid);
                } else if (sp.getSelectedItem().equals("DONE")) {
                    changeList(listDone);
                } else if (sp.getSelectedItem().equals("CONFIRMED")) {
                    changeList(listConfirmed);
                }
            }
        });

        imgPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap recycler_view_bm = getScreenshotFromRecyclerView(rvTransaction);

                try {
                    String title=null;
                    if ( sp.getSelectedItemPosition()==0){
                        title="Transaction List";
                    }else title = "Transaction List - "+ sp.getSelectedItem();

                    String dest = FileUtils.getAppPath(TransactionActivity.this) + title+".pdf";

                    //File pdfFile= new File(TransactionActivity.this.getFilesDir(), "myfile.pdf");
                    FileOutputStream fOut = new FileOutputStream(dest);

                    PdfDocument document = new PdfDocument();
                    PdfDocument.PageInfo pageInfo = new
                            PdfDocument.PageInfo.Builder(recycler_view_bm.getWidth(), recycler_view_bm.getHeight(), 1).create();
                    PdfDocument.Page page = document.startPage(pageInfo);
                    recycler_view_bm.prepareToDraw();
                    Canvas c;
                    c = page.getCanvas();
                    c.drawBitmap(recycler_view_bm,0,0,null);
                    document.finishPage(page);
                    document.writeTo(fOut);
                    document.close();
                    Toast.makeText(TransactionActivity.this,"PDF generated successfully", Toast.LENGTH_SHORT).show();
                    FileUtils.openFile(TransactionActivity.this, new File(dest));


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void getTrxStatus() {
        JSONObject jsonObject = null;
        List<String> data = new ArrayList<>();
        data.add("--Select Status--");
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String category = jo.getString("STATUS");

                data.add(category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);

        sp.setAdapter(adapter);
    }

    private void getListStatus() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransactionActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getTrxStatus();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_LIST_STATUS);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getListMedicine() {
        JSONObject jsonObject = null;
        Context context = getApplicationContext();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("ID");
                String createdBy = jo.getString("CREATED_BY");
                String createdDate = jo.getString("DATE");
                String totPrice = jo.getString("TOTAL_PRICE");
                String status = jo.getString("STATUS");
                String type = jo.getString("TRX_TYPE");
                String address = jo.getString("ADDRESS");
                String billImg = jo.getString("BILL_IMG");

                HashMap<String, String> listTrx = new HashMap<>();
                listTrx.put("ID", "TRX-" + id);
                listTrx.put("CREATED_BY", createdBy);
                listTrx.put("CREATED_DT", createdDate);
                listTrx.put("STATUS", status);
                listTrx.put("ADDRESS", address);
                listTrx.put("TYPE", type);
                listTrx.put("BILL_IMG", billImg);
                listTrx.put("TOTAL_PRICE", totPrice);

                Log.d("tag", String.valueOf(listTrx));

                listAll.add(listTrx);
                if (status.equals("PENDING")) {
                    listPending.add(listTrx);
                } else if (status.equals("PAID")) {
                    listPaid.add(listTrx);
                } else if (status.equals("DONE")) {
                    listDone.add(listTrx);
                } else if (status.equals("CONFIRMED")) {
                    listConfirmed.add(listTrx);
                }
            }
            changeList(listAll);
            //listAdapter.notifyDataSetChanged();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeList(ArrayList<HashMap<String, String>> newList) {
        Log.d("tag", String.valueOf(newList));
        list.clear();
        list.addAll(newList);
        itemAdapter.notifyDataSetChanged();
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TransactionActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListMedicine();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_LIST_ALL_TRANSACTION_APOTEKER);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
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

    public Bitmap getScreenshotFromRecyclerView(RecyclerView view) {
        RecyclerView.Adapter adapter = view.getAdapter();
        Bitmap bigBitmap = null;
        if (adapter != null) {
            int size = adapter.getItemCount();
            int height = 0;
            Paint paint = new Paint();
            int iHeight = 0;
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

            // Use 1/8th of the available memory for this memory cache.
            final int cacheSize = maxMemory / 8;
            LruCache<String, Bitmap> bitmaCache = new LruCache<>(cacheSize);
            for (int i = 0; i < size; i++) {
                RecyclerView.ViewHolder holder = adapter.createViewHolder(view, adapter.getItemViewType(i));
                adapter.onBindViewHolder(holder, i);
                holder.itemView.measure(View.MeasureSpec.makeMeasureSpec(view.getWidth(), View.MeasureSpec.EXACTLY),
                        View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                holder.itemView.layout(0, 0, holder.itemView.getMeasuredWidth(), holder.itemView.getMeasuredHeight());
                holder.itemView.setDrawingCacheEnabled(true);
                holder.itemView.buildDrawingCache();
                Bitmap drawingCache = holder.itemView.getDrawingCache();
                if (drawingCache != null) {

                    bitmaCache.put(String.valueOf(i), drawingCache);
                }

                height += holder.itemView.getMeasuredHeight();
            }

            bigBitmap = Bitmap.createBitmap(view.getMeasuredWidth(), height, Bitmap.Config.ARGB_8888);
            Canvas bigCanvas = new Canvas(bigBitmap);
            bigCanvas.drawColor(Color.WHITE);

            for (int i = 0; i < size; i++) {
                Bitmap bitmap = bitmaCache.get(String.valueOf(i));
                bigCanvas.drawBitmap(bitmap, 0f, iHeight, paint);
                iHeight += bitmap.getHeight();
                bitmap.recycle();
            }
        }
        return bigBitmap;
    }
}
