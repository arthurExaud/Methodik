package com.arthurbritto.methodik.model;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import java.util.List;

/**
 * This class holds the implementation code for the methods that interact with the database.
 * Using a repository allows us to group the implementation methods together, and allows the
 * TaskViewModel to be a clean interface between the rest of the app and the database.
 * <p>
 * For insert, update and delete, and longer-running queries,
 * you must run the database interaction methods in the background.
 * <p>
 * Typically, all you need to do to implement a database method
 * is to call it on the data access object (DAO), in the background if applicable.
 */
public class TaskRepository {

    private TaskDao taskDao;

    public TaskRepository(Application application) {
        MethodikRoomDatabase db = MethodikRoomDatabase.getDatabase(application);
        taskDao = db.taskDao();
    }

    public void insert(Task task) {
        new insertAsyncTask(taskDao).execute(task);
    }

    public void update(Task task) {
        new updateTaskAsyncTask(taskDao).execute(task);
    }

    public void deleteTask(Task task) {
        new deleteTaskAsyncTask(taskDao).execute(task);
    }

    public void getTasksByPanel(int panelId, GetTasksResult callback) {
        new GetTasksByPanelAsyncTask(taskDao, callback).execute(panelId);
    }

    /**
     * Inserts a new task into the database.
     */
    // Static inner classes below here to run database interactions in the background.
    private static class GetTasksByPanelAsyncTask extends AsyncTask<Integer, Void, List<Task>> {

        private TaskDao asyncTaskDao;
        private GetTasksResult asyncCallback;

        GetTasksByPanelAsyncTask(TaskDao dao, GetTasksResult callback) {
            asyncTaskDao = dao;
            asyncCallback = callback;
        }

        @Override
        protected List<Task> doInBackground(final Integer... params) {
            return asyncTaskDao.getAllTasksFromPanel(params[0]);
        }

        @Override
        protected void onPostExecute(List<Task> tasks) {
            asyncCallback.onTasksLoaded(tasks);
        }
    }

    public interface GetTasksResult {
        void onTasksLoaded(List<Task> tasks);
    }

    /**
     * Inserts a new task into the database.
     */
    private static class insertAsyncTask extends AsyncTask<Task, Void, Void> {

        private TaskDao asyncTaskDao;

        insertAsyncTask(TaskDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            asyncTaskDao.insert(params[0]);
            return null;
        }
    }

    /**
     * Deletes all tasks from the database (does not delete the table).
     */
    private static class deleteTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao asyncTaskDao;

        deleteTaskAsyncTask(TaskDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            asyncTaskDao.deleteTask(params[0]);
            return null;
        }
    }

    /**
     * Updates a task in the database.
     */
    private static class updateTaskAsyncTask extends AsyncTask<Task, Void, Void> {
        private TaskDao asyncTaskDao;

        updateTaskAsyncTask(TaskDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final Task... params) {
            asyncTaskDao.update(params[0]);
            return null;
        }
    }
}
