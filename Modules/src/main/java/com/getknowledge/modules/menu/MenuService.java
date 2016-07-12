package com.getknowledge.modules.menu;

import com.getknowledge.modules.menu.enumerations.MenuNames;
import com.getknowledge.modules.platform.auth.ModuleRoleName;
import com.getknowledge.modules.userInfo.UserInfo;
import com.getknowledge.modules.userInfo.UserInfoService;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import com.getknowledge.platform.modules.role.names.BaseRoleName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

@Service("MenuService")
public class MenuService extends AbstractService implements BootstrapService {

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        menuRepository.createMenuFromJson(getClass().getClassLoader().getResourceAsStream("com.getknowledge.modules/menu/menuBootstrap.json"));
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Menu service");
        bootstrapInfo.setOrder(1);
        bootstrapInfo.setRepeat(true);
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

        if (user.getUser().getRole().getRoleName().equals(BaseRoleName.ROLE_ADMIN())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.Admin.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(ModuleRoleName.ROLE_AUTHOR())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.Author.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(ModuleRoleName.ROLE_HELPDESK())) {
            menu = menuRepository.getSingleEntityByFieldAndValue("name" , MenuNames.HelpDesk.name());
            return menu;
        }

        if (user.getUser().getRole().getRoleName().equals(ModuleRoleName.ROLE_MODERATOR())){
            menu = menuRepository.getSingleEntityByFieldAndValue("name" ,MenuNames.Moderator.name());
            return menu;
        }

        return menu;
    }

}
