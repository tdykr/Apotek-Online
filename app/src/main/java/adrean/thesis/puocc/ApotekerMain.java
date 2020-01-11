
package adrean.thesis.puocc;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class ApotekerMain extends AppCompatActivity {

    private String userName;
    Toolbar toolbar;
    UserModel userModel;
    UserPreference mUserPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apoteker_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        TextView userLogin = (TextView) findViewById(R.id.userLogin);

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();
        userName = userModel.getUserName();
        userLogin.setText(userName);

        ImageView addNewProduct = (ImageView) findViewById(R.id.addNewProduct);
        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ApotekerMain.this,AddNewProductActivity.class);
                in.putExtra("userName",userName);
                startActivity(in);
            }
        });

        ImageView listMedicineActivity = (ImageView) findViewById(R.id.listMedicine);
        listMedicineActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStoragePermissionGranted()){
                    Intent in = new Intent(ApotekerMain.this,ListMedicineActivity.class);
                    startActivity(in);
                }else{
                    Toast.makeText(ApotekerMain.this, "Please allow permission for storage!", Toast.LENGTH_SHORT).show();
                }

            }
        });

        ImageView transactionActivity = (ImageView) findViewById(R.id.newTransaction);
        transactionActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ApotekerMain.this,TransactionActivity.class);
                startActivity(in);
            }
        });

        ImageView scanQrActivity = (ImageView) findViewById(R.id.scan);
        scanQrActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ApotekerMain.this,ScanQrActivity.class);
                startActivity(in);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_customer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }else if (id == R.id.menu_logout){
            Intent intent = new Intent(ApotekerMain.this,LoginActivity.class);
            mUserPreference.logoutUser();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }
}
