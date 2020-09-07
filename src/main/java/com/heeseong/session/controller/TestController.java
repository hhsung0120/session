package com.heeseong.session.controller;

import com.heeseong.session.weblistener.SessionListener;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/test")
public class TestController {

    @GetMapping("/{userId}")
    public String index(@PathVariable String userId
                        , HttpServletRequest request){
        SessionListener.getInstance().setSession(request, userId);
        return "index";
    }

    @ResponseBody
    @PostMapping("/logout")
    public String logout(HttpServletRequest request){
        SessionListener.getInstance().removeSession(request.getSession());
        return "logout";
    }
}
