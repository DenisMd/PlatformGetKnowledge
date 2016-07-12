package com.getknowledge.modules.menu;

import com.fasterxml.jackson.core.type.TypeReference;
import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.modules.menu.item.MenuItemsRepository;
import com.getknowledge.platform.base.repositories.ProtectedRepository;
import com.getknowledge.platform.base.repositories.enumerations.RepOperations;
import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.RoleRepository;
import com.getknowledge.platform.modules.trace.TraceService;
import com.getknowledge.platform.modules.trace.enumeration.TraceLevel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository("MenuRepository")
public class MenuRepository extends ProtectedRepository<Menu> {

    @Override
    public List<RepOperations> restrictedOperations() {
        List<RepOperations> operations = new ArrayList<>();
        operations.add(RepOperations.Create);
        operations.add(RepOperations.Remove);
        return operations;
    }


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

            String menuItemName = (String) item.get("name");
            String menuItemUrl = (String) item.get("url");

            MenuItem menuItem = null;

            if (parent.getId() != null) {
                List<MenuItem> list = entityManager.createQuery("select mi from MenuItem mi where mi.title = :title and mi.url = :url and mi.menu.id = :menuId", MenuItem.class)
                        .setParameter("title", menuItemName)
                        .setParameter("url", menuItemUrl)
                        .setParameter("menuId" , parent.getId())
                        .getResultList();
                menuItem = list.isEmpty() ? null : list.get(0);
            }

            boolean isNewItem = false;

            if (menuItem == null) {
                isNewItem = true;
                menuItem = new MenuItem();
                menuItem.setTitle(menuItemName);
                menuItem.setUrl(menuItemUrl);
            }

            if (item.containsKey("imageUrl")) {
                menuItem.setIconUrl((String) item.get("imageUrl"));
            }

            if (item.containsKey("color")) {
                menuItem.setColor((String) item.get("color"));
            }

            if (isNewItem) {
                menuItem.setMenu(parent);
                menuItemsRepository.create(menuItem);
                parent.getItems().add(menuItem);
            } else {
                menuItemsRepository.merge(menuItem);
            }

            if (item.containsKey("subItems")) {
                List<HashMap<String, Object>> subItems = (List<HashMap<String, Object>>) item.get("subItems");
                createSubItems(subItems, menuItem, json);
            }
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

            MenuItem subMenuItem = null;

            String menuItemName = (String) subItem.get("name");
            String menuItemUrl = (String) subItem.get("url");

            if (parent.getId() != null) {
                List<MenuItem> list = entityManager.createQuery("select mi from MenuItem mi where mi.title = :title and mi.url = :url and mi.parent.id = :parentId", MenuItem.class)
                        .setParameter("title", menuItemName)
                        .setParameter("url", menuItemUrl)
                        .setParameter("parentId" , parent.getId())
                        .getResultList();
                subMenuItem = list.isEmpty() ? null : list.get(0);
            }

            boolean isNewItem = false;

            if (subMenuItem == null) {
                isNewItem = true;
                subMenuItem = new MenuItem();
                subMenuItem.setTitle(menuItemName);
                subMenuItem.setUrl(menuItemUrl);
            }
            if (subItem.containsKey("imageUrl")) {
                subMenuItem.setIconUrl((String) subItem.get("imageUrl"));
            }

            if (isNewItem) {
                subMenuItem.setParent(parent);
                menuItemsRepository.create(subMenuItem);
                parent.getSubItems().add(subMenuItem);
            } else {
                menuItemsRepository.merge(subMenuItem);
            }
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

                String menuName = (String) menu.get("name");
                Menu menuEntity = getSingleEntityByFieldAndValue("name",menuName);

                boolean isNewMenu = false;

                if (menuEntity == null) {
                    isNewMenu = true;
                    menuEntity = new Menu();
                    menuEntity.setName(menuName);
                }

                if (menu.containsKey("roleName")) {
                    Role role = roleRepository.getRoleByName((String) menu.get("roleName"));
                    if (role != null) menuEntity.setRole(role);
                }

                if (isNewMenu) {
                    create(menuEntity);
                } else {
                    merge(menuEntity);
                }

                List<HashMap<String, Object>> items = (List<HashMap<String, Object>>) menu.get("items");

                createItems(items,menuEntity,json);

                if (menu.containsKey("base")) {
                    String baseName = (String) menu.get("base");
                    HashMap<String, Object> baseMenu =  menus.stream().filter(x -> x.get("name").equals(baseName)).findFirst().get();
                    List<HashMap<String, Object>> items2 = (List<HashMap<String, Object>>) baseMenu.get("items");
                    createItems(items2, menuEntity, json);
                }
            }
        } catch (IOException e) {
            trace.logException("Error read json from file" , e, TraceLevel.Error,true);
        }
    }
}
