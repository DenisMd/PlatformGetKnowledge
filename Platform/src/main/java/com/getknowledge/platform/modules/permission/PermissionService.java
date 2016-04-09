package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;

@Service("PermissionService")
public class PermissionService extends AbstractService implements BootstrapService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TraceService traceService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws ParseException {
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSections.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditMenu.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSocialLinks.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditFolders.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.ReadHpMessage.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditBooks.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreateBooks.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreatePrograms.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditPrograms.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditProgrammingDictionaries.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.CreateCourse.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditCourse.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditKnowledge.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.UploadVideos.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditNews.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.BlockComments.getName()));
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Permission service");
        bootstrapInfo.setRepeat(true);
        return bootstrapInfo;
    }

    @Action(name = "getUsersByPermission" , mandatoryFields = {"permissionId"})
    @Transactional
    public List<User> getUsersByPermission(HashMap<String,Object> data) throws NotAuthorized {

        Long id = longFromField("permissionId",data);
        Permission permission = permissionRepository.read(id);

        if (permission == null)
            return null;

        if (!isAccessToRead(data,permission)) {
            throw new NotAuthorized("not authorized for read users for permission" , traceService , TraceLevel.Warning);
        }

        return permission.getUsers();
    }

    @Action(name ="getRolesByPermission" , mandatoryFields = {"permissionId"})
    @Transactional
    public List<Role> getRolesByPermission(HashMap<String,Object> data) throws NotAuthorized {

        Long id = longFromField("permissionId",data);
        Permission permission = permissionRepository.read(id);

        if (permission == null)
            return null;

        if (!isAccessToRead(data,permission)) {
            throw new NotAuthorized("not authorized for read users for permission" , traceService , TraceLevel.Warning);
        }

        return permission.getRoles();
    }
}
