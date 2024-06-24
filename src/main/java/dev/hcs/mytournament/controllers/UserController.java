package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.EmailAuthEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.UserService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.security.NoSuchAlgorithmException;

@Controller
@RequestMapping(value = "/user")
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 페이지
    @RequestMapping(value = "/login", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogin() {
        String client_id = "8908f5fd6288606dcad177472698dfbf";
        String redirect_uri = "http://localhost:8080/auth_kakao";
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="+ client_id +"&redirect_uri="+ redirect_uri;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("location", location);
        modelAndView.setViewName("/user/login");
        return modelAndView;
    }

    // 회원가입 페이지
    @RequestMapping(value = "/register", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRegister() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/register");
        return modelAndView;
    }

    // 비밀번호 변경 페이지
    @RequestMapping(value = "/resetPassword", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getResetPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/user/resetPassword");
        return modelAndView;
    }

    // 이메일 인증코드 얻기
    @RequestMapping(value = "/registerEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegisterEmail(EmailAuthEntity emailAuth) throws MessagingException, NoSuchAlgorithmException {
        Result result = this.userService.sendRegisterEmail(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    // 이메일 인증
    @RequestMapping(value = "/registerEmail", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRegisterEmail(EmailAuthEntity emailAuth) {
        Result result = this.userService.verifyEmailAuth(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 회원가입하기
    @RequestMapping(value = "/register", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postRegister(
            EmailAuthEntity emailAuth,
            UserEntity user,
            @RequestParam("passwordCheck")String passwordCheck
    ) {
        Result result = this.userService.register(emailAuth, user, passwordCheck);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    // 비밀번호 변경 이메일 인증코드 받기
    @RequestMapping(value = "/resetPasswordEmail", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postResetPasswordEmail(EmailAuthEntity emailAuth) throws MessagingException, NoSuchAlgorithmException {
        Result result = this.userService.sendResetPasswordEmail(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        if (result == CommonResult.SUCCESS) {
            responseObject.put("salt", emailAuth.getSalt());
        }
        return responseObject.toString();
    }

    // 비밀번호 변경 이메일 인증
    @RequestMapping(value = "/resetPasswordEmail", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchResetPasswordEmail(EmailAuthEntity emailAuth) {
        Result result = this.userService.verifyEmailAuth(emailAuth);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 비밀번호 최종 변경
    @RequestMapping(value = "/resetPassword", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchResetPassword(
            EmailAuthEntity emailAuth,
            UserEntity user,
            @RequestParam("passwordCheck")String passwordCheck
    ) {
        Result result = this.userService.resetPassword(emailAuth, user, passwordCheck);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 로그인
    @RequestMapping(value = "/login", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postLogin(HttpSession session, UserEntity loginUser) {
        Result result = this.userService.login(loginUser);
        if (result == CommonResult.SUCCESS) {
            session.setAttribute("user", loginUser);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 로그아웃
    @RequestMapping(value = "/logout", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getLogout(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");    // 로그인한 유저 정보 불러오기

        String client_id = "8908f5fd6288606dcad177472698dfbf";
        String redirect_uri = "http://localhost:8080/logout_kakao";

        ModelAndView modelAndView = new ModelAndView();
        if (user.isKakao()) {   // 만약 카카오 로그인이라면
            String kakaoLogoutUrl = "https://kauth.kakao.com/oauth/logout?client_id=" + client_id + "&logout_redirect_uri=" + redirect_uri;
            return new ModelAndView("redirect:" + kakaoLogoutUrl);
        } else {                // 일반 로그인이라면
            session.setAttribute("user", null);
            modelAndView.setViewName("redirect:/");
            return modelAndView;
        }
    }
}
