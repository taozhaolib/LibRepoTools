/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.shareok.data.webserv;

/**
 *
 * @author Tao Zhao
 */

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.shareok.data.config.ShareokdataManager;
import org.shareok.data.kernel.api.services.config.ConfigService;
import org.shareok.data.kernel.api.services.user.PasswordAuthenticationService;
import org.shareok.data.kernel.api.services.user.RedisUserService;
import org.shareok.data.redis.RedisUser;
import org.shareok.data.webserv.exceptions.NUllUserException;
import org.shareok.data.webserv.exceptions.NoNewUserRegistrationException;
import org.shareok.data.webserv.exceptions.NullSessionException;
import org.shareok.data.webserv.exceptions.RegisterUserInfoExistedException;
import org.shareok.data.webserv.exceptions.UserRegisterInfoNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.session.ExpiringSession;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
 
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
 
public class UserSessionInterceptor implements HandlerInterceptor  {
    
    @Autowired
    private RedisUserService redisUserService;
    
    @Autowired
    private ConfigService configService;
    
    @Autowired
    private PasswordAuthenticationService pwAuthenService;
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
         
        try {
            String contextPath = request.getServletPath();
            if(contextPath.contains("/")){
                contextPath = contextPath.split("/")[1];
            }
            if(null != contextPath && !"".equals(contextPath) && ShareokdataManager.requiredUserAuthentication(contextPath)){
                SessionRepository<Session> repo = (SessionRepository<Session>) request.getAttribute(SessionRepository.class.getName());
                
                if(contextPath.equals("register")){  
                    if(!configService.getRegistrationConfig()){
                        throw new NoNewUserRegistrationException("The registraion of new users has been closed!");
                    }
                    String email = (String) request.getParameter("email");
                    String password = pwAuthenService.hash((String) request.getParameter("password"));
                    String userName = (String) request.getParameter("nickname");
                    if(null == email || "".equals(email)){
                        throw new UserRegisterInfoNotFoundException("Valid email register information is required!");
                    }
                    if(null == password || "".equals(password)){
                        throw new UserRegisterInfoNotFoundException("Valid password is required for registration!");
                    }
                    /*****************
                     * Some password validation logic here:
                     */                    
                    HttpSession httpSession = (HttpSession)request.getSession();
                    ExpiringSession session = (ExpiringSession) repo.getSession(httpSession.getId());
                    if(null == session){
                        session = (ExpiringSession) repo.createSession();
                    }                    
                    String sessionId = session.getId();
                    RedisUser user = redisUserService.findUserByUserEmail(email);
                    if(null != user){
                        throw new RegisterUserInfoExistedException("User Email has already Existed!");
                    }
                    else {
                        user = redisUserService.getNewUser();
                        user.setEmail(email);
                        user.setPassword(password);
                        if(null == userName || userName.equals("")){
                            userName = email;
                        }
                        user.setUserName(userName);
                        user.setSessionKey(sessionId);
                        redisUserService.addUser(user);
                    }
                    
                    setSessionUserInfo(session, httpSession, user);
                    repo.save(session);
                }
                else if(contextPath.equals("userLogin")){
                    String email = (String) request.getParameter("email");
                    String password = (String) request.getParameter("password");
                    if(null == email || "".equals(email)){
                        throw new UserRegisterInfoNotFoundException("Valid email information is required for logging in!");
                    }
                    if(null == password || "".equals(password)){
                        throw new UserRegisterInfoNotFoundException("Valid password is required for logging in!");
                    }
                    /*****************
                     * Some password validation logic here:
                     */                    
                    HttpSession httpSession = (HttpSession)request.getSession();
                    ExpiringSession session = (ExpiringSession) repo.getSession(httpSession.getId());
                    if(null == session || session.isExpired()){
                        session = (ExpiringSession) repo.createSession();
                    }                    
                    String sessionId = session.getId();
                    RedisUser user = redisUserService.findUserByUserEmail(email);
                    
                    if(null == user || !pwAuthenService.authenticate(password, user.getPassword())){
                        throw new UserRegisterInfoNotFoundException("User information cannot be found!");
                    }
                    
                    user.setSessionKey(sessionId);
                    redisUserService.updateUser(user);
                    
                    setSessionUserInfo(session, httpSession, user);
                    httpSession.setAttribute("email", email);
                    repo.save(session);
                }
                else if(contextPath.equals("logout")){
                    HttpSession session = (HttpSession) request.getSession(false);
                    if(null != session){
                        ExpiringSession exSession = (ExpiringSession) repo.getSession(session.getId());
                        if(null != exSession){
                            String email = (String) session.getAttribute("email");
                            if(null != email){
                                redisUserService.invalidateUserSessionIdByEmail(email);
                            }
                            exSession.isExpired();
                            repo.delete(exSession.getId());
                        }
                        session.invalidate();
                    }
                }
                // *** The following situation applies to authentication logic based on session information ***
                else {
                    boolean sessionValidated = false;
                    HttpSession session = (HttpSession) request.getSession(false);
                    if(null != session){
                        ExpiringSession exSession = (ExpiringSession) repo.getSession(session.getId());
                        if(null != exSession && !exSession.isExpired()){
                            String email = (String) session.getAttribute("email");
                            if(null != email){
                                RedisUser userPersisted = redisUserService.findAuthenticatedUser(email, session.getId());
                                if(null != userPersisted){
                                    sessionValidated = true;
                                }
                            }
                        }
                    }
                    
                    if(!sessionValidated){
                        if(null != session){
                            repo.delete(session.getId());
                            session.setAttribute(ShareokdataManager.getSessionRedisUserAttributeName(), null);
                            session.invalidate();
                        }
                        request.logout();
                        //request.getRequestDispatcher("/WEB-INF/jsp/logout.jsp").forward(request, response);
                        HttpServletResponse httpReponse = (HttpServletResponse)response;
                        httpReponse.sendRedirect("/webserv/login");
                    }
                }
            }
            else{
                ;
            }
        } catch (IOException ex) {
                request.setAttribute("errorMessage", ex.getMessage());
                request.getRequestDispatcher("/WEB-INF/jsp/userError.jsp").forward(request, response);
        } catch (ServletException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/userError.jsp").forward(request, response);
        } catch (UserRegisterInfoNotFoundException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/userError.jsp").forward(request, response);
        } catch (RegisterUserInfoExistedException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/userError.jsp").forward(request, response);
        } catch (NoNewUserRegistrationException ex) {
            request.setAttribute("errorMessage", ex.getMessage());
            request.getRequestDispatcher("/WEB-INF/jsp/closedRegistration.jsp").forward(request, response);
        }
         
        return true;
    }
     
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("Post-handle");
    }
     
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("After completion handle");
    }
    
    private void setSessionUserInfo(ExpiringSession session, HttpSession httpSession, RedisUser user){
        
        try{
            if(null == session){
                throw new NullSessionException("The ExpiringSession Session is NULL!");
            }
            if(null == httpSession){
                throw new NullSessionException("The Http Session is NULL!");
            }
            if(null == user){
                throw new NUllUserException("The User Information is NULL!");
            }

            httpSession.setAttribute("userName", user.getUserName());
            httpSession.setAttribute("email", user.getEmail());
            httpSession.setAttribute("userId", user.getUserId());
            httpSession.setAttribute("isActive", user.isIsActive());
            
            session.setAttribute("userName", user.getUserName());
            session.setAttribute("email", user.getEmail());
            session.setAttribute("userId", user.getUserId());
            session.setAttribute("isActive", user.isIsActive());
        }
        catch (NullSessionException ex){
            Logger.getLogger(UserSessionInterceptor.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (NUllUserException ex){
            Logger.getLogger(UserSessionInterceptor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
