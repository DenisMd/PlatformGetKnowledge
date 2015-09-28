package com.getknowledge.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping(value = "/module")
public class ViewController {

    @RequestMapping(value = "/**")
    public ModelAndView viewer(HttpServletRequest request) {
        String restOfTheUrl = (String) request.getAttribute(
                HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String [] split = restOfTheUrl.split("/");
        String path = restOfTheUrl + "/" + split[split.length-1];
        return new ModelAndView(path);
    }

}
