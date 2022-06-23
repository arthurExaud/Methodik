package com.arthurbritto.methodik.model;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

/**
 * After the app creates the database, all further
 * interactions with it happen through the ViewModels.
 */
@Database(entities = {Panel.class, Task.class}, version = 5, exportSchema = false)
public abstract class MethodikRoomDatabase extends RoomDatabase {

    public abstract TaskDao taskDao();

    public abstract PanelDao panelDao();

    private static MethodikRoomDatabase INSTANCE;

    public static MethodikRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MethodikRoomDatabase.class) {
                if (INSTANCE == null) {
                    // Create database here.
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            MethodikRoomDatabase.class, "methodik_database")
                            // Wipes and rebuilds instead of migrating if no Migration object.
                            .fallbackToDestructiveMigration()
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    // This callback is called when the database has opened.
    // In this case, use PopulateDbAsync to populate the database
    // with the initial data set if the database has no entries.
    private static RoomDatabase.Callback sRoomDatabaseCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onOpen(@NonNull SupportSQLiteDatabase db) {
                    super.onOpen(db);
                    new PopulateDbAsync(INSTANCE).execute();
                }
            };

    // Populate the database with the initial data set
    // only if the database has no entries.
    private static class PopulateDbAsync extends AsyncTask<Void, Void, Void> {

        private final TaskDao taskDao;
        private final PanelDao panelDao;

        // Initial data set
        private static String[] panels = {"Week Meeting", "Week TODO", "Poems", "Hey You Stranger"};
        private static String[] weekTODOtasks = {"Exercises", "Study lots", "Clean the house", "Say Mom I love her", "100 Pushups, 100 Pullups, 10km"};
        private static String[] poemsTask = {"YOLO", "Liquid Times", "MoonLightShow", "Singing for the Stars"};

        PopulateDbAsync(MethodikRoomDatabase db) {
            taskDao = db.taskDao();
            panelDao = db.panelDao();
        }

        // If we have no task, then create the initial list of tasks.
        @Override
        protected Void doInBackground(final Void... params) {

            // Tasks in the second panel, Week TODO
            if (taskDao.getAllTasksFromPanel(2) == null) {
                for (int i = 0; i <= panels.length - 1; i++) {
                    Panel panel = new Panel(panels[i]);
                    panelDao.insert(panel);
                }

                int panelIdWeek = 2; // Tasks in the second panel, Week TODO
                for (int i = 0; i <= weekTODOtasks.length - 1; i++) {
                    Task task = new Task(weekTODOtasks[i], panelIdWeek);
                    taskDao.insert(task);
                }

                int panelIdPoems = 3; // Tasks in the third panel, Poems
                for (int i = 0; i <= poemsTask.length - 1; i++) {
                    Task task = new Task(poemsTask[i], panelIdPoems);
                    taskDao.insert(task);
                }
            }
            return null;
        }
    }
}
