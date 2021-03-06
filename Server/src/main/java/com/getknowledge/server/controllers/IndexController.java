package com.getknowledge.server.controllers;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.ServletContext;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

@Controller
@RequestMapping(value = "/")
public class IndexController {

    Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    ServletContext servletContext;


    @RequestMapping
    public String index(Model model) {
        model.addAttribute("scripts", getScripts());
        return "index";
    }

    @RequestMapping(value = "/404")
    public String notFound(Model model) {
        return "404";
    }

    @RequestMapping(value = "/accessDenied")
    public String accessDenied(Model model) {
        return "accessDenied";
    }


    @RequestMapping("favicon.ico")
    String favicon() {
        return "forward:/resources/image/favicon.ico";
    }


    public List<String> getScripts() {
        List<String> scripts = new LinkedList<>();
        String path = servletContext.getRealPath("/resources/application");
        List<File> files = (List<File>) FileUtils.listFiles(new File(path), TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        for (File file : files) {
            String filePath = file.getPath();
            if (!filePath.endsWith(".js") || filePath.endsWith("getKnowledge.js") || filePath.endsWith("platform.js")) {
                continue;
            }

            int position = filePath.indexOf("resources");
            scripts.add("/" + filePath.substring(position).replace("\\", "/"));
        }

        return scripts;
    }
}
