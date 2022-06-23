package com.arthurbritto.methodik.view;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.arthurbritto.methodik.R;

/**
 * This class displays a screen where the user add a new Task.
 * The TaskActivityAdd returns the entered Panel to the calling activity
 * (TaskListActivity), which then stores the new Task and updates the list.
 */
public class TaskActivityAdd extends AppCompatActivity {

    public static final String EXTRA_REPLY_NEW_TASK = "extra_task_name";

    private EditText addTaskView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_add);

        addTaskView = findViewById(R.id.add_task);

        final Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String taskName = extras.getString(EXTRA_REPLY_NEW_TASK, "");
            if (!taskName.isEmpty()) {
                addTaskView.setText(taskName);
                addTaskView.setSelection(taskName.length());
                addTaskView.requestFocus();
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
                if (TextUtils.isEmpty(addTaskView.getText())) {
                    // No task was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Get the new task that the user entered.
                    String taskName = addTaskView.getText().toString();
                    // Put the new task in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY_NEW_TASK, taskName);
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}

