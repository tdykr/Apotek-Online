package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMedicineActivity extends AppCompatActivity implements ListView.OnItemClickListener {

    private ArrayAdapter<String> adapter;
    private RecyclerView listMed;
    private String JSON_STRING;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    Toolbar toolbar;
    ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listAll = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat1 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat2 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat3 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat4 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat5 = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listCat6 = new ArrayList<>();
    List<String> listCategory = new ArrayList<>();
    MedicineAdapter medicineAdapter;
    private Spinner sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_medicine);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medicine List");

        sp = (Spinner) findViewById(R.id.medCategory);
        Button btnFilter = findViewById(R.id.btnFilter);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        listMed = findViewById(R.id.medList);
        getListStatus();
        getJSON();

        medicineAdapter = new MedicineAdapter(ListMedicineActivity.this,list);
        medicineAdapter.notifyDataSetChanged();
        listMed = findViewById(R.id.medList);
        listMed.setHasFixedSize(true);
        listMed.setLayoutManager(new LinearLayoutManager(ListMedicineActivity.this));
        listMed.setAdapter(medicineAdapter);

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("tag", (String) sp.getSelectedItem());
                for(int i = 0; i <= listCategory.size(); i++){
                    if (sp.getSelectedItem().equals(listCategory.get(0))) {
                        changeList(listCat1);
                    } else if (sp.getSelectedItem().equals(listCategory.get(1))) {
                        changeList(listCat2);
                    } else if (sp.getSelectedItem().equals(listCategory.get(2))) {
                        changeList(listCat3);
                    } else if (sp.getSelectedItem().equals(listCategory.get(3))) {
                        changeList(listCat4);
                    } else if (sp.getSelectedItem().equals(listCategory.get(4))) {
                        changeList(listCat5);
                    }
                }
            }
        });
//        listMed.setOnItemClickListener(this);
    }

    private void getListStatus() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ListMedicineActivity.this, "Fetching Data", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getCategory();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_LIST_CATEGORY);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getCategory() {
        JSONObject jsonObject = null;
        List<String> data = new ArrayList<>();
        data.add("--Select Status--");
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String category = jo.getString("CATEGORY");

                data.add(category);
                listCategory.add(category);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adapter = new ArrayAdapter<>(this, R.layout.support_simple_spinner_dropdown_item, data);

        sp.setAdapter(adapter);
    }

    private void getListMedicine() {
        JSONObject jsonObject = null;
        Context context = getApplicationContext();
        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("ID");
                String category = jo.getString("CATEGORY");
                String medName = jo.getString("MEDICINE_NAME");
                String medPrice = jo.getString("PRICE");
                double dbMedPrice = Double.parseDouble(medPrice);
                medPrice=getString(R.string.rupiah,df.format(dbMedPrice));
                String medQuantity = jo.getString("QUANTITY");
                Bitmap medImg = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context, medImg);

                HashMap<String, String> medicine = new HashMap<>();
                medicine.put("ID", id);
                medicine.put("CATEGORY", category);
                medicine.put("MEDICINE_NAME", medName);
                medicine.put("PRICE", medPrice);
                medicine.put("QUANTITY", medQuantity + " Pcs");
                medicine.put("MEDICINE_PICT", jo.getString("MEDICINE_PICT"));

                Log.d("tag", String.valueOf(medicine));

//                list.add(medicine);
                listAll.add(medicine);
//                if(listCategory.size() != 0){
                    for(int j = 0; j<= listCategory.size();j++){
                         if(category.equals(listCategory.get(0))){
                            listCat1.add(medicine);
                        }else if(category.equals(listCategory.get(1))){
                            listCat2.add(medicine);
                        }else if(category.equals(listCategory.get(2))){
                            listCat3.add(medicine);
                        }else if(category.equals(listCategory.get(3))){
                            listCat4.add(medicine);
                        }else if(category.equals(listCategory.get(4))){
                            listCat5.add(medicine);
                        }
                    }
                }
//                if (category.equals("Antibiotik")) {
//                    listCat1.add(medicine);
//                } else if (category.equals("Obat Sakit Kepala")) {
//                    listCat2.add(medicine);
//                } else if (category.equals("Obat Batuk")) {
//                    listCat3.add(medicine);
//                } else if (category.equals("Obat Tenggorokan")) {
//                    listCat4.add(medicine);
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        changeList(listAll);

//        ListAdapter adapter = new SimpleAdapter(
//                ListMedicineActivity.this, list, R.layout.list_medicine,
//                new String[]{"CATEGORY", "MEDICINE_NAME", "PRICE", "QUANTITY", "MEDICINE_PICT"},
//                new int[]{R.id.medCategory, R.id.medName, R.id.medPrice, R.id.medQuantity, R.id.img});
//
//        listMed.setAdapter(adapter);
    }

    private void changeList(ArrayList<HashMap<String, String>> newList) {
        Log.d("tag", String.valueOf(newList));
        list.clear();
        list.addAll(newList);
        medicineAdapter.notifyDataSetChanged();
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ListMedicineActivity.this, "Fetching Data", "Please Wait...", false, false);
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
                String s = rh.sendGetRequest(phpConf.URL_GET_ALL_MEDICINE_LIST);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, MedicineDetailActivity.class);
        HashMap<String, String> map = (HashMap) adapterView.getItemAtPosition(i);
        String medicineId = map.get("ID");
        intent.putExtra("ID", medicineId);
        startActivity(intent);
    }

    public Bitmap encodedStringImage(String imgString) {
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
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
