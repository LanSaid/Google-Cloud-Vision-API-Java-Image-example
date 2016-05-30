import java.util.ArrayList;

/**
 * Created by АленкаиВова on 30.05.2016.
 */

/**
 * An object for mapping images to objects
 */
public class ImageObject {
    private float x;
    private float y;
    private String imagePath;
    private ArrayList listLabel;

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList getListLabel() {
        return listLabel;
    }

    public void setListLabel(ArrayList listLabel) {
        this.listLabel = listLabel;
    }


}
