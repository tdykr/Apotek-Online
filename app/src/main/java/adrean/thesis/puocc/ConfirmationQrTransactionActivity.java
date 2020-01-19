package adrean.thesis.puocc;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class ConfirmationQrTransactionActivity extends AppCompatActivity {

    int totalPrice = 0;
    ListView medList;
    UserPreference mUserPreferences;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation_qr_transaction);

        medList = (ListView) findViewById(R.id.list);
        TextView totalPriceTv = (TextView) findViewById(R.id.totalPrice);
        Button submitBtn = (Button) findViewById(R.id.btnConfirm);

        Intent in = getIntent();
        in.getSerializableExtra("data");
        List<Map<String,String>> data = (List<Map<String, String>>) in.getSerializableExtra("data");

        mUserPreferences = new UserPreference(this);
        userModel = mUserPreferences.getUser();

    }
}
