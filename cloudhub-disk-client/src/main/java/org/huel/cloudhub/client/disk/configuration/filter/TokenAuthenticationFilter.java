package org.huel.cloudhub.client.disk.configuration.filter;

import org.huel.cloudhub.client.disk.common.ApiContextHolder;
import org.huel.cloudhub.client.disk.domain.authentication.AuthenticationException;
import org.huel.cloudhub.client.disk.domain.authentication.token.AuthenticationTokenService;
import org.huel.cloudhub.client.disk.domain.authentication.token.TokenAuthResult;
import org.huel.cloudhub.client.disk.domain.user.UserDetailsService;
import org.huel.cloudhub.client.disk.domain.user.dto.UserInfo;
import org.huel.cloudhub.client.disk.domain.user.service.UserSignatureProvider;
import org.huel.cloudhub.web.AuthErrorCode;
import org.huel.cloudhub.web.BusinessRuntimeException;
import org.huel.cloudhub.web.RequestUtils;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import space.lingu.NonNull;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author RollW
 */
public class TokenAuthenticationFilter extends OncePerRequestFilter {
    private final AuthenticationTokenService authenticationTokenService;
    private final UserDetailsService userDetailsService;
    private final UserSignatureProvider userSignatureProvider;

    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public TokenAuthenticationFilter(AuthenticationTokenService authenticationTokenService,
                                     UserDetailsService userDetailsService,
                                     UserSignatureProvider userSignatureProvider) {
        this.authenticationTokenService = authenticationTokenService;
        this.userDetailsService = userDetailsService;
        this.userSignatureProvider = userSignatureProvider;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws ServletException, IOException {
        ApiContextHolder.clearContext();
        String requestUri = request.getRequestURI();
        HttpMethod method = HttpMethod.resolve(request.getMethod());
        boolean isAdminApi = isAdminApi(requestUri);
        String remoteIp = RequestUtils.getRemoteIpAddress(request);
        long timestamp = System.currentTimeMillis();

        try {
            Authentication existAuthentication =
                    SecurityContextHolder.getContext().getAuthentication();
            if (existAuthentication != null) {
                UserDetails userDetails = (UserDetails)
                        existAuthentication.getPrincipal();
                UserInfo userInfo = UserInfo.from(userDetails);
                setApiContext(isAdminApi, remoteIp, method, userInfo, timestamp);
                filterChain.doFilter(request, response);
                return;
            }

            String token = loadToken(request);
            boolean tokenExists = token != null && !token.isEmpty();
            if (!tokenExists) {
                nullNextFilter(isAdminApi, remoteIp, method, timestamp,
                        request, response, filterChain);
                return;
            }

            Long userId = authenticationTokenService.getUserId(token);
            if (userId == null) {
                nullNextFilter(isAdminApi, remoteIp, method, timestamp,
                        request, response, filterChain);
                return;
            }
            UserDetails userDetails =
                    userDetailsService.loadUserByUserId(userId);
            if (userDetails == null) {
                throw new AuthenticationException(
                        AuthErrorCode.ERROR_INVALID_TOKEN
                );
            }
            String signature = userSignatureProvider.getSignature(userId);
            TokenAuthResult result = authenticationTokenService.verifyToken(
                    token,
                    signature
            );
            if (!result.success()) {
                // although there is anonymous api access that doesn't need provides token,
                // but as long as it provides token here, we have to verify it.
                // And throw exception when failed.
                throw new BusinessRuntimeException(result.errorCode());
            }

            UserInfo userInfo = UserInfo.from(userDetails);
            Authentication authentication = UsernamePasswordAuthenticationToken.authenticated(
                    userDetails,
                    userDetails.getPassword(),
                    userDetails.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            setApiContext(isAdminApi, remoteIp, method, userInfo, timestamp);
            filterChain.doFilter(request, response);
        } finally {
            ApiContextHolder.clearContext();
        }
    }

    private static void setApiContext(boolean isAdminApi, String remoteIp,
                                      HttpMethod method, UserInfo userInfo,
                                      long timestamp) {
        ApiContextHolder.ApiContext apiContext = new ApiContextHolder.ApiContext(
                isAdminApi, remoteIp, LocaleContextHolder.getLocale(),
                method, userInfo, timestamp);
        ApiContextHolder.setContext(apiContext);
    }

    private boolean isAdminApi(String requestUri) {
        if (requestUri == null || requestUri.length() <= 10) {
            return false;
        }

        return antPathMatcher.match("/api/{version}/admin/**", requestUri);
    }

    private void nullNextFilter(boolean isAdminApi, String remoteIp, HttpMethod method, long timestamp,
                                HttpServletRequest request, HttpServletResponse response,
                                FilterChain filterChain) throws IOException, ServletException {
        setApiContext(isAdminApi, remoteIp, method, null, timestamp);
        filterChain.doFilter(request, response);
    }

    private String loadToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token == null || token.isEmpty()) {
            return request.getParameter("token");
        }
        return token;
    }
}
