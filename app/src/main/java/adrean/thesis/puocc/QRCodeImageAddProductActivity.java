package adrean.thesis.puocc;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;

public class QRCodeImageAddProductActivity extends AppCompatActivity {

    private Bitmap bp;
    private String medName,medPrice,medDesc,medCategory;
    private ImageView qrImg;
    private OutputStream outputStream;
    private TextView mdNameTxt,mdPriceTxt,mdDescriptionTxt,mdCategoryTxt;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode_image_add_product);

        qrImg = (ImageView) findViewById(R.id.qrImg);
        mdNameTxt = (TextView) findViewById(R.id.medName);
        mdPriceTxt = (TextView) findViewById(R.id.medPrice);
        mdCategoryTxt = (TextView) findViewById(R.id.medCategory);
        mdDescriptionTxt = (TextView) findViewById(R.id.medDescription);

        Button save = (Button) findViewById(R.id.saveBtn);
        Button home = (Button) findViewById(R.id.mainMenu);

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), ApotekerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        Intent in = getIntent();
        bp = in.getParcelableExtra("bitmap");
        medName = in.getStringExtra("mdName");
        medPrice = in.getStringExtra("mdPrice");
        medDesc = in.getStringExtra("mdDesc");
        medCategory = in.getStringExtra("mdCat");

        mdNameTxt.setText(medName);
        mdPriceTxt.setText(medPrice);
        mdDescriptionTxt.setText(medDesc);
        mdCategoryTxt.setText(medCategory);

        qrImg.setImageBitmap(bp);

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyStoragePermissions(QRCodeImageAddProductActivity.this);
                saveImg(bp);
                Intent in = new Intent(QRCodeImageAddProductActivity.this,ApotekerMain.class);
                startActivity(in);
            }
        });
    }

    private void saveImg(Bitmap bp) {
        BitmapDrawable drawable = (BitmapDrawable) qrImg.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        File dir = new File("/sdcard/Pictures/qrThesis");
        dir.mkdirs();
        File file = new File(dir, "QRImg-" + medName + ".jpg");

        outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100,outputStream);
            outputStream.flush();
            outputStream.close();
            Toast.makeText(QRCodeImageAddProductActivity.this, "Image has been Saved", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {
                        QRCodeImageAddProductActivity.super.onBackPressed();
                    }
                }).create().show();
    }
}
