package adrean.thesis.puocc;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemDetailAdapter extends RecyclerView.Adapter<ItemDetailAdapter.ItemViewHolder> {
    private static String TAG = "ItemAdapter";
    private ArrayList<HashMap<String, String>> data;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    Context context;

    public ItemDetailAdapter(Context context, ArrayList<HashMap<String, String>> list) {
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

        if (data.get(position).containsKey("MED_NAME")){
            holder.tvMedName.setText(data.get(position).get("MED_NAME"));
        }else  holder.tvMedName.setText(data.get(position).get("MEDICINE_NAME"));
        holder.tvMedCategory.setText(data.get(position).get("CATEGORY"));
        holder.tvMedPrice.setText(data.get(position).get("PRICE"));
        holder.tvMedQuantity.setText(data.get(position).get("QUANTITY"));
        holder.imgMedicine.setImageURI(Uri.parse(data.get(position).get("MEDICINE_PICT")));
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedCategory;
        TextView tvMedName;
        TextView tvMedPrice;
        TextView tvMedQuantity;
        ImageView imgMedicine;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvMedCategory = itemView.findViewById(R.id.medCategory);
            tvMedName = itemView.findViewById(R.id.medName);
            tvMedPrice = itemView.findViewById(R.id.medPrice);
            tvMedQuantity = itemView.findViewById(R.id.medQuantity);
            imgMedicine = itemView.findViewById(R.id.img);
        }
    }
}
