package adrean.thesis.puocc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class CustomAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;

    private ArrayList<HashMap<String,String>> data;

    private int fragment_position;

    public CustomAdapter(Activity a, ArrayList<HashMap<String,String>> list, int fragment_pos) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        data = list;
        fragment_position = fragment_pos;
    }

    public void updateReceiptsList(ArrayList<HashMap<String,String>> newlist) {
        data.clear();
        data.addAll(newlist);
        this.notifyDataSetChanged();
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return data.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder {
        TextView tvId;
        TextView tvCreatedBy;
        TextView tvCreatedDt;
        TextView tvStatus;
        TextView tvTotalPrice;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        switch (fragment_position) {

            case 1:
                if (convertView == null) {
                    vi = inflater.inflate(R.layout.list_transaction_admin, parent, false);

                    holder = new ViewHolder();
                    holder.tvId = vi.findViewById(R.id.id);
                    holder.tvCreatedBy = vi.findViewById(R.id.createdBy);
                    holder.tvCreatedDt = vi.findViewById(R.id.createdDate);
                    holder.tvStatus = vi.findViewById(R.id.status);
                    holder.tvTotalPrice = vi.findViewById(R.id.trxPrice);

                    holder.tvId.setText(data.get(position).get("ID"));
                    holder.tvCreatedBy.setText(data.get(position).get("CREATED_BY"));
                    holder.tvCreatedDt.setText(data.get(position).get("CREATED_DT"));
                    holder.tvStatus.setText(data.get(position).get("STATUS"));
                    holder.tvTotalPrice.setText(data.get(position).get("TOTAL_PRICE"));

                    vi.setTag(holder);
                }

                break;

            default:
        }
        return vi;
    }

}

