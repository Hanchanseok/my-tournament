package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.TournamentCommentEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.AdminService;
import dev.hcs.mytournament.survices.StoreService;
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
    private final StoreService storeService;

    @Autowired
    public AdminController(AdminService adminService, StoreService storeService) {
        this.adminService = adminService;
        this.storeService = storeService;
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

    @RequestMapping(value = "/reportedComments", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReportedComments() {
        TournamentCommentEntity[] comments = this.adminService.getReportedComments();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("comments", comments);
        modelAndView.setViewName("/admin/reportedComments");
        return modelAndView;
    }

    @RequestMapping(value = "/reportedComments", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchReportedComments(TournamentCommentEntity comment) {
        Result result = adminService.updateReportedComment(comment.getIndex());
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/reportedComments", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteReportedComments(TournamentCommentEntity comment) {
        Result result = adminService.deleteReportedComment(comment.getIndex());
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/commentDetail", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getCommentDetail(@RequestParam(value = "index")int index) {
        TournamentCommentEntity comment = this.adminService.getComment(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("comment", comment);
        modelAndView.setViewName("/admin/commentDetail");
        return modelAndView;
    }

    // 굿즈 관리 페이지로
    @RequestMapping(value = "/goodsManage", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsManage(
            @RequestParam(value = "by", required = false, defaultValue = "latest") String by,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setBy(by);
        search.setRequestPage(page);
        GoodsEntity[] goodsList = this.storeService.getGoods(search);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsList", goodsList);
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/admin/goodsManage");
        return modelAndView;
    }
}
