package rutgers.cs213androidproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;

import model.Tag;
import model.User;

public class SingleImageActivity extends AppCompatActivity {

    public ImageView imageView;
    public FloatingActionButton floatingActionButton;
    public ListView listView;

    public ArrayList<String> taglist = new ArrayList<>();
    public ArrayAdapter<String> tagAdapter;

    /*
    Possible image slideshow
    https://www.youtube.com/watch?v=nL0k2usU7I8
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_image);
        //Load content
        update();
        //End Load content
        imageView = (ImageView) findViewById(R.id.singleImageView);
        floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton_tag);
        listView = (ListView) findViewById(R.id.taglistview);

        tagAdapter = new ArrayAdapter<>(this, R.layout.album_name_text, taglist);
        listView.setAdapter(tagAdapter);
        registerForContextMenu(listView);

        openImage();


        // Add tag to a photo
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            final PopupMenu popupMenu = new PopupMenu(SingleImageActivity.this, floatingActionButton);
            popupMenu.getMenuInflater().inflate(R.menu.tagkeys, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    switch (menuItem.getItemId()){
                        case R.id.personKey:
                            AlertDialog.Builder alert = new AlertDialog.Builder(SingleImageActivity.this);
                            alert.setTitle("Person Tag");
                            alert.setMessage("Please Enter a value for this tag");

                            final EditText input = new EditText(SingleImageActivity.this);
                            alert.setView(input);


                            alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = input.getText().toString();
                                    if(MainActivity.session.getCurrentAlbum().getCurrentPhoto().tagExists("Person", value)){
                                        Context context = getApplicationContext();
                                        CharSequence text = "Tag already exists. Try another tag.";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast.makeText(context, text, duration).show();
                                        return;
                                    }
                                    if(value.isEmpty()){
                                        Context context = getApplicationContext();
                                        CharSequence text = "Field cannot be blank";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast.makeText(context, text, duration).show();
                                        return;
                                    }
                                    MainActivity.session.getCurrentAlbum().getCurrentPhoto().addTag("Person",value);

                                    try {
                                        User.save(MainActivity.session);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    update();
                                    tagAdapter.notifyDataSetChanged();
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
                        case R.id.locationKey:
                            AlertDialog.Builder locationAlert = new AlertDialog.Builder(SingleImageActivity.this);
                            locationAlert.setTitle("Location Tag");
                            locationAlert.setMessage("Please Enter a value for this tag");

                            final EditText locationInput = new EditText(SingleImageActivity.this);
                            locationAlert.setView(locationInput);


                            locationAlert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String value = locationInput.getText().toString();
                                    if(MainActivity.session.getCurrentAlbum().getCurrentPhoto().tagExists("Location", value)){
                                        Context context = getApplicationContext();
                                        CharSequence text = "Tag already exists. Try another tag.";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast.makeText(context, text, duration).show();
                                        return;
                                    }
                                    if(value.isEmpty()){
                                        Context context = getApplicationContext();
                                        CharSequence text = "Field cannot be blank";
                                        int duration = Toast.LENGTH_SHORT;
                                        Toast.makeText(context, text, duration).show();
                                        return;
                                    }
//                                    Tag tag = new Tag(customSpinner.getSelectedItem().toString(), renamed);
                                    MainActivity.session.getCurrentAlbum().getCurrentPhoto().addTag("Location",value);

                                    try {
                                        User.save(MainActivity.session);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    update();
                                    tagAdapter.notifyDataSetChanged();
                                }
                            });


                            locationAlert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    dialog.cancel();
                                }
                            });
                            AlertDialog locationDialog = locationAlert.create();
                            locationDialog.show();
                            return true;

                    }
                    return true;
                }
            });

            popupMenu.show();


            }
        });



    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.tagoptions, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        final int pos = (int) info.id;
        switch (item.getItemId()){
            case R.id.deleteTag:
                ArrayList<Tag> tagArrayList = MainActivity.session.getCurrentAlbum().getCurrentPhoto().getTaglist();
                MainActivity.session.getCurrentAlbum().getCurrentPhoto().removeTag(tagArrayList.get(pos).key, tagArrayList.get(pos).value);
                try {
                    User.save(MainActivity.session);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                update();
                tagAdapter.notifyDataSetChanged();

                return true;
            case R.id.movePhoto:

                return true;
            default:
                return super.onContextItemSelected(item);
        }
    }

    public void openImage() {

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        if(bundle == null){
        }
        else{
            int index = intent.getIntExtra("index",-1);
            Uri uri = Uri.parse(MainActivity.session.getCurrentAlbum().getPhotos().get(index).getFilepath());
            imageView.setImageURI(uri);
        }
    }

    public void update(){
        taglist.clear();
        for(int i = 0; i < MainActivity.session.getCurrentAlbum().getCurrentPhoto().getTaglist().size(); i++){
            taglist.add(MainActivity.session.getCurrentAlbum().getCurrentPhoto().getTaglist().get(i).key + " | " + MainActivity.session.getCurrentAlbum().getCurrentPhoto().getTaglist().get(i).value);
        }
    }
}
