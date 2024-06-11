package dev.hcs.mytournament.controllers;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/store")
public class StoreController {

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getStore() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/store/index");
        return modelAndView;
    }

    @RequestMapping(value = "/goods", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoods() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/store/goods");
        return modelAndView;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/store/upload");
        return modelAndView;
    }
}
