package adrean.thesis.puocc.Fragment;

public class Model {

    private String category;
    private String id;

    public Model(String category,String id){
        this.category = category;
        this.id = id;
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
