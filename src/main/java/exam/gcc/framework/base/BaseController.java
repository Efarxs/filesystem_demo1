package exam.gcc.framework.base;

import exam.gcc.domain.User;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

public class BaseController {

    protected HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
    }

    protected HttpSession getSession(){
        return getRequest().getSession(true);
    }

    protected User getUser(){
        HttpSession session = getSession();
        return (User)session.getAttribute("user");
    }
}
