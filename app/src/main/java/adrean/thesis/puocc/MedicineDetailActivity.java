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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static java.lang.Integer.parseInt;

public class MedicineDetailActivity extends AppCompatActivity {

    private String id,medicineName,medicinePrice,medicineCategory,medicineQt,medicineDesc;
    //192.168.43.106
    //192.168.1.6
    final String URL_GET_MEDICINE_DETAIL = "http://192.168.1.6/apotek/getMedicineDetail.php";
    TextView medName,medCategory,medPrice,medDesc;
    EditText qtVal;
    ImageView qrCode,qtMin,qtAdd;
    Integer tempQt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine_detail);

        qtMin = (ImageView) findViewById(R.id.qtMin);
        qtAdd = (ImageView) findViewById(R.id.qtAdd);
        qtVal = (EditText) findViewById(R.id.qtVal);
        medName = (TextView) findViewById(R.id.medName);
        medCategory = (TextView) findViewById(R.id.medCategory);
        medPrice = (TextView) findViewById(R.id.medPrice);
        medDesc = (TextView) findViewById(R.id.medDesc);
        qrCode = (ImageView) findViewById(R.id.qrImg);

        Intent intent  = getIntent();
        id = intent.getStringExtra("ID");

        getMedicine();

        qtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tempQt = tempQt+1;
                qtVal.setText(Integer.toString(tempQt));
            }
        });

        qtMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tempQt = tempQt-1;
                qtVal.setText(Integer.toString(tempQt));
            }
        });
    }

    private void getMedicine(){
        class GetEmployee extends AsyncTask<Void,Void,String> {
            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(MedicineDetailActivity.this,"Fetching...","Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                showDetail(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String,String> data = new HashMap<>();
                data.put("ID",id);
                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(URL_GET_MEDICINE_DETAIL,data);
                return s;
            }
        }
        GetEmployee ge = new GetEmployee();
        ge.execute();
    }

    private void showDetail(String json){
        try {
            JSONObject jsonObject = new JSONObject(json);

            JSONArray result = jsonObject.getJSONArray("result");
            JSONObject c = result.getJSONObject(0);
            medicineName = c.getString("MEDICINE_NAME");
            medicineCategory = c.getString("CATEGORY");
            medicinePrice = c.getString("PRICE");
            medicineQt= c.getString("QUANTITY");
            medicineDesc = c.getString("DESCRIPTION");

            String encodedString = c.getString("QR_CODE");
            tempQt = parseInt(medicineQt);

            medName.setText(medicineName);
            medCategory.setText(medicineCategory);
            medPrice.setText(medicinePrice);
            medDesc.setText(medicineDesc);
            qtVal.setText(medicineQt);

            qrCode.setImageBitmap(encodedStringImage(encodedString));

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }
}
