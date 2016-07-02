package com.getknowledge.modules.folder;

import com.getknowledge.platform.base.repositories.ProtectedRepository;
import org.springframework.stereotype.Repository;

@Repository("FolderRepository")
public class FolderRepository extends ProtectedRepository<Folder> {
    @Override
    protected Class<Folder> getClassEntity() {
        return Folder.class;
    }
}
