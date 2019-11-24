package adrean.thesis.puocc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class OrderMedicineActivity extends AppCompatActivity {

    private TextView ordAddress,ordChangeAddress,ordDetail,ordAmount,ordPrice,ordTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_medicine);

        ordAddress = (TextView) findViewById(R.id.address);
        ordChangeAddress = (TextView) findViewById(R.id.changeAddress);
        ordDetail = (TextView) findViewById(R.id.confDetail);
        ordAmount = (TextView) findViewById(R.id.confAmount);
        ordPrice = (TextView) findViewById(R.id.confPrice);
        ordTotal = (TextView) findViewById(R.id.totalPrice);



        Intent in = getIntent();
    }
}
