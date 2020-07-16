package adrean.thesis.puocc;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

public class OwnerMain extends AppCompatActivity {

    Toolbar toolbar;
    UserModel userModel;
    UserPreference mUserPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();

        ImageView trxList = findViewById(R.id.listTransaction);
        ImageView imgSummaryReport = findViewById(R.id.summary_report);
        ImageView addApoteker =  findViewById(R.id.img_add_apoteker);
        ImageView addCategory = findViewById(R.id.addCategory);

        addCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OwnerMain.this,AddCategoryActivity.class);
                startActivity(in);
            }
        });

        addApoteker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OwnerMain.this,AddApotekerActivity.class);
                startActivity(in);
            }
        });

        trxList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OwnerMain.this,TransactionActivity.class);
                startActivity(in);
            }
        });

        imgSummaryReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentSummary = new Intent(OwnerMain.this,SummaryReportActivity.class);
                startActivity(intentSummary);
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

        if (id == R.id.menu_logout){
            Intent intent = new Intent(OwnerMain.this,LoginActivity.class);
            mUserPreference.logoutUser();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
