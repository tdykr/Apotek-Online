package adrean.thesis.puocc;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Map;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;


public class QrAdapter extends BaseAdapter {

    private static LayoutInflater inflater = null;
    private Activity activity;

    private ArrayList<Map<String,String>> data;

    private int fragment_position;

    public QrAdapter(Activity a, ArrayList<Map<String,String>> list, int fragment_pos) {
        activity = a;
        inflater = (LayoutInflater) activity.getSystemService(LAYOUT_INFLATER_SERVICE);

        data = list;
        fragment_position = fragment_pos;
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
        TextView tvMedId;
        TextView tvMedCategory;
        TextView tvMedName;
        TextView tvMedPrice;
        EditText medQt;
    }

    @SuppressLint("SetTextI18n")
    public View getView(final int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        final ViewHolder holder;

        switch (fragment_position) {

            case 1:
                if (convertView == null) {
                    vi = inflater.inflate(R.layout.list_scanned_qr_medicine, parent, false);

                    holder = new ViewHolder();
                    holder.tvMedId = vi.findViewById(R.id.medId);
                    holder.tvMedCategory = vi.findViewById(R.id.medCategory);
                    holder.tvMedName = vi.findViewById(R.id.medName);
                    holder.tvMedPrice = vi.findViewById(R.id.medPrice);
                    holder.medQt = vi.findViewById(R.id.confAmount);

                    holder.tvMedId.setText(data.get(position).get("ID"));
                    holder.tvMedCategory.setText(data.get(position).get("CATEGORY"));
                    holder.tvMedName.setText(data.get(position).get("MED_NAME"));
                    holder.tvMedPrice.setText(data.get(position).get("PRICE"));

                    vi.setTag(holder);

                    if(holder.medQt.getText().toString().isEmpty()){
                        Map<String,String> row = data.get(position);
                        row.put("QUANTITY", "1");
                        data.set(position, row);
                    }

                    holder.medQt.addTextChangedListener(new TextWatcher(){// add text watcher to update your data after editing
                        public void afterTextChanged(Editable s) {
                            Map<String,String> row = data.get(position); // get the item from your ArrayList
                            row.put("QUANTITY", holder.medQt.getText().toString());// update your HashMap
                            data.set(position, row); // update your data ( your ArrayList )
                        }
                        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
                        public void onTextChanged(CharSequence s, int start, int before, int count){}
                    });
                }

                break;

            default:
        }
        return vi;
    }

}

