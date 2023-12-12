package rutgers.cs213androidproject;

import android.media.Image;
import android.net.Uri;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import model.Photo;

public class SlideshowActivity extends AppCompatActivity {

    private ImageButton previousButton;
    private ImageButton nextButton;
    private ImageView imageView;
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slideshow);

        previousButton = findViewById(R.id.previous);
        nextButton = findViewById(R.id.next);
        imageView = findViewById(R.id.slideshowImage);

        final int end = MainActivity.session.getCurrentAlbum().getPhotos().size();

        currentIndex = 0;

        openImageAtIndex(currentIndex);

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex = (currentIndex + 1) % end;
                openImageAtIndex(currentIndex);
            }
        });

        previousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentIndex = (currentIndex - 1 + end) % end;
                openImageAtIndex(currentIndex);
            }
        });
    }

    private void openImageAtIndex(int index) {
        Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(index);
        Uri uri = Uri.parse(photo.getFilepath());
        imageView.setImageURI(uri);
    }
}
