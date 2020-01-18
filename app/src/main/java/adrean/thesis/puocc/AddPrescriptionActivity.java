package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class AddPrescriptionActivity extends AppCompatActivity {

    private ImageView targetImage;
    private String totalPrice,presImgStr;
    private TextView totalPriceTv;
    private Uri selectedImage;
    private Bitmap bitmap;
    UserPreference mUserPreference;
    UserModel userModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_prescription);

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();
        totalPriceTv = (TextView) findViewById(R.id.trxPrice);
        targetImage = (ImageView) findViewById(R.id.categoryImg);
        Button selectPictBtn = (Button) findViewById(R.id.uploadCategory);
        Button submitBtn = (Button) findViewById(R.id.submitBtn);

        selectPictBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(in,0);
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                totalPrice = totalPriceTv.getText().toString();

                if(totalPrice.equals("") || totalPrice.isEmpty()){
                    totalPriceTv.setError("Category Field Can't be Empty!");
                    totalPriceTv.requestFocus();
                }else{
                    if(targetImage.getDrawable() != null){
                        presImgStr = getStringImage(bitmap);
                        addPrescription();
                    }else{
                        Toast.makeText(AddPrescriptionActivity.this, "Please Upload Prescription Image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
        if (resultCode == RESULT_OK) {
            selectedImage = data.getData();
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                targetImage.setImageBitmap(bitmap);
                BitmapHelper.getInstance().setBitmap(bitmap);
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void addPrescription(){

        class addPrescription extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddPrescriptionActivity.this,"Uploading Prescription...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(AddPrescriptionActivity.this,s,Toast.LENGTH_LONG).show();
                if(!s.equals("Failed to Insert Transaction Data")){
                    Intent in = new Intent(AddPrescriptionActivity.this,OwnerMain.class);
                    startActivity(in);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("TOTAL_PRICE",totalPrice);
                params.put("IMG",presImgStr);
                params.put("TYPE","OFFLINE");
                params.put("USER",userModel.getUserName());

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_ADD_PRESCRIPTION, params);
                return res;
            }
        }

        addPrescription add = new addPrescription();
        add.execute();
    }
}
