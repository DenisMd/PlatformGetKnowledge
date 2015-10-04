package com.getknowledge.server.controllers;

import com.getknowledge.platform.modules.role.Role;
import com.getknowledge.platform.modules.role.names.RoleName;
import com.getknowledge.platform.modules.user.User;
import com.getknowledge.platform.modules.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping(value = "/module")
public class ViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    ServletContext servletContext;

    private Map<String,String> marshalling = new HashMap<>();

    private String specCharacter = "!";
    private String prefix = "/WEB-INF/pages/";

    private void fillMarshalling() {
        if (marshalling.isEmpty()) {
            marshalling.put("admin/" , RoleName.ROLE_ADMIN.name());
        }
    }

    private boolean isCorrectRole(User user , String [] splitUrl) {
        fillMarshalling();
        String tempUrl = "";
        for (int i=1; i < splitUrl.length; i++) {
            tempUrl += splitUrl[i] + "/";
            if (marshalling.containsKey(tempUrl)){
                if (user == null) return false;
                Role role = new Role();
                role.setRoleName(marshalling.get(tempUrl));
                if (!user.isHasRole(role)) {return false;}
            }
        }
        return true;
    }

    private User getCurrentUser(Principal p) {
        return p == null ? null : userRepository.getSingleEntityByFieldAndValue(User.class , "login",p.getName());
    }

    private String getNameJsp(String realPath,String fileName) {
        File directory = new File(realPath);
        File[] list = directory.listFiles();
        String fileNameWithoutSpecCharacter = fileName + ".jsp";
        String fileNameWithSpecCharacter = fileName + specCharacter + ".jsp";
        for (File file : list) {
            if (file.isFile()) {
                if (fileNameWithoutSpecCharacter.equals(file.getName())) {
                    return fileName;
                }
                if (fileNameWithSpecCharacter.equals(file.getName())) {
                    return fileName+specCharacter;
                }
            }
        }
        return "";
    }

    private String getPath(String[] split) {
        String pathForView = split[0];

        if (split.length==1) return pathForView + "/" +split[0];

        for (int i=1; i < split.length; i++) {
            File dir = new File(servletContext.getRealPath(prefix + pathForView + "/" + split[i]));
            if (dir.isDirectory()) {
                pathForView += "/"+split[i];
                if (split.length == i+1) {
                    return pathForView + "/"+getNameJsp(servletContext.getRealPath(prefix + pathForView) , split[split.length-1]);
                }
            } else {
                dir = new File(servletContext.getRealPath(prefix + pathForView + "/" + split[i]+specCharacter));
                if (dir.isDirectory()) {
                    pathForView += "/"+split[i]+specCharacter;
                    if (split.length == i+1) {
                        return pathForView + "/"+getNameJsp(servletContext.getRealPath(prefix + pathForView), split[split.length-1]);
                    }
                } else {
                    if (i+1 != split.length && getNameJsp(servletContext.getRealPath(prefix + pathForView) , split[i-1]).endsWith("!")) {
                        continue;
                    }
                    return pathForView + "/"+getNameJsp(servletContext.getRealPath(prefix + pathForView) , split[split.length-2]);
                }
            }
        }
        return  pathForView;
    }

    private ModelAndView filter(String  restOfTheUrl , Principal p) {
        String [] split = restOfTheUrl.split("/");
        if (!isCorrectRole(getCurrentUser(p), split)) {
            return new ModelAndView("accessDenied");
        }
        return new ModelAndView(getPath(split));
    }

    @RequestMapping(value = "/**")
    public ModelAndView viewer(HttpServletRequest request,Principal principal) {
        String restOfTheUrl = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);

        restOfTheUrl = restOfTheUrl.substring(1);
        return filter(restOfTheUrl,principal);
    }

}
