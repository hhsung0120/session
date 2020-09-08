package com.heeseong.session.controller;

import com.heeseong.session.weblistener.WebSessionListener;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class SessionController {

    @GetMapping("/{userId}")
    public String session(@PathVariable String userId
                        , HttpServletRequest request){

        WebSessionListener.getInstance().setSession(request, userId);
        return "성공";
    }
}
