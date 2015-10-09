package com.getknowledge.modules.menu;

import com.getknowledge.modules.menu.item.MenuItem;
import com.getknowledge.modules.menu.item.MenuItemsRepository;
import com.getknowledge.platform.annotations.Action;
import com.getknowledge.platform.base.services.AbstractService;
import com.getknowledge.platform.base.services.BootstrapService;
import com.getknowledge.platform.exceptions.ParseException;
import com.getknowledge.platform.modules.bootstrapInfo.BootstrapInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContext;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service("MenuService")
public class MenuService extends AbstractService implements BootstrapService {

    @Autowired
    MenuRepository menuRepository;

    @Autowired
    MenuItemsRepository menuItemsRepository;

    @Autowired
    ServletContext servletContext;

    private int countLevel(String text) {
        int numberSpacing = 4;
        int count = 0;
        for (int i = 0; i < text.length(); i++) {
            if (text.charAt(i) != ' ') {
                break;
            }
            count++;
        }
        if (count < numberSpacing || (count % 4 != 0)) {
            return 0;
        }
        return count / numberSpacing;
    }

    private void parseMenu(String fileName) throws Exception {
        InputStream is = getClass().getClassLoader().getResourceAsStream(fileName);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line = "";

        List<List<MenuItem>> stackMenuItems = new ArrayList<>();

        int level = 0;
        Menu menu = null;
        MenuItem prevMenu = null;
        List<MenuItem> parents = new ArrayList<>();
        while ((line = reader.readLine()) != null) {

            if (!line.startsWith(" ")) {

                if (!parents.isEmpty()) {
                    for (MenuItem menuItem : parents) {
                        menuItem.setSubItems(stackMenuItems.get(level));
                        level--;
                        menuItemsRepository.update(menuItem);
                    }
                    parents.clear();
                }

                level = 0;
                if (menu != null) {
                    menu.setItems(stackMenuItems.get(level));
                    menuRepository.update(menu);
                }
                stackMenuItems.clear();
                stackMenuItems.add(new ArrayList<>());

                menu = new Menu();
                menu.setTitle(line);
                menuRepository.create(menu);

                continue;
            }

            String[] split = line.split(":");
            if (split.length != 2) throw new ParseException("Can't parse menu from file. Error in line : " + line);
            String title = split[0].trim();
            String url = split[1].trim();

            int stringLevel = countLevel(line);

            MenuItem menuItem = new MenuItem();
            menuItem.setUrl(url);
            menuItem.setTitle(title);
            menuItemsRepository.create(menuItem);

            if (level != stringLevel-1){
                if (level < stringLevel) {
                    parents.add(prevMenu);
                    level++;
                    stackMenuItems.add(new ArrayList<>());
                } else {
                    level--;
                    parents.get(level).setSubItems(stackMenuItems.remove(level+1));
                    menuItemsRepository.update(parents.get(level));
                    parents.remove(level);
                }
            }

            stackMenuItems.get(level).add(menuItem);
            prevMenu = menuItem;
        }

        if (menu != null) {

            if (!parents.isEmpty()) {
                for (MenuItem menuItem : parents) {
                    menuItem.setSubItems(stackMenuItems.get(level));
                    level--;
                    menuItemsRepository.update(menuItem);
                }
                parents.clear();
            }

            menu.setItems(stackMenuItems.get(0));
            menuRepository.update(menu);
        }
    }

    @Override
    public void bootstrap(HashMap<String, Object> map) throws Exception {
        if (menuRepository.count(Menu.class) == 0) {
            parseMenu("com.getknowledge.modules/menu/menuBootstrap");
        }
    }

    @Override
    public BootstrapInfo getBootstrapInfo() {
        BootstrapInfo bootstrapInfo = new BootstrapInfo();
        bootstrapInfo.setName("Menu service");
        bootstrapInfo.setOrder(0);
        bootstrapInfo.setRepeat(false);
        return bootstrapInfo;
    }
}
