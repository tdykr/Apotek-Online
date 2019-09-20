package adrean.thesis.puocc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class AddNewProductActivity extends AppCompatActivity {

    String mdCategory,mdName,mdPrice,mdNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_product);

        Spinner sp = (Spinner) findViewById(R.id.medCategory);
        final EditText medicineName = (EditText) findViewById(R.id.medName);
        final EditText medicinePrice = (EditText) findViewById(R.id.medPrice);
        final EditText medicineNotes = (EditText) findViewById(R.id.medNotes);
        Button generate = (Button) findViewById(R.id.generateBtn);

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mdName = medicineName.getText().toString();
                mdPrice = medicinePrice.getText().toString();
                mdNotes = medicineNotes.getText().toString();
                String text = "{\"name\":\""+ mdName +"\",\"price\":\""+ mdPrice +"\"}";
                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                    Intent in = new Intent(AddNewProductActivity.this,QRCodeImageAddProductActivity.class);
                    in.putExtra("bitmap",bitmap);
                    startActivity(in);
                    //imageView.setImageBitmap(bitmap);
                } catch (WriterException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
