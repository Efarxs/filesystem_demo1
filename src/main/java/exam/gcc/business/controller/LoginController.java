package exam.gcc.business.controller;

import exam.gcc.business.service.UserService;
import exam.gcc.domain.User;
import exam.gcc.framework.base.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class LoginController extends BaseController {
    @Autowired
    private UserService userService;
    @PostMapping("/registerUserAccount")
    public String registerUserAccount(@RequestParam String username,@RequestParam String password,@RequestParam String realname,
                                      @RequestParam String confirmPassword) {
        if(StringUtils.isEmpty(password) || !password.equals(confirmPassword)){
           return "两次密码输入不一致！";
        }
        User user = new User();
        user.setPassword(password);
        user.setUsername(username);
        user.setRealname(realname);
        if(userService.saveUser(user)==-1){
            return "用户已存在！";
        }
        return "success";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        Optional<User> userOptional = userService.findByUsername(username);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (userService.checkPassword(password, user)) {
                getSession().setAttribute("user",user);
                return "success";
            }
        }
        return "fail";
    }
}
