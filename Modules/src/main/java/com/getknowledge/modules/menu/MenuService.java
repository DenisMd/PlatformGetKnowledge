package com.getknowledge.modules.menu;

import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.modules.menu.item.MenuItemsRepository;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletContext;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Service("MenuService")
public class MenuService extends AbstractService implements BootstrapService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (menuRepository.count() == 0) {
            menuRepository.createMenuFromJson(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/menu/menuBootstrap.json"));
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Menu service");
        bootstrapInfo.setOrder(1);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }


    @Action(name = "getMenuByName" , mandatoryFields = {"name"})
    @Transactional
    public Menu getMenuByName(HashMap<String, Object> data) {
        return menuRepository.getSingleEntityByFieldAndValue("name" , data.get("name"));
    }

    @Action(name = "getMenu")
    @Transactional
    public Menu getMenu(HashMap<String, Object> data) {
        UserInfo user = userInfoService.getAuthorizedUser(data);
        Menu menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.General.name());
        if (user == null) {
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(RoleName.ROLE_ADMIN.name())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.Admin.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(RoleName.ROLE_AUTHOR.name())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.Author.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(RoleName.ROLE_HELPDESK.name())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.HelpDesk.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(RoleName.ROLE_MODERATOR.name())){
            menu = menuRepository.getSingleEntityByFieldAndValue("name" ,MenuNames.Moderator.name());
            return menu;
        }

        return menu;
    }

}
