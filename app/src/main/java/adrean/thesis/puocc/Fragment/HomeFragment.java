package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adrean.thesis.puocc.DetailTransactionActivity;
import adrean.thesis.puocc.ItemDetailAdapter;
import adrean.thesis.puocc.ListMedicineActivity;
import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import adrean.thesis.puocc.phpConf;

public class HomeFragment extends Fragment {

    public HomeFragment() {
        // Required empty public constructor
    }

    private List<Model> model;
    private String JSON_STRING;

    private ViewPager listCategory;
    Adapter adapter;
    private SharedPreferences mPreferences;
    private SharedPreferences.Editor mEditor;
    private TextView etSearch;
    private LinearLayout llHome;
    private ArrayList<HashMap<String, String>> list = new ArrayList<>();
    private ArrayList<HashMap<String, String>> listSearch = new ArrayList<>();
    private ArrayList<HashMap<String,String>> listData = new ArrayList<>();
    private ItemDetailAdapter itemDetailAdapter;
    private RecyclerView rvItemDetail;
    private DecimalFormat df = new DecimalFormat("#,###.##");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        Intent in = getActivity().getIntent();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        mEditor = mPreferences.edit();

        llHome = view.findViewById(R.id.llHome);
        listCategory = (ViewPager) view.findViewById(R.id.medCat);
        CardView card1 = (CardView) view.findViewById(R.id.card1);
        etSearch= view.findViewById(R.id.etSearch);

        getListMedicineJSON();

        itemDetailAdapter = new ItemDetailAdapter(getActivity(), list);
        itemDetailAdapter.notifyDataSetChanged();
        rvItemDetail = view.findViewById(R.id.rv_item_detail);
        rvItemDetail.setHasFixedSize(true);
        rvItemDetail.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvItemDetail.setAdapter(itemDetailAdapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("") || charSequence.toString().trim().length() == 0 || charSequence == null) {
                    llHome.setVisibility(View.VISIBLE);
                    rvItemDetail.setVisibility(View.GONE);
                } else {
                    llHome.setVisibility(View.GONE);
                    rvItemDetail.setVisibility(View.VISIBLE);
                    listSearch.clear();
                    for (int j = 0; j < listData.size(); j++) {
                        if (listData.get(j).get("MEDICINE_NAME").toUpperCase().contains(charSequence.toString().trim().toUpperCase())) {
                            listSearch.add(listData.get(j));
                        }
                    }

                    changeList(listSearch);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        card1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try{
                    Intent sendIntent =new Intent("android.intent.action.MAIN");
//                    sendIntent.setComponent(new ComponentName("com.whatsapp", "com.whatsapp.Conversation"));
                    sendIntent.putExtra("jid", "6281297444885@s.whatsapp.net");
                    sendIntent.putExtra(Intent.EXTRA_TEXT, "test");
                    sendIntent.setAction(Intent.ACTION_SEND);
                    sendIntent.setPackage("com.whatsapp");
                    sendIntent.setType("text/plain");
                    startActivity(sendIntent);
                }catch(Exception e){
                    Toast.makeText(getContext(),"Error/n"+ e.toString(),Toast.LENGTH_SHORT).show();
                }
            }
        });

        getJSON();

        return view;
    }

    private void getMedicineCategory(){
        JSONObject jsonObject = null;
        model = new ArrayList<>();
        Context context = getContext();
        ArrayList<HashMap<String,Object>> data = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String cat = jo.getString("CATEGORY");
                String id = jo.getString("ID");
                String catImg = jo.getString("CAT_IMG");
                catImg.split("/");
                Bitmap bp = encodedStringImage(catImg);

                Uri uri = getImageUri(context,bp);
                HashMap<String,Object> category = new HashMap<>();
                category.put("category",cat);
                category.put("id",id);
                category.put("image",uri);

                data.add(category);
                model.add(new Model(uri,cat,id));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /*ListAdapter adapter = new SimpleAdapter(
                getContext(), data, R.layout.list_category,
                new String[]{"category"},
                new int[]{R.id.category});*/

        adapter = new Adapter(model,getContext());
        listCategory.setAdapter(adapter);
        listCategory.setPadding(130, 0, 130, 0);

        listCategory.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
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
                getMedicineCategory();
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(phpConf.URL_GET_MEDICINE_CATEGORY);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void getListMedicineJSON(){
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(getActivity(), "Fetching Data", "Please Wait...", false, false);
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

    private void getListMedicine() {
        JSONObject jsonObject = null;
        Context context = getContext();
        ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
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
                medicine.put("MEDICINE_PICT", imgUri.toString());

                listData.add(medicine);
                changeList(listData);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void changeList(ArrayList<HashMap<String, String>> newList) {
        Log.d("tag", String.valueOf(newList));
        list.clear();
        list.addAll(newList);
        itemDetailAdapter.notifyDataSetChanged();
    }
}
