package rutgers.cs213androidproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import model.AlbumImageAdapter;
import model.Photo;
import model.User;

public class AlbumActivity extends AppCompatActivity {

    /*
    Links:
        Adding a photo
        https://www.youtube.com/watch?v=6beVvhCFiKA
     */

    public final int REQUEST_CODE = 0;
    public ArrayList<Photo> photoList = new ArrayList<>();
    public FloatingActionButton fab;
    public GridView gridView;
    public AlbumImageAdapter albumImageAdapter;
    public int pos;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);


        gridView = (GridView) findViewById(R.id.gridview_album);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_album);
        albumImageAdapter = new AlbumImageAdapter(AlbumActivity.this, photoList);
        gridView.setAdapter(albumImageAdapter);
        registerForContextMenu(gridView);

        update();

        // Add a photo
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent addPhoto = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                addPhoto.addCategory(Intent.CATEGORY_OPENABLE);
                addPhoto.setType("image/*");
                startActivityForResult(addPhoto, REQUEST_CODE);

            }
        });


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int index, long l) {
                Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(index);
                MainActivity.session.getCurrentAlbum().setCurrentPhoto(photo);
                Intent viewFullImage = new Intent(AlbumActivity.this, SingleImageActivity.class);
                viewFullImage.putExtra("index", index);
                startActivity(viewFullImage);
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.photooptions, menu);
    }

    @Override
    public boolean onContextItemSelected(final MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        final int pos = (int) info.id;
        pos = info.position;
        System.out.println("===============================================" + pos);

        switch (item.getItemId()){
            case R.id.deletePhoto:
                MainActivity.session.getCurrentAlbum().deletePhoto(pos);
                try {
                    User.save(MainActivity.session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gridView = (GridView) findViewById(R.id.gridview_album);
                update();
                albumImageAdapter.notifyDataSetChanged();
                gridView.invalidateViews();
                gridView.setAdapter(albumImageAdapter);
                Toast.makeText(getApplicationContext(), "Photo Deleted", Toast.LENGTH_SHORT).show();

                return true;
            case R.id.movePhoto:
                final PopupMenu popupMenu = new PopupMenu(AlbumActivity.this, gridView);
//                popupMenu.getMenuInflater().inflate(R.menu.tagkeys, popupMenu.getMenu());
                for(int i = 0; i < MainActivity.session.getAlbums().size(); i++){
                    popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE,MainActivity.session.getAlbums().get(i).albumName);
                }


                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
//                        Toast.makeText(AlbumActivity.this, menuItem.getItemId(), Toast.LENGTH_SHORT);
                        //Gets index of new album
                        System.out.println(menuItem.getItemId());


                        Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(pos);
                        MainActivity.session.getAlbums().get(menuItem.getItemId()).addPhoto(photo);
                        MainActivity.session.getCurrentAlbum().deletePhoto(pos);

                        try {
                            User.save(MainActivity.session);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        gridView = (GridView) findViewById(R.id.gridview_album);
                        update();
                        albumImageAdapter.notifyDataSetChanged();
                        gridView.invalidateViews();
                        gridView.setAdapter(albumImageAdapter);

                        return true;
                    }
                });
                popupMenu.show();

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && data != null){
            Uri uri = null;
            if(data != null){
                uri = data.getData();
            }
            String photopath = uri.toString();
            MainActivity.session.getCurrentAlbum().addPhoto(photopath);

            try {
                User.save(MainActivity.session);
            } catch (IOException e) {
                e.printStackTrace();
            }
            gridView = (GridView) findViewById(R.id.gridview_album);
            update();

            albumImageAdapter.notifyDataSetChanged();
            gridView.setAdapter(albumImageAdapter);
        }
        else{

        }
    }

    public void update(){
        photoList.clear();
        photoList.addAll(MainActivity.session.getCurrentAlbum().getPhotos());
//        for(int i = 0; i < MainActivity.session.getCurrentAlbum().getPhotos().size(); i++){
//            photoList.add(MainActivity.session.getCurrentAlbum().getPhotos().get(i));
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.album_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.slideshow) {
            if(MainActivity.session.getCurrentAlbum().getPhotos().size() == 0){
                AlertDialog alertDialog = new AlertDialog.Builder(AlbumActivity.this).create();
                alertDialog.setTitle("Empty Album");
                alertDialog.setMessage("Add a photo to an album to view a slideshow");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
            else {
                Intent goToSlideshow = new Intent(AlbumActivity.this, SlideshowActivity.class);
                startActivity(goToSlideshow);
//                Toast.makeText(getApplicationContext(), "Going to Slideshow", Toast.LENGTH_SHORT).show();
            }
        }
        return super.onOptionsItemSelected(item);
    }
}