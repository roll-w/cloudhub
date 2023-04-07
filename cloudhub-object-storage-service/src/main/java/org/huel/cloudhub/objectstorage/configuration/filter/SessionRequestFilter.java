package org.huel.cloudhub.objectstorage.configuration.filter;

import org.huel.cloudhub.objectstorage.controller.SessionConstants;
import org.huel.cloudhub.objectstorage.data.dto.user.UserInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * @author RollW
 */
public class SessionRequestFilter implements Filter {
    // TODO: 更换为JWT
    private UserDetailsService userDetailsService;

    @Autowired
    public void setUserDetailsService(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    private final Logger logger = LoggerFactory.getLogger(SessionRequestFilter.class);

    @Override
    public void doFilter(ServletRequest req, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpSession session = request.getSession();
        UserInfo userInfo = (UserInfo) session.getAttribute(SessionConstants.USER_INFO_SESSION_ID);
        if (userInfo == null) {
            chain.doFilter(request, response);
            return;
        }
        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            logger.info("holder authentication null. re-login by session with name {}", userInfo.username());
            UserDetails userDetails =
                    userDetailsService.loadUserByUsername(userInfo.username());
            if (userDetails != null) {
                Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                        userDetails,
                        userDetails.getPassword(),
                        userDetails.getAuthorities()
                );
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }
        chain.doFilter(request, response);
    }
}
