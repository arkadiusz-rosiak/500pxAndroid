package pl.rosiakit.px500.model;

import java.io.Serializable;
import java.util.NoSuchElementException;

import pl.rosiakit.px500.utils.PhotoCategory;

public class Photo implements Serializable{

    private final int id;
    private final String url;
    private final String name;
    private final String description;
    private final int votes;
    private final boolean isNSFW;

    private PhotoCategory category = PhotoCategory.UNCATEGORIZIED;

    private String camera = "";
    private String focalLength = "";
    private String iso = "";
    private String shutterSpeed = "";
    private String aperture = "";

    public Photo(int id, String url, String name, String description, int votes, boolean isNSFW) {
        this.id = id;
        this.url = url;
        this.name = parseInputString(name);
        this.description = parseInputString(description);
        this.votes = votes;
        this.isNSFW = isNSFW;
    }

    public Photo(int id, String url, String name, int votes, boolean isNSFW){
        this(id, url, name, "", votes, isNSFW);
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getVotes() {
        return votes;
    }

    public String getCamera() {
        return camera;
    }

    public void setCamera(String camera) {
        this.camera = parseInputString(camera);
    }

    public String getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(String focal_length) {
        this.focalLength = parseInputString(focal_length);
    }

    public String getIso() {
        return iso;
    }

    public void setIso(String iso) {
        this.iso = parseInputString(iso);
    }

    public String getShutterSpeed() {
        return shutterSpeed;
    }

    public void setShutterSpeed(String shutterSpeed) {
        this.shutterSpeed = parseInputString(shutterSpeed);
    }

    public String getAperture() {
        return aperture;
    }

    public void setAperture(String aperture) {
        this.aperture = parseInputString(aperture);
    }

    public String getCategoryName() {
        return category.toString();
    }

    public boolean isNSFW() {
        return isNSFW;
    }

    public void setCategory(int id) {
        try {
            this.category = PhotoCategory.getCategoryById(id);

        }catch (NoSuchElementException e){
            this.category = PhotoCategory.UNCATEGORIZIED;
        }
    }

    private String parseInputString(String text){

        if(isStringEmpty(text)){
            return "-";
        }

        return text;
    }

    private boolean isStringEmpty(String text) {
        return text == null || text.equalsIgnoreCase("null") || text.equals("");
    }

    @Override
    public String toString() {
        return "Photo{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", votes=" + votes +
                '}';
    }
}
