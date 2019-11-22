package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private String userEmail,userPass;
    HashMap<String,String> userData = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        final TextView email = (TextView) findViewById(R.id.userEmail);
        final TextView pass = (TextView) findViewById(R.id.userPass);
        Button loginBtn = (Button) findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = email.getText().toString();
                userPass = pass.getText().toString();

                if(userEmail.isEmpty()){
                    email.setError("Email Can't be Empty");
                    email.requestFocus();
                    Toast.makeText(LoginActivity.this, "Email Can't be Empty", Toast.LENGTH_SHORT).show();
                }else if(userPass.isEmpty()){
                    pass.setError("Password Can't be Empty");
                    pass.requestFocus();
                    Toast.makeText(LoginActivity.this, "Password Can't be Empty", Toast.LENGTH_SHORT).show();
                }else{
                    login();
                }
            }
        });
        TextView regTv = (TextView) findViewById(R.id.registerTxt);
        regTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(in);
            }
        });
    }

    private void login(){

        class login extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this,"Loading Data...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                Toast.makeText(LoginActivity.this,s,Toast.LENGTH_LONG).show();
                try {
                    JSONObject jsonObject = new JSONObject(s);
                    JSONArray data = jsonObject.getJSONArray("result");

                    JSONObject jo = data.getJSONObject(0);

                    Log.d("tagJsonObject", jo.toString());
                    String userId = jo.getString("ID");
                    String userName = jo.getString("USERNAME");
                    String userEmail = jo.getString("EMAIL");
                    String userAddress = jo.getString("ADDRESS");
                    String userPhone = jo.getString("PHONE");
                    String userRole = jo.getString("ROLE");
                    String response = jo.getString("response");
                    String message = jo.getString("message");

                    if(response.equals("1") && userRole.equals("admin")){
                        Intent apotekerAct = new Intent(LoginActivity.this, ApotekerMain.class);
                        userData.put("userId",userId);
                        userData.put("userName",userName);
                        userData.put("userEmail",userEmail);
                        userData.put("userAddress",userAddress);
                        userData.put("userPhone",userPhone);
                        apotekerAct.putExtra("userData",userData);
                        startActivity(apotekerAct);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }else if(response.equals("1") && userRole.equals("user")){
                        Intent customerAct = new Intent(LoginActivity.this, CustomerMain.class);
                        userData.put("userId",userId);
                        userData.put("userName",userName);
                        userData.put("userEmail",userEmail);
                        userData.put("userAddress",userAddress);
                        userData.put("userPhone",userPhone);
                        customerAct.putExtra("userData",userData);
                        startActivity(customerAct);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("EMAIL",userEmail);
                params.put("PASSWORD",userPass);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_LOGIN, params);
                return res;
            }
        }

        login add = new login();
        add.execute();
    }
}
