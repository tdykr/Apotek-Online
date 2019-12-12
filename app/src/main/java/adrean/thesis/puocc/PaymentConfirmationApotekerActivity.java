package adrean.thesis.puocc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PaymentConfirmationApotekerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paid_confirmation_apoteker);

        Button backBtn = (Button) findViewById(R.id.backBtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(PaymentConfirmationApotekerActivity.this,ApotekerMain.class);
                startActivity(in);
            }
        });
    }
}
