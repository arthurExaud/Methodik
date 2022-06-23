package com.arthurbritto.methodik.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.arthurbritto.methodik.model.Task;
import com.arthurbritto.methodik.model.TaskRepository;

public class TaskViewModel extends AndroidViewModel {

    private TaskRepository repository;

    public TaskViewModel(@NonNull Application application) {
        super(application);
        repository = new TaskRepository(application);
    }

    public void insert(Task task) {
        repository.insert(task);
    }

    public void deleteTask(Task task) {
        repository.deleteTask(task);
    }

    public void update(Task task) {
        repository.update(task);
    }

    public void getTasksByPanel(int panelId, TaskRepository.GetTasksResult callback) {
        repository.getTasksByPanel(panelId, callback);
    }
}
