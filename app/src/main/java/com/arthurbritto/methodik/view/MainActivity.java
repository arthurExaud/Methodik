package com.arthurbritto.methodik.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurbritto.methodik.R;
import com.arthurbritto.methodik.model.Panel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int NEW_PANEL_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_PANEL_ACTIVITY_REQUEST_CODE = 2;
    public static final int SHOW_TASK_LIST_ACTIVITY_REQUEST_CODE = 3;

    public static final int DEFAULT_ID = -1;

    public static final String EXTRA_PANEL_NAME = "extra_panel_name";
    public static final String EXTRA_PANEL_ID = "extra_panel_id";
    public static final String EXTRA_COLOR = "extra_color";

    private PanelViewModel panelViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Set up the RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final PanelAdapter adapter = new PanelAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the PanelViewModel.
        panelViewModel = ViewModelProviders.of(this).get(PanelViewModel.class);
        // Get all the lists from the database and associate them to the adapter.
        panelViewModel.getAllLists().observe(this, new Observer<List<Panel>>() {
            @Override
            public void onChanged(List<Panel> panels) {
                // Update the cached copy of the panels in the adapter.
                adapter.updatePanels(panels);
            }
        });

        // Floating action button setup
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, PanelActivityAdd.class);
                MainActivity.this.startActivityForResult(intent, NEW_PANEL_ACTIVITY_REQUEST_CODE);
            }
        });

        // Add the functionality to swipe items in the RecyclerView to delete the swiped item.
        ItemTouchHelper helper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0,
                        ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
                    @Override
                    // We are not implementing onMove() in this app.
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    // When the use swipes a panel,
                    // delete that panel from the database.
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Panel myPanel = adapter.getPanelAtPosition(position);
                        Toast.makeText(MainActivity.this,
                                getString(R.string.delete_preamble) + " " +
                                        myPanel.getName(), Toast.LENGTH_LONG).show();

                        // Delete the word.
                        panelViewModel.deletePanel(myPanel);
                    }
                });
        // Attach the item touch helper to the recycler view.
        helper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new PanelAdapter.ClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                Panel panel = adapter.getPanelAtPosition(position);
                launchTaskListActivity(panel);

            }

            @Override
            public void onItemLongClick(View v, int position) {
                Panel panel = adapter.getPanelAtPosition(position);
                launchPanelActivity(panel);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // The options menu has a single item "Clear all data now"
    // that deletes all the entries in the database.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, as long
        // as you specify a parent activity in AndroidManifest.xml.
        if (item.getItemId() == R.id.clear_data) {
            Toast.makeText(this, R.string.clear_data_toast_text, Toast.LENGTH_LONG).show();

            // Delete the existing data.
            panelViewModel.deleteAll();
            return true;
        } else return super.onOptionsItemSelected(item);
    }

    /**
     * When the user enters a new panel in the PanelActivityAdd,
     * that activity returns the result to this activity.
     * If the user entered a new panel, save it in the database.
     *
     * @param requestCode ID for the request
     * @param resultCode  indicates success or failure
     * @param data        The Intent sent back from the panelActivityAdd,
     *                    which includes the panel that the user entered
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_PANEL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Panel panel = new Panel(data.getStringExtra(PanelActivityAdd.EXTRA_REPLY_NEW_PANEL));
            // Save the data.
            panelViewModel.insert(panel);
            Toast.makeText(this, R.string.panel_created, Toast.LENGTH_LONG).show();
        } else if (requestCode == UPDATE_PANEL_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String panelNewName = data.getStringExtra(PanelActivityEdit.EXTRA_REPLY_PANEL_EDITED);
            int id = data.getIntExtra(PanelActivityEdit.EXTRA_REPLY_PANEL_ID, DEFAULT_ID);
            int colorId = data.getIntExtra(PanelActivityEdit.EXTRA_REPLY_COLOR_VIEW, DEFAULT_ID);
            panelViewModel.update(new Panel(id, panelNewName, colorId));
            Toast.makeText(this, R.string.panel_updated, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Handles the onItemLongClick and send to the edition page.
     * in the app, always when you hold a task or panel you edit them.
     */
    public void launchTaskListActivity(Panel panel) {
        Intent intent = new Intent(this, TaskListActivity.class);
        intent.putExtra(EXTRA_PANEL_NAME, panel.getName());
        intent.putExtra(EXTRA_PANEL_ID, panel.getId());
        startActivityForResult(intent, SHOW_TASK_LIST_ACTIVITY_REQUEST_CODE);
    }

    /**
     * Handles the onItemClick and send to the the panel task List page.
     */
    public void launchPanelActivity(Panel panel) {
        Intent intent = new Intent(this, PanelActivityEdit.class);
        intent.putExtra(EXTRA_PANEL_NAME, panel.getName());
        intent.putExtra(EXTRA_PANEL_ID, panel.getId());
        intent.putExtra(EXTRA_COLOR, panel.getColor());
        startActivityForResult(intent, UPDATE_PANEL_ACTIVITY_REQUEST_CODE);
    }
}
