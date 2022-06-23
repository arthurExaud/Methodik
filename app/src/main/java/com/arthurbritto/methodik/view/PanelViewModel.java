package com.arthurbritto.methodik.view;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.arthurbritto.methodik.model.Panel;
import com.arthurbritto.methodik.model.PanelRepository;

import java.util.List;

public class PanelViewModel extends AndroidViewModel {

    private PanelRepository repository;
    private LiveData<List<Panel>> allLists;

    public PanelViewModel(@NonNull Application application) {
        super(application);
        repository = new PanelRepository(application);
        allLists = repository.getAllLists();
    }

    LiveData<List<Panel>> getAllLists() {
        return allLists;
    }

    public void insert(Panel panel) {
        repository.insert(panel);
    }

    public void deleteAll() {
        repository.deleteAllPanelLists();
    }

    public void deletePanel(Panel panel) {
        repository.deletePanelList(panel);
    }

    public void update(Panel panel) {
        repository.update(panel);
    }
}
