package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adrean.thesis.puocc.CategoryDetailActivity;
import adrean.thesis.puocc.CustomerMain;
import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import adrean.thesis.puocc.UploadReceiptConfirmationPayActivity;
import adrean.thesis.puocc.phpConf;

public class CartFragment extends Fragment{

    public CartFragment(){

    }

    SharedPreferences mPreferences;
    TextView id, medNameTv;
    String JSON_STRING,cartId,medName,medCategory,medPrice,medDesc,medQt,check;
    Bitmap medPict;
    Button btnSubmitCart;
    ListView listViewCart;
    CheckBox checkBox;
    ListAdapter adapter;
    ArrayList<HashMap<String,Object>> listData = new ArrayList<HashMap<String, Object>>();
    View view1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        view1 = inflater.inflate(R.layout.list_cart,container,false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        listViewCart = (ListView) view.findViewById(R.id.cartList);
        id = (TextView) view.findViewById(R.id.medCategory);
        btnSubmitCart = (Button) view.findViewById(R.id.btnSubmitCart);
        medNameTv = (TextView) view.findViewById(R.id.medName);

        checkBox = (CheckBox) view1.findViewById(R.id.rowCheckBox);
        getJSON();

        listViewCart.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, final View view, final int i, long l) {

                final HashMap<String,Object> data = (HashMap<String, Object>) adapterView.getItemAtPosition(i);
                check = (String) data.get("isChecked");
                if(check.equals("false")){
                    CheckBox check1 = view.findViewById(R.id.rowCheckBox);
                    check1.setChecked(true);
                    data.put("isChecked","true");
                    listData.set(i,data);
                }else if(check.equals("true")){
                    CheckBox check1 = view.findViewById(R.id.rowCheckBox);
                    check1.setChecked(false);
                    data.put("isChecked","false");
                    listData.set(i,data);
                }
            }
        });

        btnSubmitCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                List<String> currCartID = new ArrayList<>();
                List<Map<String,Object>> tempData = new ArrayList<>();

                for(HashMap<String,Object> mapData : listData){
                    if(mapData.get("isChecked").equals("true")){
                        tempData.add(mapData);
                        Log.d("CheckDataChecked", "" + mapData.get("CART_ID"));
                        String tempCurrCartID = (String) mapData.get("CART_ID");
                        currCartID.add(tempCurrCartID);
                    }
                }
                Intent in = new Intent(getContext(), UploadReceiptConfirmationPayActivity.class);
                in.putExtra("data", (Serializable) tempData);
                startActivity(in);
                Log.d("MapData", tempData.toString());
//                addOrder(currCartID);
            }
        });

        return view;
    }

    public void addOrder(final List<String> cartID){

        class addOrder extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                loading = ProgressDialog.show(getContext(),"Uploading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(final String s) {
                super.onPostExecute(s);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        loading.dismiss();
                        Toast.makeText(getContext(),s,Toast.LENGTH_LONG).show();
                    }
                }, 2000);
                final Intent intent = new Intent(getContext(), CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(intent);
                    }
                }, 2000);
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String,String> params = new HashMap<>();
                String res = "";
                for(String cartData : cartID){
                    params.put("ID",cartData);

                    RequestHandler rh = new RequestHandler();
                    res = rh.sendPostRequest(phpConf.URL_UPDATE_CART_ORDER, params);

                }
                return res;
            }
        }

        addOrder add = new addOrder();
        add.execute();
    }

    private void getListMedicine(){
        listViewCart.setAdapter(null);
        JSONObject jsonObject = null;
        Context context = getContext();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                cartId = jo.getString("CART_ID");
                medName = jo.getString("MED_NAME");
                medCategory = jo.getString("CATEGORY");
                medPrice = jo.getString("PRICE");
                medQt = jo.getString("QUANTITY");
                medDesc = jo.getString("DESCRIPTION");
                medPict = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context,medPict);

                HashMap<String,Object> medicine = new HashMap<>();
                medicine.put("CART_ID",cartId);
                medicine.put("MED_NAME",medName);
                medicine.put("CATEGORY",medCategory);
                medicine.put("PRICE",medPrice);
                medicine.put("DESCRIPTION",medDesc);
                medicine.put("QUANTITY",medQt);
                medicine.put("MEDICINE_PICT",imgUri);
                medicine.put("isChecked","false");

                Log.d("tag", String.valueOf(medicine));

                listData.add(medicine);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

         adapter = new SimpleAdapter(
                getContext(), listData, R.layout.list_cart,
                new String[]{"MED_NAME","CATEGORY","MED_NAME","PRICE","QUANTITY","MEDICINE_PICT"},
                new int[]{R.id.rowCheckBox,R.id.medCategory, R.id.medName,R.id.medPrice, R.id.medQuantity, R.id.img});

        listViewCart.setAdapter(adapter);
    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getContext(),"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListMedicine();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("USER",mPreferences.getString("userName",""));

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_GET_CART,params);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
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
