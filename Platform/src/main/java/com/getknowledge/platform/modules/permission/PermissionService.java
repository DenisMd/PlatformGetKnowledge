package com.getknowledge.platform.modules.permission;

import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.NotAuthorized;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.bootstrapInfo.states.BootstrapState;
import com.getknowledge.platform.modules.permission.names.PermissionNames;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.aspectj.weaver.ast.Not;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

@Service("PermissionService")
public class PermissionService extends AbstractService implements BootstrapService{

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TraceService traceService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws ParseException {
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSections.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditMenu.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditSocialLinks.getName()));
        permissionRepository.ifNotExistCreate(new Permission(PermissionNames.EditGroupCourses.getName()));
    }


    @Action(name = "getUsersByPermission" , mandatoryFields = {"permissionId"})
    public List<User> getUsersByPermission(HashMap<String,Object> data) throws NotAuthorized {

        Long id = new Long((Integer)data.get("permissionId"));
        Permission permission = permissionRepository.read(id);

        if (permission == null)
            return null;

        if (!isAccessToRead(data,permission,userRepository)) {
            throw new NotAuthorized("not authorized for read users for permission" , traceService , TraceLevel.Warning);
        }

        List<User> users = entityManager.createQuery("select u from User u join u.permissions as p where p.id = :id").setParameter("id" ,id).getResultList();
        users.forEach(u -> {u.getPermissions().clear(); u.setRole(null);});
        return users;
    }

    @Action(name ="getRolesByPermission" , mandatoryFields = {"permissionId"})
    public List<Role> getRolesByPermission(HashMap<String,Object> data) throws NotAuthorized {

        Long id = new Long((Integer)data.get("permissionId"));
        Permission permission = permissionRepository.read(id);

        if (permission == null)
            return null;

        if (!isAccessToRead(data,permission,userRepository)) {
            throw new NotAuthorized("not authorized for read users for permission" , traceService , TraceLevel.Warning);
        }

        List<Role> roles = entityManager.createQuery("select r from Role r join r.permissions as p where p.id = :id").setParameter("id" , id).getResultList();
        roles.forEach(r -> r.getPermissions().clear());
        return roles;
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("PermissionService");
        bootstrapInfo.setBootstrapState(BootstrapState.NotComplete);
        bootstrapInfo.setRepeat(true);
        return bootstrapInfo;
    }
}
