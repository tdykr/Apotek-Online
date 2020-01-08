package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    EditText uName,uEmail,uPhone,uAddress,uPass,uRePass;
    String userName,userEmail,userPhone,userAddress,userPass,userRePass;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Medicine List");

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        uName = (EditText) findViewById(R.id.userName);
        uEmail = (EditText) findViewById(R.id.userEmail);
        uPhone = (EditText) findViewById(R.id.userPhone);
        uAddress = (EditText) findViewById(R.id.userAddress);
        uPass = (EditText) findViewById(R.id.userPass);
        uRePass = (EditText) findViewById(R.id.reTypePass);

        Button registerBtn = (Button) findViewById(R.id.regBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validate()){
                    addUser();
                }
            }
        });
    }

    private boolean validate(){
        Boolean validation = false;

        userName = uName.getText().toString();
        userEmail = uEmail.getText().toString();
        userPhone = uPhone.getText().toString();
        userAddress = uAddress.getText().toString();
        userPass = uPass.getText().toString();
        userRePass = uRePass.getText().toString();

        if(userName.isEmpty()){
            uName.setError("Email Field Can't be Empty!");
            uName.requestFocus();
        }else if(userEmail.isEmpty()){
            uEmail.setError("Email Field Can't be Empty!");
            uEmail.requestFocus();
        }else if(userPhone.isEmpty()){
            uPhone.setError("Email Field Can't be Empty!");
            uPhone.requestFocus();
        }else if(userAddress.isEmpty()){
            uAddress.setError("Email Field Can't be Empty!");
            uAddress.requestFocus();
        }else if(!userPass.equals(userRePass)){
            Toast.makeText(this, "Password Doesn't Match!", Toast.LENGTH_SHORT).show();
        }else{
            validation = true;
        }

        return validation;
    }

    private void addUser(){

        class addUser extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(RegisterActivity.this,"Registering User...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(RegisterActivity.this,s,Toast.LENGTH_LONG).show();
                if(!s.equals("Email Address Already Used")){
                    Intent in = new Intent(RegisterActivity.this,LoginActivity.class);
                    startActivity(in);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("EMAIL",userEmail);
                params.put("PASSWORD",userPass);
                params.put("USERNAME",userName);
                params.put("ADDRESS",userAddress);
                params.put("PHONE",userPhone);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_REGISTER, params);
                return res;
            }
        }

        addUser add = new addUser();
        add.execute();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
