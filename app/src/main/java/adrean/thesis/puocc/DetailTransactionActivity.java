package adrean.thesis.puocc;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

import static android.Manifest.permission.CAMERA;

public class DetailTransactionActivity extends AppCompatActivity {

    private String JSON_STRING,cartId,medName,medCategory,medPrice,medDesc,medQt,trxId,imgStr,billImg,trxDate,status,totalPrice;
    private Bitmap medPict;
    private ListAdapter adapter;
    private static final int PIC_ID = 123;
    private Button uploadBillBtn,submitBillBtn,confirmTrxBtn,endTrxBtn;
    private ArrayList<HashMap<String,String>> listData = new ArrayList<>();
    private UserModel userModel;
    private UserPreference mUserPreference;
    private Uri selectedImage;
    private Bitmap bitmap;
    private ImageView targetImage;
    private ImageView imgPrinter;
    Toolbar toolbar;
    private DecimalFormat df = new DecimalFormat("#,###.##");
    private LinearLayout llPrint;
    private RelativeLayout rlDetailTrans;
    private ItemDetailAdapter itemDetailAdapter;
    private RecyclerView rvItemDetail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaction);

        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Transaction Detail");

        mUserPreference = new UserPreference(DetailTransactionActivity.this);
        userModel = mUserPreference.getUser();

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        itemDetailAdapter = new ItemDetailAdapter(DetailTransactionActivity.this, listData);
        itemDetailAdapter.notifyDataSetChanged();
        rvItemDetail = findViewById(R.id.rv_item_detail);
        rvItemDetail.setHasFixedSize(true);
        rvItemDetail.setLayoutManager(new LinearLayoutManager(DetailTransactionActivity.this));
        rvItemDetail.setAdapter(itemDetailAdapter);

        uploadBillBtn = (Button) findViewById(R.id.uploadReceiptBtn);
        submitBillBtn = (Button) findViewById(R.id.submitBill);
        confirmTrxBtn = (Button) findViewById(R.id.confirmTrx);
        endTrxBtn = (Button) findViewById(R.id.endTrx);
        imgPrinter = findViewById(R.id.img_download);
        llPrint = findViewById(R.id.rel1);
        rlDetailTrans = findViewById(R.id.rl_detail_trans);

        confirmTrxBtn.setVisibility(View.GONE);
        endTrxBtn.setVisibility(View.GONE);

        targetImage = (ImageView) findViewById(R.id.imgBill);
        TextView trxIdTv = (TextView) findViewById(R.id.trxId);
        TextView trxDateTv = (TextView) findViewById(R.id.trxDate);
        TextView trxStatus = (TextView) findViewById(R.id.trxStatus);
        TextView trxTot = (TextView) findViewById(R.id.trxPrice);
        TextView trxUser = (TextView) findViewById(R.id.trxUser);
        TextView trxAddress = (TextView) findViewById(R.id.trxAddress);
        TextView trxType = (TextView) findViewById(R.id.trxType);

        Intent in = getIntent();
        trxId = in.getStringExtra("TRANS_ID");
        billImg = in.getStringExtra("BILL_IMG");
        trxDate = in.getStringExtra("DATE");
        status = in.getStringExtra("STATUS");
        totalPrice = in.getStringExtra("TOTAL_PRICE");
        String type = in.getStringExtra("TYPE");
        String address = in.getStringExtra("ADDRESS");
        String user = in.getStringExtra("CREATED_BY");

        if(type.equals("OFFLINE")){
            uploadBillBtn.setVisibility(View.GONE);
            submitBillBtn.setVisibility(View.GONE);
            confirmTrxBtn.setVisibility(View.GONE);
            endTrxBtn.setVisibility(View.GONE);
        }

        trxIdTv.setText(trxId);
        trxTot.setText(totalPrice);
        trxStatus.setText(status);
        trxDateTv.setText(trxDate);
        trxType.setText(type);
        trxUser.setText(user);

        if(address != null && !address.equals("null") &&  !address.isEmpty()){
            trxAddress.setText(address);
        }else{
            trxAddress.setText("-");
        }

        if(trxId.contains("TRX")){
            trxId = trxId.replace("TRX-","");
        }

        mUserPreference = new UserPreference(this);
        userModel = mUserPreference.getUser();

        getJSON();

        uploadBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPictureDialog();
            }
        });

        confirmTrxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateTransactionPaid();
            }
        });

        endTrxBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateEndTransaction();
            }
        });

        submitBillBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(targetImage.getDrawable() != null){
                    imgStr = getStringImage(bitmap);
                    uploadBillTrx();
                }else{
                    Toast.makeText(DetailTransactionActivity.this, "Please Upload Your Transaction Bill First!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        imgPrinter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String dest = FileUtils.getAppPath(DetailTransactionActivity.this) + trxId+".pdf";
                createPdf(dest);
            }
        });


        if(userModel.getUserRole().equals("admin")){

            submitBillBtn.setVisibility(View.GONE);
            uploadBillBtn.setVisibility(View.GONE);
            if(billImg != null && !billImg.equals("null") &&  !billImg.isEmpty()) {
                Bitmap imgBillBp = encodedStringImage(billImg);
                targetImage.setImageBitmap(imgBillBp);
            }
            targetImage.setVisibility(View.VISIBLE);

            if(status.equals("PAID")){
                confirmTrxBtn.setVisibility(View.VISIBLE);
            }else{
                confirmTrxBtn.setVisibility(View.GONE);
            }
        }else if(userModel.getUserRole().equals("user")) {
            trxUser.setVisibility(View.GONE);
            trxAddress.setVisibility(View.GONE);
            trxType.setVisibility(View.GONE);
            if (billImg != null && !billImg.equals("null") && !billImg.isEmpty()) {
                submitBillBtn.setVisibility(View.GONE);
                uploadBillBtn.setVisibility(View.GONE);
                Bitmap imgBillBp = encodedStringImage(billImg);
                targetImage.setImageBitmap(imgBillBp);
                targetImage.setVisibility(View.VISIBLE);
                if(status.equals("CONFIRMED")){
                    endTrxBtn.setVisibility(View.VISIBLE);
                }
            }else{
                submitBillBtn.setVisibility(View.VISIBLE);
                uploadBillBtn.setVisibility(View.VISIBLE);
            }
        }else if(userModel.getUserRole().equals("owner")){
            if(billImg != null && !billImg.equals("null") &&  !billImg.isEmpty()) {
                Bitmap imgBillBp = encodedStringImage(billImg);
                targetImage.setImageBitmap(imgBillBp);
            }
            submitBillBtn.setVisibility(View.GONE);
            uploadBillBtn.setVisibility(View.GONE);
            targetImage.setVisibility(View.GONE);
            confirmTrxBtn.setVisibility(View.GONE);
        }
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.show();
    }

    private void takePhotoFromCamera() {
        if(isCameraPermissionGranted()){
            Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(camera_intent, PIC_ID);
        }else{
            Toast.makeText(DetailTransactionActivity.this, "Please allow camera permission!", Toast.LENGTH_SHORT).show();
        }
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent,0);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent data) {
//        if (resultCode == RESULT_OK) {
//            selectedImage = data.getData();
//            try {
//                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
//                targetImage.setImageBitmap(bitmap);
//                BitmapHelper.getInstance().setBitmap(bitmap);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
        if(resultCode == RESULT_OK && data != null){
            if(requestCode == 0){
                selectedImage = data.getData();
                try {
                    bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage));
                    targetImage.setImageBitmap(bitmap);
                    BitmapHelper.getInstance().setBitmap(bitmap);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(requestCode == PIC_ID){
                bitmap = (Bitmap) data.getExtras().get("data");
                targetImage.setImageBitmap(bitmap);
            }
        }
    }

    private void createPdf(String dest){
        if (new File(dest).exists()) {
            new File(dest).delete();
        }


        try {
            FileOutputStream fOut = new FileOutputStream(dest);

            PdfDocument document = new PdfDocument();
            LinearLayout view = findViewById(R.id.rel1);
            Bitmap bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bm);
            view.draw(canvas);
            PdfDocument.PageInfo pageInfo = new
                    PdfDocument.PageInfo.Builder(bm.getWidth(), bm.getHeight(), 1).create();
            PdfDocument.Page page = document.startPage(pageInfo);
            bm.prepareToDraw();
            Canvas c;
            c = page.getCanvas();
            c.drawBitmap(bm,0,0,null);
            document.finishPage(page);
            document.writeTo(fOut);
            document.close();

            Toast.makeText(DetailTransactionActivity.this,"PDF generated successfully", Toast.LENGTH_SHORT).show();
            FileUtils.openFile(DetailTransactionActivity.this, new File(dest));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void addImage(Document document,byte[] byteArray)
    {
        Image image = null;
        try
        {
            image = Image.getInstance(byteArray);
        }
        catch (BadElementException e)
        {
            e.printStackTrace();
        }
        catch (MalformedURLException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        // image.scaleAbsolute(150f, 150f);
        try
        {
            document.add(image);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
    }

    private void updateEndTransaction(){
        class updateEndTransaction extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
                Intent in = new Intent(DetailTransactionActivity.this,CustomerMain.class);
                startActivity(in);
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_END_TRX_STATUS,params);
                return s;
            }
        }
        updateEndTransaction gj = new updateEndTransaction();
        gj.execute();
    }

    private void updateTransactionPaid(){
        class updateTransactionPaid extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
                if(userModel.getUserRole().equals("user")){
                    Intent in = new Intent(DetailTransactionActivity.this,CustomerMain.class);
                    startActivity(in);
                }else if(userModel.getUserRole().equals("admin")){
                    Intent in = new Intent(DetailTransactionActivity.this,ApotekerMain.class);
                    startActivity(in);
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_CART_ORDER_STATUS_CONFIRMED,params);
                return s;
            }
        }
        updateTransactionPaid gj = new updateTransactionPaid();
        gj.execute();
    }

    private void getListMedicine(){
        JSONObject jsonObject = null;
        Context context = getApplicationContext();
        try {
            jsonObject = new JSONObject(JSON_STRING);
            JSONArray result = jsonObject.getJSONArray("result");

            for(int i = 0; i<result.length(); i++){
                JSONObject jo = result.getJSONObject(i);
                cartId = jo.getString("CART_ID");
                medName = jo.getString("MED_NAME");
                medCategory = jo.getString("CATEGORY");
                medPrice = jo.getString("PRICE");
                double dbMedPrice = Double.parseDouble(medPrice);
                String formattedMedPrice=getString(R.string.rupiah,df.format(dbMedPrice));

                medQt = jo.getString("QUANTITY");
                medDesc = jo.getString("DESCRIPTION");
                medPict = encodedStringImage(jo.getString("MEDICINE_PICT"));

                Uri imgUri = getImageUri(context,medPict);

                HashMap<String,String> medicine = new HashMap<>();
                medicine.put("CART_ID",cartId);
                medicine.put("MED_NAME",medName);
                medicine.put("CATEGORY",medCategory);
                medicine.put("PRICE",  medPrice);
                medicine.put("FORMATTED_PRICE",  formattedMedPrice);
                medicine.put("DESCRIPTION",medDesc);
                medicine.put("QUANTITY",medQt);
                medicine.put("MEDICINE_PICT",imgUri.toString());
                medicine.put("isChecked","false");

                Log.d("tag", String.valueOf(medicine));

                listData.add(medicine);
                itemDetailAdapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void getJSON(){
        class GetJSON extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Fetching Data","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                getListMedicine();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_GET_DETAIL_TRX_APOTEKER,params);
                return s;
            }
        }
        GetJSON gj = new GetJSON();
        gj.execute();
    }

    private void uploadBillTrx(){
        class uploadBillTrx extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransactionActivity.this,"Uploading Image","Please Wait...",false,false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                JSON_STRING = s;
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
                updateQuantity();
                Intent intent = new Intent(DetailTransactionActivity.this, CustomerMain.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("BILL_TRX_IMG",imgStr);
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPLOAD_BILL_TRANSACTION,params);
                return s;
            }
        }
        uploadBillTrx gj = new uploadBillTrx();
        gj.execute();
    }

    private void updateQuantity(){
        class uploadQtTrx extends AsyncTask<Void,Void,String> {

            ProgressDialog loading;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                JSON_STRING = s;
                Toast.makeText(DetailTransactionActivity.this, s, Toast.LENGTH_SHORT).show();
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String,String> params = new HashMap<>();
                params.put("TRX_ID",trxId);

                RequestHandler rh = new RequestHandler();
                String s = rh.sendPostRequest(phpConf.URL_UPDATE_QT_AFTER_STATUS_PAID,params);
                return s;
            }
        }
        uploadQtTrx gj = new uploadQtTrx();
        gj.execute();
    }

    public Bitmap encodedStringImage(String imgString){
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString,0,decodedString.length);

        return decodedBitmap;
    }

    public String getStringImage(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public  boolean isCameraPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), CAMERA) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{CAMERA}, 1);                return false;
            }
        }
        else {
            return true;
        }
    }
}
