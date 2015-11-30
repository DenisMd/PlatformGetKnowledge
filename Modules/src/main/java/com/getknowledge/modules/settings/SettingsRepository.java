package com.getknowledge.modules.settings;

import com.getknowledge.platform.base.repositories.BaseRepository;
import org.springframework.stereotype.Repository;

@Repository("SettingsRepository")
public class SettingsRepository extends BaseRepository<Settings> {
    @Override
    protected Class<Settings> getClassEntity() {
        return Settings.class;
    }

    public Settings getSettings(){
        return listPartial(0,1).get(0);
    }
}
