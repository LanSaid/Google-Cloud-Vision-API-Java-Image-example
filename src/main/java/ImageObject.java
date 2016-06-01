import com.google.api.services.vision.v1.model.EntityAnnotation;

import java.util.ArrayList;
import java.util.List;

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
    private List<EntityAnnotation> listLabel;
    private String Status;

    public ImageObject(String ImagePath, String Status, List<EntityAnnotation> listLabel) {
        this.imagePath = ImagePath;
        this.Status = Status;
        this.listLabel = listLabel;


    }

    public String getStatus() {
        return Status;
    }

    public List<EntityAnnotation> getListLabel() {
        return listLabel;
    }

    public void setListLabel(List<EntityAnnotation> listLabel) {
        this.listLabel = listLabel;
    }

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





}
