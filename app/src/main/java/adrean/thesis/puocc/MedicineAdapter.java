package adrean.thesis.puocc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.ItemViewHolder> {
    private static String TAG = "ItemAdapter";
    private ArrayList<HashMap<String, String>> data;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    Context context;

    public MedicineAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.data = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_medicine, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

        Bitmap medImg = encodedStringImage(data.get(position).get("MEDICINE_PICT"));

        holder.tvCategory.setText(data.get(position).get("CATEGORY"));
        holder.tvMedName.setText(data.get(position).get("MEDICINE_NAME"));
        holder.tvPrice.setText(data.get(position).get("PRICE"));
        holder.tvQt.setText(data.get(position).get("QUANTITY"));
        holder.medImg.setImageBitmap(medImg);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = data.get(position);
                String medId = map.get("ID");
                Intent intent = new Intent(context, MedicineDetailActivity.class);
                intent.putExtra("ID", medId);

                context.startActivity(intent);
            }
        });
    }

    public Bitmap encodedStringImage(String imgString) {
        byte[] decodedString = Base64.decode(imgString, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        return decodedBitmap;
    }


    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory;
        TextView tvMedName;
        TextView tvQt;
        TextView tvPrice;
        ImageView medImg;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.medCategory);
            tvMedName = itemView.findViewById(R.id.medName);
            tvQt = itemView.findViewById(R.id.medQuantity);
            tvPrice = itemView.findViewById(R.id.medPrice);
            medImg = itemView.findViewById(R.id.img);
        }
    }
}
