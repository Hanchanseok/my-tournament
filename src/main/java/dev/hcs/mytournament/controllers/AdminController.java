package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.AdminService;
import dev.hcs.mytournament.survices.StoreService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

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
    public ModelAndView getRecognize(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", this.adminService.getTournaments(search));
        modelAndView.addObject("paging", search);
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
    public ModelAndView getAccounts(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("users", this.adminService.getUsers(search));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/admin/accounts");
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

    // 해당 유저에게 관리자 권한 주기
    @RequestMapping(value = "/patchAdmin", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchAdmin(
            @RequestParam(value = "email")String email,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.patchAdmin(email, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/reportedComments", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getReportedComments(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("comments", this.adminService.getReportedComments(search));
        modelAndView.addObject("paging", search);
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

    // 굿즈 수정 페이지로(팝업)
    @RequestMapping(value = "/updateGoodsInfo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateGoodsInfo(
            @RequestParam(value = "index",required = false, defaultValue = "1")int index
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goods", this.adminService.getGoodsByIndex(index));
        modelAndView.addObject("goodsImages", this.adminService.getGoodsImages(index));
        modelAndView.setViewName("/admin/updateGoodsInfo");
        return modelAndView;
    }

    // 굿즈 수정
    @RequestMapping(value = "/updateGoodsInfo", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchUpdateGoodsInfo(
            @RequestParam(value = "files", required = false) MultipartFile[] files,
            GoodsEntity goods,
            @RequestParam(value = "deletedImagesIndex", required = false) int[] deletedImagesIndex,
            HttpSession session
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.goodsModify(goods, files, deletedImagesIndex, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 판매 여부 전환
    @RequestMapping(value = "/changeGoodsSale", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchChangeGoodsSale(
            @RequestParam(value = "index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.changeGoodsSale(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 주문목록 페이지
    @RequestMapping(value = "/goodsOrder", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsOrder(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpSession session,
            SearchDto search
    ) {
        search.setRequestPage(page);
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsOrders", this.adminService.getGoodsOrders(search, user));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/admin/goodsOrder");
        return modelAndView;
    }

    // 유저들의 굿즈 주문 취소/삭제
    @RequestMapping(value = "/goodsOrder", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteGoodsOrder(
            @RequestParam("index")int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.deleteGoodsOrder(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 주문 상세 페이지
    @RequestMapping(value = "/goodsOrderDetail", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsOrderDetail(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsOrder", this.adminService.getGoodsOrderByIndex(index, user));
        modelAndView.setViewName("/admin/goodsOrderDetail");
        return modelAndView;
    }

    // 굿즈 리뷰 목록 페이지
    @RequestMapping(value = "/goodsReview", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsReview(
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpSession session,
            SearchDto search
    ) {
        search.setRequestPage(page);
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsReviews", this.adminService.getGoodsReviews(search, user));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/admin/goodsReviewList");
        return modelAndView;
    }

    // 굿즈 리뷰 상세 페이지
    @RequestMapping(value = "/goodsReview/detail", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsReviewDetail(@RequestParam("index") int index) {
        GoodsReviewDto goodsReview = this.adminService.getGoodsReviewByIndex(index);
        GoodsReviewImageEntity[] goodsReviewImages = this.adminService.getGoodsReviewImages(goodsReview.getIndex());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsReview", goodsReview);
        modelAndView.addObject("goodsReviewImages", goodsReviewImages);
        modelAndView.setViewName("/myPage/goodsReviewInfo");
        return modelAndView;
    }

    @RequestMapping(value = "/goodsReview", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchGoodsReview(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.reviewNa(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    @RequestMapping(value = "/goodsReview", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteGoodsReview(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.deleteReview(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 삭제
    @RequestMapping(value = "/deleteGoods", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteGoods(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.deleteGoods(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 토너먼트 삭제
    @RequestMapping(value = "/deleteTournament", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteTournament(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.adminService.deleteTournament(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}
