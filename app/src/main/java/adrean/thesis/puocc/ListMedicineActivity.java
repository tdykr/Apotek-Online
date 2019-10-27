package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListMedicineActivity extends AppCompatActivity implements ListView.OnItemClickListener{

    private ListView listMed;
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
        Context context = getApplicationContext();
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
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
                Bitmap medImg = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context,medImg);

                HashMap<String,Object> medicine = new HashMap<>();
                medicine.put("ID",id);
                medicine.put("CATEGORY","Category : " + category);
                medicine.put("MEDICINE_NAME",medName);
                medicine.put("PRICE","Rp." + medPrice);
                medicine.put("QUANTITY","Quantity : " + medQuantity);
                medicine.put("MEDICINE_PICT",imgUri);

                Log.d("tag", String.valueOf(medicine));

                list.add(medicine);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                ListMedicineActivity.this, list, R.layout.activity_medlist,
                new String[]{"CATEGORY","MEDICINE_NAME","PRICE","QUANTITY","MEDICINE_PICT"},
                new int[]{R.id.medCategory, R.id.medName, R.id.medPrice, R.id.qt, R.id.img});

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
        HashMap<String,String> map =(HashMap)adapterView.getItemAtPosition(i);
        String medicineId = map.get("ID");
        intent.putExtra("ID",medicineId);
        startActivity(intent);
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

}
