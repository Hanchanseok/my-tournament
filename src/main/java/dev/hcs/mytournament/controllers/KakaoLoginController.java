package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.KakaoService;
import dev.hcs.mytournament.survices.UserService;
import jakarta.servlet.http.HttpSession;
import org.eclipse.angus.mail.iap.CommandFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.HashMap;

@Controller
@RequestMapping("")
public class KakaoLoginController {
    private final KakaoService kakaoService;
    private final UserService userService;

    private String client_id = "8908f5fd6288606dcad177472698dfbf";
    private String redirect_uri = "http://localhost:8080/auth_kakao";

    @Autowired
    public KakaoLoginController(KakaoService kakaoService, UserService userService) {
        this.kakaoService = kakaoService;
        this.userService = userService;
    }

    @RequestMapping(value = "/auth_kakao", method = RequestMethod.GET)
    public String authKakao(
            @RequestParam("code") String code,
            UserEntity user,
            HttpSession session
    ) throws IOException {
        String accessToken = this.kakaoService.getAccessTokenFromKakao(client_id, code);
        HashMap<String, Object> userInfo = this.kakaoService.getUserInfo(accessToken);
        System.out.println("id : " + userInfo.get("id"));

        // 로그인, 회원가입 로직 추가
        // 만약 처음 로그인 시, 회원 DB에 해당 이메일이 없다면
        if (this.userService.getUserByEmail( "kakao_" + userInfo.get("email").toString() ) == null) {
            // 회원가입하기
            this.kakaoService.kakaoRegister(userInfo, user);
        }
        // 로그인
        UserEntity loginUser = this.kakaoService.kakaoLogin(userInfo, user);
        if (loginUser == null) {
            return "redirect:/user/login";
        }
        session.setAttribute("user", loginUser);

        return "redirect:/";
    }

    // 카카오 로그아웃
    @RequestMapping(value = "/logout_kakao", method = RequestMethod.GET)
    public String logoutKakao(HttpSession session) {
        session.setAttribute("user", null);
        return "redirect:/";
    }
}
