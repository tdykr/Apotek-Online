package adrean.thesis.puocc;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;

public class AddCategoryActivity extends AppCompatActivity {

    Button selectPictBtn,submitBtn;
    private Uri selectedImage;
    private Bitmap bitmap;
    private ImageView targetImage;
    private String categoryName,categoryImgStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        selectPictBtn = (Button) findViewById(R.id.uploadCategory);
        targetImage = (ImageView) findViewById(R.id.categoryImg);
        submitBtn = (Button) findViewById(R.id.submitBtn);
        final TextView catNameTv = (TextView) findViewById(R.id.categoryName);

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
                categoryName = catNameTv.getText().toString();

                if(categoryName.equals("") || categoryName.isEmpty()){
                    catNameTv.setError("Category Field Can't be Empty!");
                    catNameTv.requestFocus();
                }else{
                    if(targetImage.getDrawable() != null){
                        categoryImgStr = getStringImage(bitmap);
                        addCategory();
                    }else{
                        Toast.makeText(AddCategoryActivity.this, "Please Upload Category Image!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
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

    private void addCategory(){

        class addCategory extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(AddCategoryActivity.this,"Uploading Category...","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                Toast.makeText(AddCategoryActivity.this,s,Toast.LENGTH_LONG).show();
                if(!s.equals("Category Already Exist")){
                    Intent in = new Intent(AddCategoryActivity.this,OwnerMain.class);
                    startActivity(in);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("CATEGORY",categoryName);
                params.put("CAT_IMG",categoryImgStr);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(phpConf.URL_ADD_CATEGORY, params);
                return res;
            }
        }

        addCategory add = new addCategory();
        add.execute();
    }
}
