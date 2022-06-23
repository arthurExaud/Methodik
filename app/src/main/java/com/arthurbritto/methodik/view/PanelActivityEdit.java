package com.arthurbritto.methodik.view;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.arthurbritto.methodik.R;
import com.arthurbritto.methodik.databinding.ActivityMainBinding;
import com.arthurbritto.methodik.databinding.ActivityPanelEditBinding;
import com.arthurbritto.methodik.model.AlarmReceiver;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import java.util.Calendar;

import static com.arthurbritto.methodik.view.MainActivity.DEFAULT_ID;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_PANEL_ID;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_PANEL_NAME;
import static com.arthurbritto.methodik.view.MainActivity.EXTRA_COLOR;

/**
 * This class displays a screen where the user can edit a Panel.
 * The PanelActivityEdit returns the entered Panel to the calling activity
 * (MainActivity), which then stores the new Panel and updates the list.
 */
public class PanelActivityEdit extends AppCompatActivity {

    public static final String EXTRA_REPLY_PANEL_EDITED = "extra_reply_panel_edited";
    public static final String EXTRA_REPLY_PANEL_ID = "extra_reply_panel_id";
    public static final String EXTRA_REPLY_VIEW_COLOR = "extra_reply_view_color";
    
    private EditText editPanelView;
    private View editColorView;
    private int viewColor;

    private ActivityPanelEditBinding binding;
    private MaterialTimePicker picker;
    private Calendar calendar;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private int counter = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPanelEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        createNotificationChannel();

        editPanelView = findViewById(R.id.edit_panel_text);
        editColorView = findViewById(R.id.view_panel_color_change);

        final Bundle extras = getIntent().getExtras();

        // If we are passed content, fill it in for the user to edit.
        if (extras != null) {
            String panelName = extras.getString(EXTRA_PANEL_NAME, "");
            int buttonColor = extras.getInt(EXTRA_COLOR, DEFAULT_ID);
            if (!panelName.isEmpty()) {
                editPanelView.setText(panelName);
                editPanelView.setSelection(panelName.length());
                editPanelView.requestFocus();
            }
        } // Otherwise, start with empty fields.

        final Button button = findViewById(R.id.button_save);

        // When the user presses the Save button, create a new Intent for the reply.
        // The reply Intent will be sent back to the calling activity (in this case, MainActivity).
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a new Intent for the reply.
                Intent replyIntent = new Intent();
                if (TextUtils.isEmpty(editPanelView.getText())) {
                    // No panel was entered, set the result accordingly.
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    // Get the new panelName, color and panelId that the user entered.
                    String panelName = editPanelView.getText().toString();
                    int panelId = extras.getInt(EXTRA_PANEL_ID, DEFAULT_ID);
                    // Put the new panelName in the extras for the reply Intent.
                    replyIntent.putExtra(EXTRA_REPLY_PANEL_EDITED, panelName);
                    replyIntent.putExtra(EXTRA_REPLY_VIEW_COLOR, viewColor);
                    replyIntent.putExtra(EXTRA_REPLY_PANEL_ID, panelId);
                    // Set the result status to indicate success.
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });

        binding.scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        binding.switchOnAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(counter <= 0){
                    setAlarm();
                    counter++;
                }
                else {
                    cancelAlarm();
                    counter = 0;
                }
            }
        });
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);

        if (alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
        Toast.makeText(this, "Alarm Cancelled", Toast.LENGTH_SHORT).show();
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),pendingIntent);
        Toast.makeText(this, "Alarm set Successfully", Toast.LENGTH_SHORT).show();
    }

    private void showTimePicker() {

        picker = new MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(12)
                .setMinute(0)
                .setTitleText("Select Alarm Time")
                .build();

        picker.show(getSupportFragmentManager(), "ThisIsAnAlarm");

        picker.addOnPositiveButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                binding.alarmTextView.setText("Alarm set to: " + picker.getHour() + ":" + picker.getMinute());

                calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, picker.getHour());
                calendar.set(Calendar.MINUTE, picker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
        });

    }

    /**
     * Create a notification channel to be sent when the alarm in on
     * Check if the SDK version, only runs on android Oreo or above
     */
    public void createNotificationChannel() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence channelName = "ThisIsAnAlarmchannel";
            String description = "channel for the Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("ThisIsAnAlarm", channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

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
    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

}

