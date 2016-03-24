package com.getknowledge.modules.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.modules.menu.item.MenuItemsRepository;
import com.getknowledge.platform.base.repositories.BaseRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.trace.level.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

@Repository("MenuRepository")
public class MenuRepository extends ProtectedRepository<Menu> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MenuItemsRepository menuItemsRepository;

    @Autowired
    private TraceService trace;

    @Override
    protected Class<Menu> getClassEntity() {
        return Menu.class;
    }

    private void createItems(List<HashMap<String, Object>> items,Menu parent,HashMap<String, Object> json) {
        //Пробегаем элементы в меню
        for (HashMap<String, Object> item : items) {
            MenuItem menuItem = new MenuItem();
            menuItem.setTitle((String) item.get("name"));
            menuItem.setUrl((String) item.get("url"));

            if (item.containsKey("imageUrl")) {
                menuItem.setIconUrl((String) item.get("imageUrl"));
            }

            if (item.containsKey("color")) {
                menuItem.setColor((String) item.get("color"));
            }

            if (item.containsKey("subItems")) {
                List<HashMap<String, Object>> subItems = (List<HashMap<String, Object>>) item.get("subItems");
                createSubItems(subItems, menuItem, json);
            }

            menuItemsRepository.create(menuItem);
            parent.getItems().add(menuItem);
        }
    }

    private void createSubItems(List<HashMap<String, Object>> subItems, MenuItem parent, HashMap<String, Object> json) {
        //Пробегаем подэлементы
        for (HashMap<String, Object> subItem : subItems) {
            if (subItem.containsKey("ref")) {
                List<HashMap<String, Object>> ref = (List<HashMap<String, Object>>) json.get("ref");
                createSubItems(ref,parent,null);
                break;
            }
            MenuItem subMenuItem = new MenuItem();
            subMenuItem.setTitle((String) subItem.get("name"));
            subMenuItem.setUrl((String) subItem.get("url"));
            if (subItem.containsKey("imageUrl")) {
                subMenuItem.setIconUrl((String) subItem.get("imageUrl"));
            }

            menuItemsRepository.create(subMenuItem);
            parent.getSubItems().add(subMenuItem);
        }
    }

    public void createMenuFromJson(InputStream is) {
        TypeReference<HashMap<String, Object>> typeRef
                = new TypeReference<HashMap<String, Object>>() {
        };

        try {
            HashMap<String, Object> json = objectMapper.readValue(is,typeRef);
            List<HashMap<String, Object>> menus = (List<HashMap<String, Object>>) json.get("menu");

            //пробегаем все возможные меню
            for (HashMap<String, Object> menu : menus) {

                Menu menuEntity = new Menu();
                menuEntity.setName((String) menu.get("name"));
                if (menu.containsKey("roleName")) {
                    Role role = roleRepository.getRoleByName((String) menu.get("roleName"));
                    if (role != null) menuEntity.setRole(role);
                }


                List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) menu.get("items");

                createItems(items,menuEntity,json);

                if (menu.containsKey("base")) {
                    String baseName = (String) menu.get("base");
                    HashMap<String, Object> baseMenu =  menus.stream().filter(x -> x.get("name").equals(baseName)).findFirst().get();
                    List<HashMap<String, Object>> items2 = (List<HashMap<String, Object>>) baseMenu.get("items");
                    createItems(items2,menuEntity,json);
                }

                create(menuEntity);

            }
        } catch (IOException e) {
            trace.logException("Error read json from file" , e, TraceLevel.Error);
        }
    }
}
