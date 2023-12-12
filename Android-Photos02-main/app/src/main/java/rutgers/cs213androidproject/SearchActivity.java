package rutgers.cs213androidproject;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;
import model.AlbumImageAdapter;
import model.CustomSpinner;
import model.Photo;
import model.Tag;

import java.util.ArrayList;

public class SearchActivity extends AppCompatActivity {

    private ImageButton search;
    private CustomSpinner customSpinner;
    private GridView gridView;
    private SearchView searchView;
    private ArrayList<Photo> photoList = new ArrayList<>();
    private AlbumImageAdapter albumImageAdapter;
    private String[] spinnerOptions = {"Person", "Location", "Search All"};
    private int option;
    private ArrayList<Tag> tagArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);

        customSpinner = findViewById(R.id.tagspinner);
        gridView = findViewById(R.id.gridview_search);
        searchView = findViewById(R.id.searchField);
        albumImageAdapter = new AlbumImageAdapter(SearchActivity.this, photoList);
        gridView.setAdapter(albumImageAdapter);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getApplicationContext(),
                android.R.layout.simple_spinner_item, spinnerOptions);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        customSpinner.setAdapter(spinnerAdapter);

        customSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                option = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                handleSearch(s);
                return true;
            }
        });
    }

    private void handleSearch(String query) {
        photoList.clear();
        switch (option) {
            case 0:
            case 1:
                photoList.addAll(MainActivity.session.searchCertainTags(spinnerOptions[option], query));
                break;
            case 2:
                photoList.addAll(MainActivity.session.searchAllTags(query));
                break;
        }
        albumImageAdapter.notifyDataSetChanged();
    }
}