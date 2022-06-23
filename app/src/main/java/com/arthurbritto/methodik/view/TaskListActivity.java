package com.arthurbritto.methodik.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.arthurbritto.methodik.R;
import com.arthurbritto.methodik.model.Task;
import com.arthurbritto.methodik.model.TaskRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import static com.arthurbritto.methodik.view.MainActivity.DEFAULT_ID;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_PANEL_ID;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_PANEL_NAME;

public class TaskListActivity extends AppCompatActivity {

    public static final int NEW_TASK_ACTIVITY_REQUEST_CODE = 1;
    public static final int UPDATE_TASK_ACTIVITY_REQUEST_CODE = 2;

    public static final String EXTRA_TASK_NAME = "extra_task_name";
    public static final String EXTRA_TASK_ID = "extra_task_id";
    public static final String EXTRA_COLOR = "extra_color";

    private TaskViewModel taskViewModel;
    private int extraPanelId;
    private TaskAdapter adapter;
    private TextView panelName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_list);

        int extraPanelIdIntent = getIntent().getIntExtra(EXTRA_PANEL_ID, DEFAULT_ID);
        this.extraPanelId = extraPanelIdIntent;
        String extraPanelName = getIntent().getStringExtra(EXTRA_PANEL_NAME);
        panelName = (TextView) findViewById(R.id.textViewPanelName);
        panelName.setText(extraPanelName);

        // Set up the RecyclerView.
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        final TaskAdapter adapter = new TaskAdapter(this);
        this.adapter = adapter;
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up the PanelViewModel.
        taskViewModel = ViewModelProviders.of(this).get(TaskViewModel.class);
        // Get all the tasks from the database
        // and associate them to the adapter.
        taskViewModel.getTasksByPanel(extraPanelId, new TaskRepository.GetTasksResult() {
            @Override
            public void onTasksLoaded(List<Task> tasks) {
                // update the cached copy of the tasks in the adapter.
                adapter.updateTasks(tasks);
            }
        });

        // Floating action button setup
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(TaskListActivity.this, TaskActivityAdd.class);
                startActivityForResult(intent, NEW_TASK_ACTIVITY_REQUEST_CODE);
            }
        });

        // Add the functionality to swipe items in the
        // RecyclerView to delete the swiped item.
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
                    // When the use swipes a task,
                    // delete that task from the database.
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        int position = viewHolder.getAdapterPosition();
                        Task myTask = adapter.getTaskAtPosition(position);
                        Toast.makeText(TaskListActivity.this,
                                getString(R.string.delete_preamble) + " " +
                                        myTask.getName(), Toast.LENGTH_LONG).show();

                        // Delete the word.
                        taskViewModel.deleteTask(myTask);
                    }
                });
        // Attach the item touch helper to the recycler view.
        helper.attachToRecyclerView(recyclerView);

        adapter.setOnItemClickListener(new TaskAdapter.ClickListener() {
            @Override
            public void onItemLongClick(View v, int position) {
                Task task = adapter.getTaskAtPosition(position);
                launchEditTaskActivity(task);
            }
        });
    }

    /**
     * When the user enters a new task in the TaskActivityAdd or
     * TaskActivityEdit, that activity returns the result to this activity.
     * If the user entered a new task, save it in the database.
     *
     * @param requestCode ID for the request
     * @param resultCode  indicates success or failure
     * @param data        The Intent sent back from the TaskActivityAdd,
     *                    which includes the task that the user entered
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == NEW_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            Task task = new Task(data.getStringExtra(EXTRA_TASK_NAME), extraPanelId);
            // Save the data
            taskViewModel.insert(task);
            taskViewModel.getTasksByPanel(extraPanelId, new TaskRepository.GetTasksResult() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    // update the cached copy of the tasks in the adapter.
                    adapter.updateTasks(tasks);
                }
            });
            Toast.makeText(this, R.string.add_new_task, Toast.LENGTH_LONG).show();

        } else if (requestCode == UPDATE_TASK_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            String taskNewName = data.getStringExtra(TaskActivityEdit.EXTRA_REPLY_TASK_EDITED);
            int taskId = data.getIntExtra(TaskActivityEdit.EXTRA_REPLY_TASK_ID, DEFAULT_ID);
            int colorId = data.getIntExtra(TaskActivityEdit.EXTRA_REPLY_VIEW_COLOR, DEFAULT_ID);
            taskViewModel.update(new Task(taskId, taskNewName, extraPanelId, colorId));
            // Save the data
            taskViewModel.getTasksByPanel(extraPanelId, new TaskRepository.GetTasksResult() {
                @Override
                public void onTasksLoaded(List<Task> tasks) {
                    // update the cached copy of the tasks in the adapter.
                    adapter.updateTasks(tasks);
                }
            });
            Toast.makeText(this, R.string.task_updated, Toast.LENGTH_LONG).show();
        }
    }

    public void launchEditTaskActivity(Task task) {
        Intent intent = new Intent(this, TaskActivityEdit.class);
        intent.putExtra(EXTRA_TASK_NAME, task.getName());
        intent.putExtra(EXTRA_TASK_ID, task.getId());
        intent.putExtra(EXTRA_COLOR, task.getColor());
        startActivityForResult(intent, UPDATE_TASK_ACTIVITY_REQUEST_CODE);
    }
}
