package com.heeseong.session.weblistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;
import java.util.Hashtable;


@Slf4j
@Component
public class SessionListener implements HttpSessionListener {

    public static SessionListener sessionListener = null;
    private static Hashtable loginSessionList = new Hashtable();

    /**
     * 싱글톤 생성
     * @return
     */
    public static synchronized SessionListener getInstance() {
        if(sessionListener == null) {
            sessionListener = new SessionListener();
        }
        return sessionListener;
    }

    /**
     * HttpServletRequest 실행 되는 순간 호출
     * @param httpSessionEvent
     */
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        log.info("sessionCreated {}", httpSessionEvent.getSession());

    }

    /**
     * session 이 소멸되는 시점에 실행, 기본 단위는 초(1분 미만은 설정할 수 없음)
     * @param httpSessionEvent
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        log.info("sessionDestroyed userId -> {}", httpSessionEvent.getSession().getAttribute("userId"));
    }

    /**
     * 현제 HashTable에 담겨 있는 유저 리스트, 즉 session list
     */
    private void currentSessionList(){
        Enumeration elements = loginSessionList.elements();
        HttpSession session = null;
        while (elements.hasMoreElements()){
            session = (HttpSession)elements.nextElement();

            String userId = (String)session.getAttribute("userId");
            log.info("currentSessionUserList -> userId {} ", userId);
            log.info("currentSessionUserList -> sessionId {} ", session.getId());
            log.info("currentSessionUserList -> hashtable SessionList {} ", loginSessionList.get(session.getId()));
        }
    }

    /**
     * session 생성
     * @param request
     * @param value
     */
    public void setSession(HttpServletRequest request, String value){
        HttpSession session = request.getSession();
        session.setAttribute("userId", value);
        session.setMaxInactiveInterval(0);

        System.out.println(isLoginUser(value, request));
        //HashMap에 Login에 성공한 유저 담기
        synchronized(loginSessionList){
            loginSessionList.put(session.getId(), session);
        }

        //currentSessionList();
    }

    public boolean isLoginUser(String loginUserId, HttpServletRequest request){
        Enumeration elements = loginSessionList.elements();
        HttpSession session = null;
        while (elements.hasMoreElements()){
            session = (HttpSession)elements.nextElement();
            String userId = (String)session.getAttribute("userId");
            if(loginUserId.equals(userId) && (!session.getId().equals(request.getSession().getId()))){
                return true;
            }
        }
        return false;
    }

}
