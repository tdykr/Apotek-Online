package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.Button;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        Button uploadReceiptBtn = (Button) view.findViewById(R.id.uploadReceipt);

        Intent in = getActivity().getIntent();

        mPreferences = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        mEditor = mPreferences.edit();

        listCategory = (ViewPager) view.findViewById(R.id.medCat);

        uploadReceiptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        getJSON();

        return view;
    }

    private void getMedicineCategory(){
        JSONObject jsonObject = null;
        model = new ArrayList<>();

        ArrayList<HashMap<String,String>> data = new ArrayList<>();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                String cat = jo.getString("CATEGORY");
                String id = jo.getString("ID");

                HashMap<String,String> category = new HashMap<>();
                category.put("category",cat);
                category.put("id",id);

                data.add(category);
                model.add(new Model(cat,id));
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
}
