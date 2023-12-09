package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Album implements Serializable {

    private static final long serialVersionUID = 1L;

    public String albumName;
    public List<Photo> photos;
    public Photo currentPhoto;

    public Album(String albumName) {
        this.albumName = albumName;
        this.photos = new ArrayList<>();
    }

    public void addPhoto(String filepath) {
        Photo photo = new Photo(filepath);
        photos.add(photo);
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public void deletePhoto(int index) {
        photos.remove(index);
    }

    public String getAlbumName() {
        return albumName;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public Photo getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentPhoto(Photo currentPhoto) {
        this.currentPhoto = currentPhoto;
    }

    public int getNumberOfPhotos() {
        return photos.size();
    }
}
