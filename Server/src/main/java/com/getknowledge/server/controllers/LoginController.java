package com.getknowledge.server.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class LoginController {

    class LoginResult {
        private String message;

        public LoginResult(String message) {
            this.message = message;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }


    @RequestMapping(value = "/successLogin")
    public @ResponseBody
    LoginResult successLogin() {
        return new LoginResult("success");
    }

    @RequestMapping(value = "/errorLogin")
    public @ResponseBody
    LoginResult errorLogin() {
        return new LoginResult("Invalid username and password!");
    }
}
