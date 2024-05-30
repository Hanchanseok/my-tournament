package dev.hcs.mytournament.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/myPage")
public class MyPageController {
    @RequestMapping(value = "/myInfo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyInfo() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/myInfo");
        return modelAndView;
    }
}
