package adrean.thesis.puocc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class QRCodeImageAddProductActivity extends AppCompatActivity {

    private Bitmap bp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_image_add_product);

        ImageView qrImg = (ImageView) findViewById(R.id.qrImg);
        Intent in = getIntent();
        bp = in.getParcelableExtra("bitmap");
        qrImg.setImageBitmap(bp);
    }
}
