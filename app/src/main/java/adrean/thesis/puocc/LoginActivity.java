package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    private String userEmail, userPass;
    private UserModel userModel;
    private UserPreference mUserPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();
        if (userModel.getUserId() != null) {
            userEmail = userModel.getUserEmail();
            userPass = userModel.getUserPassword();
            login();
        }
        userModel = new UserModel();

        final TextView email = findViewById(R.id.userEmail);
        final TextView pass = findViewById(R.id.userPass);
        TextView regTv = findViewById(R.id.registerTxt);
        Button loginBtn = findViewById(R.id.btnLogin);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userEmail = email.getText().toString();
                userPass = pass.getText().toString();

                if (userEmail.isEmpty()) {
                    email.setError("Email Can't be Empty");
                    email.requestFocus();
                    Toast.makeText(LoginActivity.this, "Email Can't be Empty", Toast.LENGTH_SHORT).show();
                } else if (userPass.isEmpty()) {
                    pass.setError("Password Can't be Empty");
                    pass.requestFocus();
                    Toast.makeText(LoginActivity.this, "Password Can't be Empty", Toast.LENGTH_SHORT).show();
                } else {
                    login();
                }
            }
        });

        regTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(in);
            }
        });
    }

    private void login() {

        class login extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(LoginActivity.this, "Loading Data...", "Please Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);

                loading.dismiss();
                try {
                    Log.d("Json Login", s);
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

                    if (response.equals("1")) {
                        saveUser(userId, userPass, userName, userEmail, userAddress, userPhone,
                                userRole, response, message);
                    }
                    if (response.equals("1") && userRole.equals("admin")) {
                        Intent apotekerAct = new Intent(LoginActivity.this, ApotekerMain.class);//
                        startActivity(apotekerAct);

                    } else if (response.equals("1") && userRole.equals("user")) {
                        Intent customerAct = new Intent(LoginActivity.this, CustomerMain.class);
                        startActivity(customerAct);
                    }else if(response.equals("1") && userRole.equals("owner")){
                        Intent ownerAct = new Intent(LoginActivity.this, OwnerMain.class);
                        startActivity(ownerAct);
                    }
                    Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();
                params.put("EMAIL", userEmail);
                params.put("PASSWORD", userPass);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_LOGIN, params);
                return res;
            }
        }

        login add = new login();
        add.execute();
    }

    void saveUser(String userId, String userPassword, String userName, String userEmail, String userAddress, String userPhone,
                  String userRole, String response, String message) {
        UserPreference userPreference = new UserPreference(this);
        userModel.setUserId(userId);
        userModel.setUserPassword(userPassword);
        userModel.setUserName(userName);
        userModel.setUserEmail(userEmail);
        userModel.setUserPhone(userPhone);
        userModel.setUserAddress(userAddress);
        userModel.setUserRole(userRole);
        userModel.setUserResponse(response);
        userModel.setUserMessage(message);

        userPreference.setUser(userModel);
    }
}
