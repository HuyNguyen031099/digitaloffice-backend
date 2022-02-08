package com.datn.doffice.config.security;

import com.datn.doffice.annotation.Rbac;
import com.datn.doffice.defines.RoleTypes;
import com.datn.doffice.exceptions.RequestNotFoundException;
import com.datn.doffice.exceptions.TokenExpiredException;
import com.datn.doffice.exceptions.UnauthorizedException;
import com.datn.doffice.utils.ResponseUtil;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private MyUserDetailsService myUserDetailsService;

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @Autowired
    private ResponseUtil responseUtil;

    @Value("${jwt.ignore-filter}")
    private boolean isIgnoreFilter;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        HandlerMethod handlerMethod = null;
        try {
            handlerMethod = getHandle(request);
            if (handlerMethod == null) {
                responseUtil.writeException(response, new RequestNotFoundException(), handlerMethod);
            } else if (isIgnoreFilter || checkIgnores(request)) {
                chain.doFilter(request, response);
            } else {
                validateRequest(request, handlerMethod);
                chain.doFilter(request, response);
            }
        } catch (Exception ex) {
            responseUtil.writeException(response, ex, handlerMethod);
        }
    }

    void validateRequest(HttpServletRequest request, HandlerMethod handlerMethod) {
        final String requestTokenHeader = request.getHeader("Authorization");

        String username = null;
        String jwtToken = null;
        // JWT Token is in the form "Bearer token". Remove Bearer word and get only the Token
        if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
            jwtToken = requestTokenHeader.substring(7);
            try {
                username = jwtUtils.getUsernameFromToken(jwtToken);
            } catch (IllegalArgumentException e) {
                log.error("Unable to get JWT Token");
                throw new UnauthorizedException();
            } catch (ExpiredJwtException e) {
                log.error("JWT Token has expired");
                throw new TokenExpiredException();
            }
        } else {
            logger.warn("JWT Token does not begin with Bearer String");
            throw new UnauthorizedException();
        }

        // Once we get the token validate it.
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            MyUserPrincipal userDetails = this.myUserDetailsService.loadUserByUsername(username);

            // if token is valid configure Spring Security to manually set
            // authentication
            if (jwtUtils.validateToken(jwtToken, userDetails)) {

                if (!checkPermission(userDetails, handlerMethod)) {
                    throw new AccessDeniedException("");
                }

                UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, null);

                usernamePasswordAuthenticationToken
                        .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // After setting the Authentication in the context, we specify
                // that the current user is authenticated. So it passes the
                // Spring Security Configurations successfully.
                SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
            }
        }

    }

    boolean checkIgnores(HttpServletRequest request) {
        return request.getRequestURI().contains("/api/auth/")
                || request.getRequestURI().contains("/authenticate")
                || request.getRequestURI().contains("/validate-token");
    }

    HandlerMethod getHandle(HttpServletRequest request) throws Exception {
        HandlerExecutionChain handlerExeChain = mapping.getHandler(request);
        if (Objects.nonNull(handlerExeChain)) {
            return (HandlerMethod) handlerExeChain.getHandler();
        }
        return null;
    }


    boolean checkPermission(MyUserPrincipal userDetails, HandlerMethod handlerMethod) {
        if (!handlerMethod.hasMethodAnnotation(Rbac.class)) {
            return true;
        }

        boolean result = false;
        Rbac handleInfo = handlerMethod.getMethodAnnotation(Rbac.class);

        List<Integer> roles = userDetails.getRoles();
        Set<Integer> permissions = userDetails.getPermissions();

//        if (handleInfo.isPrivate()) {
//            throw new RequestNotFoundException();
//        }
        if (handleInfo.isPrivate() && roles.contains(RoleTypes.ROLE_SYS_ADMIN)) {
            result = true;
        } else {
            result = checkPermissionWithRole(roles, handleInfo.roleTypes()) && checkPermissionWithFeatures(permissions, handleInfo.permissionGroups());
        }
        return result;
    }

    boolean checkPermissionWithRole(List<Integer> roles, int[] roleTypes) {
        for (int i = 0; i < roles.size(); i++) {
            for (int j = 0; j < roleTypes.length; j++) {
                if (roles.get(i) == roleTypes[j]) {
                    return true;
                }
            }
        }
        return false;
    }

    boolean checkPermissionWithFeatures(Set<Integer> permissions, int[] permissionGroups) {
        for (int i : permissionGroups) {
            if (permissions.contains(i)) {
                return true;
            }
        }
        return false;
    }
}
