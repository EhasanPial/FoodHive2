package Model;

public class CategoryModel {

    private String catname, catimage ;

    public CategoryModel() {
    }

    public CategoryModel(String catname, String catimage) {
        this.catname = catname;
        this.catimage = catimage;
    }

    public String getCatname() {
        return catname;
    }

    public void setCatname(String catname) {
        this.catname = catname;
    }

    public String getCatimage() {
        return catimage;
    }

    public void setCatimage(String catimage) {
        this.catimage = catimage;
    }
}
