package com.heeseong.session.weblistener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.servlet.server.Session;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import java.util.Enumeration;
import java.util.Hashtable;


@Slf4j
@Component
public class WebSessionListener implements HttpSessionListener {

    public static WebSessionListener sessionListener = null;
    private static Hashtable loginSessionList = new Hashtable();

    /**
     * 싱글톤 생성
     * @return
     */
    public static synchronized WebSessionListener getInstance() {
        if(sessionListener == null) {
            sessionListener = new WebSessionListener();
        }
        return sessionListener;
    }

    /**
     * HttpServletRequest 실행 되는 순간 호출
     * @param httpSessionEvent
     */
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {
        log.info("sessionCreated -> {}", httpSessionEvent.getSession().getAttribute("userId"));
    }

    /**
     * session 이 소멸되는 시점에 실행, 기본 단위는 초(1분 미만은 설정할 수 없음)
     * @param httpSessionEvent
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
        log.info("sessionDestroyed 실행");
        HttpSession session = httpSessionEvent.getSession();

        String userId = (String) session.getAttribute("userId");

        //로그아웃 유저 삭제
        synchronized(loginSessionList){
            loginSessionList.remove(httpSessionEvent.getSession().getId());
        }

        if(userId != null){
            this.updateUserCloseTime(userId);
        }

        currentSessionList();
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
            //log.info("currentSessionUserList -> sessionId {} ", session.getId());
            //log.info("currentSessionUserList -> hashtable SessionList {} ", loginSessionList.get(session.getId()));
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
        session.setMaxInactiveInterval(2);

        //HashMap에 Login에 성공한 유저 담기
        synchronized(loginSessionList){
            loginSessionList.put(session.getId(), session);
        }
        currentSessionList();
    }

    /**
     * session 삭제
     * 세션이 remove 되면 destroyed 함수가 실행된다.
     * @param request
     */
    public void removeSession(HttpServletRequest request){
        log.info("removeSession 실행");

        HttpSession session = request.getSession();
        String userId = (String)session.getAttribute("userId");

        session.removeAttribute("userId");
        session.invalidate();

        if(userId != null){
            this.updateUserCloseTime(userId);
        }
    }

    /**
     * 유저 나간 시간 업데이트
     * @param userId
     */
    private void updateUserCloseTime(String userId) {
        log.info("updateUserCloseTime {} ", userId);
        //호출부에서 NULL 검사
        //업데이트 로직
    }

    /**
     * 현재 로그인한 유저가 이미 존재 하는지 확인
     * @param request
     * @param loginUserId
     * @return boolean
     */
    public boolean isLoginUser(HttpServletRequest request, String loginUserId){
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
