package adrean.thesis.puocc;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

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

        TextView trxList = (TextView) findViewById(R.id.listTransaction);
        TextView addApoteker = (TextView) findViewById(R.id.addApoteker);

        addApoteker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(OwnerMain.this,AddApotekerActivity.class);
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
            Intent intent = new Intent(OwnerMain.this,LoginActivity.class);
            mUserPreference.logoutUser();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

}
