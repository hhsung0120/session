package com.heeseong.session.controller;

import com.heeseong.session.weblistener.WebSessionListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/{userId}")
    public String index(@PathVariable String userId, HttpServletRequest request){
        WebSessionListener.getInstance().setSession(request, userId);
        return "index";
    }

    @ResponseBody
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        WebSessionListener.getInstance().removeSession(request);
        return "logout";
    }

    @ResponseBody
    @PostMapping("/was/keepAlive")
    public String keepAlive(HttpServletRequest request){
        System.out.println("keepAlive -> " + request.getSession().getAttribute("userId"));
        return "";
    }
}
