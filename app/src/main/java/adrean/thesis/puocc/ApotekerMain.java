
package adrean.thesis.puocc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class ApotekerMain extends AppCompatActivity {

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apoteker_main);

        TextView userLogin = (TextView) findViewById(R.id.userLogin);

        final SharedPreferences mPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userName = mPreferences.getString("userName","");
        userLogin.setText(userName);

        ImageView logoutBtn = (ImageView) findViewById(R.id.logout);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ApotekerMain.this,LoginActivity.class);
                mPreferences.edit().clear().apply();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

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
                Intent in = new Intent(ApotekerMain.this,ListMedicineActivity.class);
                startActivity(in);
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
    }
}
