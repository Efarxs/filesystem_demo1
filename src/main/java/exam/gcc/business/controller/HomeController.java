package exam.gcc.business.controller;

import exam.gcc.business.service.UserService;
import exam.gcc.domain.User;
import exam.gcc.framework.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller

public class HomeController extends BaseController {
    @Autowired
    private UserService userService;

    /**
     * 开通VIP
     * @param request
     * @param response
     * @throws IOException
     */
    @GetMapping("/ktvip")
    public void ktvip(HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = getUser();
        user.setVip(1);
        userService.updateUser(user);
        request.getSession().setAttribute("user",user);
        response.sendRedirect("/home");
    }

    @GetMapping("/index")
    public String index() {
        return "index";
    }

    @GetMapping("/toRegister")
    public String register() {
        return "register";
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("user",getUser());
        return "home";
    }
}