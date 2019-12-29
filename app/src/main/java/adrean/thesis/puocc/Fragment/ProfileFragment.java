package adrean.thesis.puocc.Fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

import adrean.thesis.puocc.DetailTransactionActivity;
import adrean.thesis.puocc.LoginActivity;
import adrean.thesis.puocc.R;
import adrean.thesis.puocc.RequestHandler;
import adrean.thesis.puocc.phpConf;

public class ProfileFragment extends Fragment {

    HashMap<String,String> userData = new HashMap<>();
    private String userId,userName,userEmail,userAddress,userPhone,JSON_STRING,newPass,newPassConf;
    SharedPreferences mPreferences;
    private Button changePassBtn;
    private TextView changePassTrigger;
    private RelativeLayout relPass;
    private EditText newPassET,newPassConfET;
    public ProfileFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        TextView uName = (TextView) view.findViewById(R.id.userNameTv);
        TextView uEmail = (TextView) view.findViewById(R.id.userEmailTv);
        TextView uPhone = (TextView) view.findViewById(R.id.userPhoneTv);

        relPass = view.findViewById(R.id.relPass);
        relPass.setVisibility(View.INVISIBLE);

        changePassBtn = (Button) view.findViewById(R.id.changePassBtn);
        newPassET = (EditText) view.findViewById(R.id.newPass);
        newPassConfET = (EditText) view.findViewById(R.id.newPassConf);
        changePassTrigger = (TextView) view.findViewById(R.id.changePass);
        changePassTrigger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                relPass.setVisibility(View.VISIBLE);
            }
        });

        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newPass = newPassET.getText().toString();
                newPassConf = newPassConfET.getText().toString();

                if(newPass.isEmpty()){
                    newPassET.setError("This Field be Empty");
                    newPassET.requestFocus();
                }else if(newPassConf.isEmpty()){
                    newPassConfET.setError("This Field Can't be Empty");
                    newPassConfET.requestFocus();
                }else if(!newPass.equals(newPassConf)){
                    Toast.makeText(getContext(), "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
                }else{
                    updatePass();
                }
            }
        });

        mPreferences = PreferenceManager.getDefaultSharedPreferences(this.getContext());

        Button logoutBtn = (Button) view.findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                mPreferences.edit().clear().apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Intent in = getActivity().getIntent();
        userData = (HashMap<String, String>) in.getSerializableExtra("userData");

        userId = mPreferences.getString("userId","");
        userName = mPreferences.getString("userName","");
        userEmail = mPreferences.getString("userEmail","");
        userAddress = mPreferences.getString("userAddress","");
        userPhone = mPreferences.getString("userPhone","");

        uName.setText(userName);
        uEmail.setText(userEmail);
        uPhone.setText(userPhone);

        return view;
    }

    private void updatePass(){
        class updatePass extends AsyncTask<Void,Void,String> {

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
                Toast.makeText(getContext(), s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ID",mPreferences.getString("userId",""));
                params.put("NEW_PASS",newPass);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_PASSWORD,params);
                return s;
            }
        }
        updatePass gj = new updatePass();
        gj.execute();
    }
}
