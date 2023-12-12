package model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;

public class User implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * List of albums for each user
     */
    public ArrayList<Album> albums;

    /**
     * Current user
     */
    public Album currentAlbum;

    /**
     * Number of albums a user has
     */
    public int numberOfAlbums;


    public User(){
        albums = new ArrayList<Album>();
        this.currentAlbum = null;
        numberOfAlbums = 0;
    }


    public void addAlbum(Album name){
        albums.add(name);
        numberOfAlbums++;
    }

    public void deleteAlbum(int index){
        albums.remove(index);
        numberOfAlbums--;
    }

    public boolean albumExists(String albumname){
        for(Album album : albums){
            if(album.getAlbumName().equals(albumname)){
                return true;
            }
        }
        return false;
    }

    //Search All
    public ArrayList<Photo> searchAllTags(String tagvalue){
        ArrayList<Photo> photolist = new ArrayList<Photo>();
        //Used to make sure no duplicates
        HashSet<Photo> check = new HashSet<Photo>();
        for(Album album : albums) {
            for(Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTaglist()){
                    System.out.println(tagvalue);
                    System.out.println(tag.value.toLowerCase().contains(tagvalue.toLowerCase()));
                    if(tag.value.toLowerCase().contains(tagvalue.toLowerCase())){
                        check.add(photo);
                    }
                }
            }

        }

        photolist.addAll(check);
        return photolist;
    }

    public ArrayList<Photo> searchCertainTags(String tagkey, String tagvalue){
        ArrayList<Photo> photolist = new ArrayList<Photo>();
        //Used to make sure no duplicates
        HashSet<Photo> check = new HashSet<Photo>();
        for(Album album : albums) {
            for(Photo photo : album.getPhotos()) {
                for (Tag tag : photo.getTaglist()){

                    System.out.println(tagvalue);
                    System.out.println(tag.value.toLowerCase().contains(tagvalue.toLowerCase()));
                    if(tag.value.toLowerCase().contains(tagvalue.toLowerCase()) && tag.key.toLowerCase().contains(tagkey.toLowerCase())){
                        check.add(photo);
                    }
                }
            }

        }

        photolist.addAll(check);
        return photolist;
    }

    public ArrayList<Album> getAlbums() {
        return albums;
    }


    public Album getCurrentAlbum() {
        return currentAlbum;
    }

    public void setCurrentAlbum(Album currentAlbum) {
        this.currentAlbum = currentAlbum;
    }

    public int getNumberOfAlbums() {
        return numberOfAlbums;
    }

    public static void save(User pdApp) throws IOException {
        ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("/data/data/rutgers.cs213androidproject/files/data.dat"));
        oos.writeObject(pdApp);
        oos.close();
    }

    public static User load() throws IOException, ClassNotFoundException {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("/data/data/rutgers.cs213androidproject/files/data.dat"));
        User userList = (User) ois.readObject();
        ois.close();
        return userList;

    }

}
