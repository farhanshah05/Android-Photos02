package rutgers.cs213androidproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.net.Uri;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import model.Album;
import model.AlbumImageAdapter;
import model.User;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    public final Context pub_context = this;

    public FloatingActionButton fab;

    public ArrayList<String> albumnames = new ArrayList<String>();
    public ArrayAdapter adapter;
    public ListView listview;
    public File filename = new File("/data/data/rutgers.cs213androidproject/files/data.dat");
    public int pos;
    public static User session = new User();

    private static final int PICK_DOCUMENT_REQUEST_CODE = 123;

    private ActivityResultLauncher<Intent> pickDocumentLauncher;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    private GridView gridView;





    @Override
    protected void onCreate(Bundle savedInstanceState) {


        try {
            session = User.load();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!filename.exists()){
            Context context = this;
            File file = new File(context.getFilesDir(), "data.dat");
            try {
                file.createNewFile();
            }
            catch (IOException e){

            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted, request it
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
        } else {
            // Permission is already granted, proceed with document selection
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");  // Adjust the MIME type as per your file type
            pickDocumentLauncher.launch(intent);
        }
        update();

        // Link XML values to controller
        listview = (ListView) findViewById(R.id.listview);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);

        //Listview Stuff
        adapter = new ArrayAdapter<String>(this, R.layout.album_name_text, albumnames);

        listview.setAdapter(adapter);
        registerForContextMenu(listview);

        //Button functions
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                addAlbum(v);
            }
        });
        // Initialize the ActivityResultLauncher
        pickDocumentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Uri imageUri = result.getData().getData();
                        getContentResolver().takePersistableUriPermission(
                                imageUri,
                                Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        );
                        // Perform the necessary operations with the document URI
                    }
                }
        );



        //When Clicked should redirect you to another activity with that albums photos
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Album currentAlbum = session.getAlbums().get(i);
                session.setCurrentAlbum(currentAlbum);

                // Start AlbumActivity directly
                Intent intent = new Intent(MainActivity.this, AlbumActivity.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.albumoptions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//        final int pos = (int) info.id;
        pos = info.position;
        System.out.println("======================" + pos);
        switch (item.getItemId()){
            case R.id.renameAlbum:
                AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
                alert.setTitle("Rename");
                alert.setMessage("Rename me");

                final EditText input = new EditText(MainActivity.this);
                alert.setView(input);


                alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        String renamed = input.getText().toString();
                        if(session.albumExists(renamed)){
                            Context context = getApplicationContext();
                            CharSequence text = "Album already exists. Try another name.";
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, text, duration).show();
                            return;
                        }
                        if(renamed.isEmpty()){
                            Context context = getApplicationContext();
                            CharSequence text = "Field cannot be blank";
                            int duration = Toast.LENGTH_SHORT;
                            Toast.makeText(context, text, duration).show();
                            return;
                        }
                        session.getAlbums().get(pos).setAlbumName(renamed);

                        try {
                            User.save(session);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        update();
                        adapter.notifyDataSetChanged();
                    }
                });


                alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
                return true;
            case R.id.deleteAlbum:
                session.deleteAlbum(pos);
                try {
                    User.save(session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                update();
                adapter.notifyDataSetChanged();
                Toast.makeText(getApplicationContext(), "Album Deleted", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search_icon) {
            Intent goToSearch = new Intent(MainActivity.this, SearchActivity.class);
            startActivity(goToSearch);
//            Toast.makeText(getApplicationContext(), "Onto Search", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause(){
        super.onPause();

        try {
            User.save(session);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        try {
            session = User.load();
            adapter.notifyDataSetChanged();
            listview.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        try {
            session = User.load();
            adapter.notifyDataSetChanged();
            listview.setAdapter(adapter);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void addAlbum(View view){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Enter the name of the album: ");

        final EditText input = new EditText(this);
        alertDialogBuilder.setView(input);
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String albumname = input.getText().toString().trim();
                if(albumname.isEmpty() || albumname == null){
                    Context context = getApplicationContext();
                    CharSequence text = "Field cannot be blank";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                    return;
                }

                if(session.albumExists(albumname)){
                    Context context = getApplicationContext();
                    CharSequence text = "Album already exists!";
                    int duration = Toast.LENGTH_SHORT;
                    Toast.makeText(context, text, duration).show();
                    return;
                }

                try {
                    User.save(session);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Create the album without accessing the DownloadStorageProvider
                Album album = new Album(albumname);
                session.addAlbum(album);

                try {
                    User.save(session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                update();
                adapter.notifyDataSetChanged();
            }
        });
        alertDialogBuilder.show();
    }

    public void update(){
        albumnames.clear();
        for(int i=0; i <session.getAlbums().size(); i++){
            String albumname = session.getAlbums().get(i).getAlbumName();
            albumnames.add(albumname);
        }
//        adapter.notifyDataSetChanged();
    }
}