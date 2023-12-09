package rutgers.cs213androidproject;

import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import model.Photo;

public class SlideshowActivity extends AppCompatActivity {


    public ImageButton previous, next;
    public ImageView imageView;
    public int currentindex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);


        previous = (ImageButton) findViewById(R.id.previous);
        next = (ImageButton) findViewById(R.id.next);
        imageView = (ImageView) findViewById(R.id.slideshowImage);


        final int end = MainActivity.session.getCurrentAlbum().getPhotos().size();

        currentindex = 0;

        openInitialImage();


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentindex++;

                if(currentindex == end){
                    currentindex = 0;
                }

                Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(currentindex);
                Uri uri = Uri.parse(photo.getFilepath());
                imageView.setImageURI(uri);
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentindex--;
                if(currentindex == -1){
                    currentindex = end-1;
                }
                Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(currentindex);
                Uri uri = Uri.parse(photo.getFilepath());
                imageView.setImageURI(uri);
            }
        });





    }

    public void openInitialImage(){
        Uri uri = Uri.parse(MainActivity.session.getCurrentAlbum().getPhotos().get(0).getFilepath());
        imageView.setImageURI(uri);
    }
}
