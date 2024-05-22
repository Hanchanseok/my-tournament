package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.AdminService;
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
@RequestMapping(value = "/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @RequestMapping(value = "/recognize", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRecognize() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", this.adminService.getTournaments());
        modelAndView.setViewName("/admin/recognize");
        return modelAndView;
    }

    @RequestMapping(value = "/recognize", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchRecognize(
            @RequestParam(value = "index") int index
    ) {
        Result result = this.adminService.recognizeTournament(index);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getAccounts() {
        UserEntity[] users = this.adminService.getUsers();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/admin/accounts");
        modelAndView.addObject("users", users);
        return modelAndView;
    }

    @RequestMapping(value = "/accounts", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchAccounts(@RequestParam(value = "email")String email) {
        Result result = this.adminService.suspendUser(email);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}
