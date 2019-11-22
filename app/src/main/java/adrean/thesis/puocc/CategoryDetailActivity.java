package adrean.thesis.puocc;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class CategoryDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category_detail);

        TextView category = (TextView) findViewById(R.id.text);
        Intent in = getIntent();
        category.setText(in.getStringExtra("category"));
    }
}
