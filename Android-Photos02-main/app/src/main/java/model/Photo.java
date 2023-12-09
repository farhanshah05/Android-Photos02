package model;

import java.io.Serializable;
import java.util.ArrayList;

public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Path of photo
     */
    public String filepath;

    /**
     * Name of file
     */
    public String filename;

    /**
     * List of person tags
     */
    public ArrayList<String> personList;

    /**
     * List of location tags
     */
    public ArrayList<String> locationList;

    /**
     * Stores list of tags for the photo
     */
    public ArrayList<Tag> taglist;

    public Photo(String filepath){
        this.filepath = filepath;
        personList = new ArrayList<>();
        locationList = new ArrayList<>();
        taglist = new ArrayList<>();
    }

    public void addTag(String name, String value){
        taglist.add(new Tag(name, value));
    }

    public void removeTag(int index){
        taglist.remove(index);
    }

    public void removeTag(String name, String value){
        for(int i = 0; i < taglist.size(); i++) {
            Tag cur = taglist.get(i);
            if(cur.key.toLowerCase().equals(name.toLowerCase()) && cur.value.toLowerCase().equals(value.toLowerCase())) {
                taglist.remove(i);
            }
        }
    }

    public boolean tagExists(String name, String value) {
        for(int i = 0; i < taglist.size(); i++) {
            Tag cur = taglist.get(i);
            if(cur.key.toLowerCase().equals(name.toLowerCase()) && cur.value.toLowerCase().equals(value.toLowerCase())) {
                return true;
            }
        }
        return false;

    }


    public String getFilepath() {
        return filepath;
    }

    public String getFilename() {
        return filename;
    }

    public ArrayList<String> getPersonList() {
        return personList;
    }

    public ArrayList<String> getLocationList() {
        return locationList;
    }

    public ArrayList<Tag> getTaglist() {
        return taglist;
    }
}
