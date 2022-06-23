package com.arthurbritto.methodik.model;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

/**
 * Data Access Object (DAO) for a task.
 * Each method performs a database operation, such as inserting
 * or deleting a task, running a DB query.
 */
@Dao
public interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Task task);

    @Delete
    void deleteTask(Task task);

    @Update
    void update(Task... tasks);

    @Query("SELECT * FROM task WHERE panel_id =:panelId ORDER BY color, name ASC")
    List<Task> getAllTasksFromPanel(int panelId);
}
