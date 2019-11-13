
package adrean.thesis.puocc;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

public class ApotekerMain extends AppCompatActivity {

    HashMap<String,String> userData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apoteker_main);

        TextView userLogin = (TextView) findViewById(R.id.userLogin);
        Intent in = getIntent();
        userData = (HashMap<String, String>)in.getSerializableExtra("userData");
        userLogin.setText(userData.get("userName"));

        ImageView addNewProduct = (ImageView) findViewById(R.id.addNewProduct);
        addNewProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ApotekerMain.this,AddNewProductActivity.class);
                in.putExtra("userName",userData.get("userName"));
                startActivity(in);
            }
        });

        final ImageView listMedicineActivity = (ImageView) findViewById(R.id.listMedicine);
        listMedicineActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(ApotekerMain.this,ListMedicineActivity.class);
                startActivity(in);
            }
        });
    }
//
//    @Override
//    public void onBackPressed() {
//        new AlertDialog.Builder(this)
//                .setTitle("Really Exit?")
//                .setMessage("Are you sure you want to exit?")
//                .setNegativeButton(android.R.string.no, null)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        ApotekerMain.super.onBackPressed();
//                    }
//                }).create().show();
//    }
}
