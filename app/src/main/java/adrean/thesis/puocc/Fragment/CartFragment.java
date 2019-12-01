package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

import adrean.thesis.puocc.CategoryDetailActivity;
import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import adrean.thesis.puocc.phpConf;

public class CartFragment extends Fragment {

    public CartFragment(){

    }

    SharedPreferences mPreferences;
    TextView id, medNameTv;
    String JSON_STRING,medId,medName,medCategory,medPrice,medDesc;
    Bitmap medPict;
    ListView listCart;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cart, container, false);

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());
        listCart = (ListView) view.findViewById(R.id.cartList);
        id = (TextView) view.findViewById(R.id.medCategory);
        medNameTv = (TextView) view.findViewById(R.id.medName);

        getJSON();
        return view;
    }

    private void getListMedicine(){
        JSONObject jsonObject = null;
        Context context = getContext();
        ArrayList<HashMap<String,Object>> list = new ArrayList<HashMap<String, Object>>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                medId = jo.getString("CART_ID");
                medName = jo.getString("MED_NAME");
                medCategory = jo.getString("CATEGORY");
                medPrice = jo.getString("PRICE");
                medDesc = jo.getString("DESCRIPTION");
                medPict = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context,medPict);

                HashMap<String,Object> medicine = new HashMap<>();
                medicine.put("CART_ID",id);
                medicine.put("MED_NAME",medName);
                medicine.put("CATEGORY",medCategory);
                medicine.put("PRICE","Rp. " + medPrice);
                medicine.put("DESCRIPTION",medDesc);
                medicine.put("MEDICINE_PICT",imgUri);

                Log.d("tag", String.valueOf(medicine));

                list.add(medicine);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        ListAdapter adapter = new SimpleAdapter(
                getContext(), list, R.layout.list_cart,
                new String[]{"MED_NAME","CATEGORY","PRICE","QUANTITY","MEDICINE_PICT"},
                new int[]{R.id.rowCheckBox,R.id.medCategory, R.id.medPrice, R.id.qt, R.id.img});

        listCart.setAdapter(adapter);
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
