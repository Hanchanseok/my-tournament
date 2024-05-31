package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.survices.MyPageService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

}
