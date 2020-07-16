package adrean.thesis.puocc.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.util.List;

import adrean.thesis.puocc.CategoryDetailActivity;
import adrean.thesis.puocc.R;

public class Adapter extends PagerAdapter {

    private List<Model> models;
    private LayoutInflater layoutInflater;
    private Context context;

    public Adapter(List<Model> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.list_category, container, false);

        TextView category;
        ImageView imageCategory;

        category = view.findViewById(R.id.category);
        imageCategory = view.findViewById(R.id.image);

        Uri imgSource = models.get(position).getImage();
        category.setText(models.get(position).getCategory());
        try {
            imageCategory.setImageDrawable(Drawable.createFromStream(
                    context.getContentResolver().openInputStream(imgSource),
                    null));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CategoryDetailActivity.class);
                intent.putExtra("category", models.get(position).getCategory());
                intent.putExtra("id",models.get(position).getId());
                context.startActivity(intent);
                // finish();
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }
}
