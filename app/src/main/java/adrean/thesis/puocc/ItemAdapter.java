package adrean.thesis.puocc;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {
    private static String TAG = "ItemAdapter";
    private ArrayList<HashMap<String, String>> data;
    private DecimalFormat df = new DecimalFormat("#,###.###");
    Context context;

    public ItemAdapter(Context context, ArrayList<HashMap<String, String>> list) {
        this.data = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_transaction_admin, viewGroup, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

        holder.tvId.setText(data.get(position).get("ID"));
        holder.tvCreatedBy.setText(data.get(position).get("CREATED_BY"));
        holder.tvCreatedDt.setText(data.get(position).get("CREATED_DT"));
        holder.tvStatus.setText(data.get(position).get("STATUS"));
        holder.tvTotalPrice.setText(data.get(position).get("TOTAL_PRICE"));
        holder.tvType.setText(data.get(position).get("TYPE"));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> map = data.get(position);
                String trxId = map.get("ID");
                Intent intent = new Intent(context, DetailTransactionActivity.class);
                intent.putExtra("TRX-ID", trxId);
                String billImgIntent = map.get("BILL_IMG");
                String trxDate = map.get("CREATED_DT");
                intent.putExtra("TRANS_ID", trxId);
                intent.putExtra("DATE", trxDate);
                intent.putExtra("STATUS", map.get("STATUS"));
                intent.putExtra("TYPE", map.get("TYPE"));
                intent.putExtra("CREATED_BY", map.get("CREATED_BY"));
                intent.putExtra("ADDRESS", map.get("ADDRESS"));
                intent.putExtra("TOTAL_PRICE", map.get("TOTAL_PRICE"));
                if (billImgIntent != null && !billImgIntent.equals("null") && !billImgIntent.isEmpty()) {
                    intent.putExtra("BILL_IMG", billImgIntent);
                }
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvId;
        TextView tvCreatedBy;
        TextView tvCreatedDt;
        TextView tvStatus;
        TextView tvTotalPrice;
        TextView tvType;

        ItemViewHolder(View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.id);
            tvCreatedBy = itemView.findViewById(R.id.createdBy);
            tvCreatedDt = itemView.findViewById(R.id.createdDate);
            tvStatus = itemView.findViewById(R.id.status);
            tvTotalPrice = itemView.findViewById(R.id.trxPrice);
            tvType = itemView.findViewById(R.id.type);
        }
    }
}
