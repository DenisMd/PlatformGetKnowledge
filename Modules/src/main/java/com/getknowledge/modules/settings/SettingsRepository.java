package com.getknowledge.modules.settings;

import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository("SettingsRepository")
public class SettingsRepository extends BaseRepository<Settings> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Update);
        operations.add(RepOperations.Remove);
        return operations;
    }

    @Override
    protected Class<Settings> getClassEntity() {
        return Settings.class;
    }

    public Settings getSettings(){
        return listPartial(0,1).get(0);
    }
}
