package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ListMedicineActivity extends AppCompatActivity implements ListView.OnItemClickListener{
    //192.168.43.106
    //192.168.1.6
    private ListView listMed;
    final String URL_GET_ALL_MEDICINE_LIST = "http://192.168.1.6/apotek/getListMedicine.php";
    private String JSON_STRING;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_medicine);

        listMed = (ListView) findViewById(R.id.medList);
        getJSON();
        listMed.setOnItemClickListener(this);
    }

    private void getListMedicine(){
        JSONObject jsonObject = null;
        ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String, String>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String id = jo.getString("ID");
                String category = jo.getString("CATEGORY");
                String medName = jo.getString("MEDICINE_NAME");
                String medPrice = jo.getString("PRICE");
                String medQuantity = jo.getString("QUANTITY");

                HashMap<String,String> medicine = new HashMap<>();
                medicine.put("ID",id);
                medicine.put("CATEGORY","Category : " + category);
                medicine.put("MEDICINE_NAME",medName);
                medicine.put("PRICE","Rp." + medPrice);
                medicine.put("QUANTITY","Quantity : " + medQuantity);

                list.add(medicine);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ListMedicineActivity.this, list, R.layout.activity_medlist,
                new String[]{"CATEGORY","MEDICINE_NAME","PRICE","QUANTITY"},
                new int[]{R.id.medCategory, R.id.medName, R.id.medPrice, R.id.qt});

        listMed.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ListMedicineActivity.this,"Fetching Data","Please Wait...",false,false);
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
                String s = rh.sendGetRequest(URL_GET_ALL_MEDICINE_LIST);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        Intent intent = new Intent(this, MedicineDetailActivity.class);
        HashMap<String,String> map =(HashMap)adapterView.getItemAtPosition(i);
        String medicineId = map.get("ID");
        intent.putExtra("ID",medicineId);
        startActivity(intent);
    }
}
