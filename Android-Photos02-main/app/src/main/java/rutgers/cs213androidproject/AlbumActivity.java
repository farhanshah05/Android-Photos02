package rutgers.cs213androidproject;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;
import java.util.ArrayList;

import model.AlbumImageAdapter;
import model.Photo;
import model.User;

public class AlbumActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 0;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private GridView gridView;
    private AlbumImageAdapter albumImageAdapter;
    private int pos;

    private final ActivityResultLauncher<Intent> getContentLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                handleContentResult(data.getData());
                            }
                        }
                    });
    private void handleContentResult(Uri result) {
        try {
            Bitmap photo = MediaStore.Images.Media.getBitmap(getContentResolver(), result);
            // Assuming getCurrentAlbum() and addPhoto() methods handle the null checks
            // Proceed with photo selection
            MainActivity.session.getCurrentAlbum().addPhoto(result.toString());

            // Make sure to update UI on the main thread
            runOnUiThread(() -> {
                gridView = findViewById(R.id.gridview_album);
                update();
                albumImageAdapter.notifyDataSetChanged();
                gridView.setAdapter(albumImageAdapter);
            });

            try {
                User.save(MainActivity.session);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        gridView = findViewById(R.id.gridview_album);
        update();
        albumImageAdapter.notifyDataSetChanged();
        gridView.setAdapter(albumImageAdapter);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album);
        System.out.println("here");

        gridView = findViewById(R.id.gridview_album);
        albumImageAdapter = new AlbumImageAdapter(AlbumActivity.this, photoList);
        gridView.setAdapter(albumImageAdapter);
        registerForContextMenu(gridView);

        long albumId = getIntent().getLongExtra("ALBUM_ID", -1);
        String albumName = getIntent().getStringExtra("ALBUM_NAME");

        updateUI();

        findViewById(R.id.floatingActionButton_album).setOnClickListener(view -> {
            // Directly launch the image picker
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            getContentLauncher.launch(intent);
        });


        gridView.setOnItemClickListener((adapterView, view, index, l) -> {
            Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(index);
            MainActivity.session.getCurrentAlbum().setCurrentPhoto(photo);
            Intent viewFullImage = new Intent(AlbumActivity.this, SingleImageActivity.class);
            viewFullImage.putExtra("index", index);
            startActivity(viewFullImage);
        });
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, android.view.View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.photooptions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        pos = info.position;

        switch (item.getItemId()) {
            case R.id.deletePhoto:
                MainActivity.session.getCurrentAlbum().deletePhoto(pos);
                try {
                    User.save(MainActivity.session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                gridView = findViewById(R.id.gridview_album);
                update();
                albumImageAdapter.notifyDataSetChanged();
                gridView.invalidateViews();
                gridView.setAdapter(albumImageAdapter);
                Toast.makeText(getApplicationContext(), "Photo Deleted", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.movePhoto:
                final PopupMenu popupMenu = new PopupMenu(AlbumActivity.this, gridView);
                for (int i = 0; i < MainActivity.session.getAlbums().size(); i++) {
                    popupMenu.getMenu().add(Menu.NONE, i, Menu.NONE, MainActivity.session.getAlbums().get(i).albumName);
                }

                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    Photo photo = MainActivity.session.getCurrentAlbum().getPhotos().get(pos);
                    MainActivity.session.getAlbums().get(menuItem.getItemId()).addPhoto(photo);
                    MainActivity.session.getCurrentAlbum().deletePhoto(pos);

                    try {
                        User.save(MainActivity.session);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    gridView = findViewById(R.id.gridview_album);
                    update();
                    albumImageAdapter.notifyDataSetChanged();
                    gridView.invalidateViews();
                    gridView.setAdapter(albumImageAdapter);

                    return true;
                });
                popupMenu.show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void update() {
        photoList.clear();
        photoList.addAll(MainActivity.session.getCurrentAlbum().getPhotos());
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.album_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.slideshow) {
            if (MainActivity.session.getCurrentAlbum().getPhotos().size() == 0) {
                AlertDialog alertDialog = new AlertDialog.Builder(AlbumActivity.this).create();
                alertDialog.setTitle("Empty Album");
                alertDialog.setMessage("Add a photo to an album to view a slideshow");
                alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                        (dialog, which) -> dialog.dismiss());
                alertDialog.show();
            } else {
                Intent goToSlideshow = new Intent(AlbumActivity.this, SlideshowActivity.class);
                startActivity(goToSlideshow);
            }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == this.REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("Permission", "Permission granted");
                // Permission granted, proceed with launching the image picker
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                getContentLauncher.launch(intent);
            } else {
                Log.d("Permission", "Permission denied");
                // Permission denied with rationale, do nothing
            }
        }
    }


    private void showPermissionRationale() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("This app requires access to your photos to function properly.")
                .setPositiveButton("OK", (dialog, which) -> {
                    // Request permission again
                    ActivityCompat.requestPermissions(AlbumActivity.this,
                            new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE},
                            REQUEST_CODE);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .setNeutralButton("App Settings", (dialog, which) -> openAppSettings()) // Add this line
                .show();
    }


    private void showAppSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permission Required")
                .setMessage("To use this feature, you need to grant storage permission. Go to app settings?")
                .setPositiveButton("Yes", (dialog, which) -> openAppSettings())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void openAppSettings() {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        // Check if there is an activity to handle the intent
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            // Handle the case where there is no activity to handle the intent
            Log.e("AppSettings", "No activity to handle app settings intent");
            Toast.makeText(this, "Unable to open app settings", Toast.LENGTH_SHORT).show();
        }
    }
}