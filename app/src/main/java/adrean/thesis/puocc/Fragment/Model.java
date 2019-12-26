package adrean.thesis.puocc.Fragment;

import android.net.Uri;

public class Model {

    private Uri image;
    private String category;
    private String id;

    public Model(Uri image,String category,String id){
        this.image = image;
        this.category = category;
        this.id = id;
    }

    public Uri getImage() {
        return image;
    }

    public void setImage(Uri image) {
        this.image = image;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
