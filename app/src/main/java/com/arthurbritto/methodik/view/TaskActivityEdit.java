package com.arthurbritto.methodik.view;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.arthurbritto.methodik.R;

import static com.arthurbritto.methodik.view.MainActivity.DEFAULT_ID;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_COLOR;
import static com.arthurbritto.methodik.view.TaskListActivity.EXTRA_TASK_ID;
import static com.arthurbritto.methodik.view.TaskListActivity.EXTRA_TASK_NAME;

/**
 * This class displays a screen where the user edit a task.
 * The TaskActivityEdit returns the entered Task to the calling activity
 * (TaskListActivity), which then stores the task and updating the list.
 */
public class TaskActivityEdit extends AppCompatActivity {

    public static final String EXTRA_REPLY_TASK_EDITED = "extra_reply_task_edited";
    public static final String EXTRA_REPLY_TASK_ID = "extra_reply_task_id";
    public static final String EXTRA_REPLY_VIEW_COLOR = "extra_reply_view_color";

    private EditText editTaskView;
    private View editColorView;
    private int viewColor;
    private int taskId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_edit);

        editTaskView = findViewById(R.id.edit_task_text);
        editColorView = findViewById(R.id.view_task_color_change);

        final Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String taskName = extras.getString(EXTRA_TASK_NAME, "");
            int taskId = extras.getInt(EXTRA_TASK_ID, DEFAULT_ID);
            int buttonColor = extras.getInt(EXTRA_COLOR, DEFAULT_ID);
            this.taskId = taskId;
            if (!taskName.isEmpty()) {
                editTaskView.setText(taskName);
                editTaskView.setSelection(taskName.length());
                editTaskView.requestFocus();
            }
        } // Otherwise, start with empty fields.

        final Button button = findViewById(R.id.button_save);

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, TaskListActivity).
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new Intent for the reply.
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editTaskView.getText())) {
                    // No task was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Get the new task that the user entered.
                    String taskName = editTaskView.getText().toString();
                    // Put the new task in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY_TASK_EDITED, taskName);
                    replyIntent.putExtra(EXTRA_REPLY_VIEW_COLOR, viewColor);
                    replyIntent.putExtra(EXTRA_REPLY_TASK_ID, taskId);
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }

    /**
     * Handles the onClick for the background color buttons. Gets the background
     * color of the button that was clicked, and sets the button on the
     * Edit text to that color.
     */
    public void changeBackground(View view) {
        Drawable color = view.getBackground();
        editColorView.setBackground(color);
        viewColor = Integer.parseInt((String) view.getTag());
    }
}

