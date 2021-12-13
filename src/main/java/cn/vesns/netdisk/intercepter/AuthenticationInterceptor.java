package cn.vesns.netdisk.intercepter;

import cn.vesns.netdisk.pojo.User;

import cn.vesns.netdisk.service.UserService;
import cn.vesns.netdisk.util.SessionUtil;
import com.qiwenshare.common.exception.NotLoginException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * token验证拦截
 */
@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {
    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 取得token
        //HttpHeaders.AUTHORIZATION
        String token = request.getHeader("token");
        if ("undefined".equals(token) || StringUtils.isEmpty(token)) {
            token = request.getParameter("token不存在");
        }
        if ("undefined".equals(token) || StringUtils.isEmpty(token)) {
            throw new NotLoginException("token不存在");
        }
//        if (!token.startsWith("Bearer ")) {
//            throw new NotLoginException("token格式错误");
//        }
        token = token.replace("Bearer ", "");
        User userBean = userService.getUserByToken(token);
        SessionUtil.setSession(userBean);
        if (userBean == null) {
            throw new NotLoginException();
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception { }


    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {}


}
