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
public class PermissionService extends AbstractService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private TraceService traceService;

    @Action(name = "getUsersByPermission" , mandatoryFields = {"permissionId"})
    @Transactional
    public List<User> getUsersByPermission(HashMap<String,Object> data) throws NotAuthorized {

        Long id = longFromField("permissionId",data);
        Permission permission = permissionRepository.read(id);

        if (permission == null)
            return null;

        if (!isAccessToRead(data,permission)) {
            throw new NotAuthorized("not authorized for read users for permission" , traceService);
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
            throw new NotAuthorized("not authorized for read users for permission" , traceService);
        }

        return permission.getRoles();
    }
}
