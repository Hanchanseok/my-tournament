package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/myPage")
public class MyPageController {
    private final MyPageService myPageService;

    @Autowired
    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    @RequestMapping(value = "/myInfo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyInfo(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("myInfo", this.myPageService.getUser(user));
        modelAndView.addObject("tournamentCount", this.myPageService.getTournamentCount(user));
        modelAndView.addObject("commentCount", this.myPageService.getCommentCount(user));
        modelAndView.setViewName("/myPage/myInfo");
        return modelAndView;
    }

    @RequestMapping(value = "/myTournament", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyTournament(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", this.myPageService.getMyTournaments(user));
        modelAndView.setViewName("/myPage/myTournament");
        return modelAndView;
    }

    @RequestMapping(value = "/myComment", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyComment(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("comments", this.myPageService.getMyComments(user));
        modelAndView.setViewName("/myPage/myComment");
        return modelAndView;
    }

    @RequestMapping(value = "/updateMyInfo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateMyInfo(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        if (!user.isKakao()) {
            modelAndView.setViewName("/myPage/checkMyPassword");
        } else {
            modelAndView.setViewName("/myPage/updateMyInfoOption");
        }
        return  modelAndView;
    }

    @RequestMapping(value = "/updateMyInfo", method = RequestMethod.POST, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCheckMyPassword(HttpSession session, @RequestParam("password") String password) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.checkMyPassword(user, password);
        ModelAndView modelAndView = new ModelAndView();
        if (result == CommonResult.FAILURE) {
            modelAndView.addObject("message", "failure");
            modelAndView.setViewName("/myPage/checkMyPassword");
        } else if (result == CommonResult.SUCCESS) {
            modelAndView.setViewName("/myPage/updateMyInfoOption");
        }
        return modelAndView;
    }

    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateMyNickname(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/updateMyNickname");
        return modelAndView;
    }

    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchUpdateMyNickname(
            @RequestParam("nickname") String nickname
    ) {
        Result result = this.myPageService.checkDuplicateNickname(nickname);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUpdateMyNickname(
            HttpSession session,
            @RequestParam("nickname") String nickname
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.updateMyNickname(user, nickname);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/updateMyPassword", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateMyPassword(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/updateMyPassword");
        return modelAndView;
    }
}
