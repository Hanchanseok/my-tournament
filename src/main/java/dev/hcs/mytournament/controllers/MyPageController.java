package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.user.UpdatePasswordResult;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping(value = "/myPage")
public class MyPageController {
    private final MyPageService myPageService;

    @Autowired
    public MyPageController(MyPageService myPageService) {
        this.myPageService = myPageService;
    }

    // 내 정보 페이지
    @RequestMapping(value = "/myInfo", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyInfo(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("myInfo", this.myPageService.getUser(user));
        modelAndView.addObject("tournamentCount", this.myPageService.getTournamentCount(user));
        modelAndView.addObject("commentCount", this.myPageService.getCommentCount(user));
        modelAndView.addObject("orderCount", this.myPageService.getOrderCount(user));
        modelAndView.setViewName("/myPage/myInfo");
        return modelAndView;
    }

    // 내 토너먼트 페이지
    @RequestMapping(value = "/myTournament", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyTournament(
            HttpSession session,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", this.myPageService.getMyTournaments(user, search));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/myPage/myTournament");
        return modelAndView;
    }

    // 내 댓글 페이지
    @RequestMapping(value = "/myComment", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyComment(
            HttpSession session,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("comments", this.myPageService.getMyComments(user, search));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/myPage/myComment");
        return modelAndView;
    }

    // 내 정보 수정(카카오일 경우 비밀번호 입력 면제)
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

    // 비밀번호 입력 성공시 정보 수정페이지로
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

    // 닉네임 변경 페이지
    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateMyNickname() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/updateMyNickname");
        return modelAndView;
    }

    // 닉네임 변경 전 닉네임 중복 검사
    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUpdateMyNickname(
            @RequestParam("nickname") String nickname
    ) {
        Result result = this.myPageService.checkDuplicateNickname(nickname);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 닉네임 최종 변경
    @RequestMapping(value = "/updateMyNickname", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchUpdateMyNickname(
            HttpSession session,
            @RequestParam("nickname") String nickname
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.updateMyNickname(user, nickname);
        if (result == UpdatePasswordResult.SUCCESS) {
            session.setAttribute("user", user);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 비밀번호 변경 페이지
    @RequestMapping(value = "/updateMyPassword", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpdateMyPassword() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/updateMyPassword");
        return modelAndView;
    }

    // 비밀번호 최종 변경
    @RequestMapping(value = "/updateMyPassword", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchUpdateMyPassword(
            @RequestParam("password") String password,
            @RequestParam("passwordCheck") String passwordCheck,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.updateMyPassword(user, password, passwordCheck);
        if (result == UpdatePasswordResult.SUCCESS) {
            session.setAttribute("user", user);
        }
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 회원 탈퇴 페이지
    @RequestMapping(value = "/deleteMyAccount", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getDeleteMyAccount() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/myPage/deleteMyAccount");
        return modelAndView;
    }

    // 회원 탈퇴
    @RequestMapping(value = "/deleteMyAccount", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteMyAccount(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.deleteMyAccount(user);
        session.setAttribute("user", null);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 찜 목록 페이지로
    @RequestMapping(value = "/myWishlist", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyWishlist(
            HttpSession session,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        UserEntity user = (UserEntity) session.getAttribute("user");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("wishlists", this.myPageService.getMyWishlist(search, user));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/myPage/myWishlist");
        return modelAndView;
    }

    // 굿즈 찜 목록 삭제
    @RequestMapping(value = "/myWishlist", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteMyWishlist(
            @RequestParam(value = "goodsIndex") int goodsIndex,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.deleteMyWishlist(goodsIndex, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    // 굿즈 주문내역 페이지로
    @RequestMapping(value = "myGoodsOrder", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyGoodsOrder(
            @RequestParam(value = "page", required = false, defaultValue = "1")int page,
            HttpSession session,
            SearchDto search
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        search.setRequestPage(page);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsOrders", this.myPageService.getMyGoodsOrder(search, user));
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/myPage/myGoodsOrder");
        return modelAndView;
    }

    // 굿즈 리뷰 페이지로
    @RequestMapping(value = "/myGoodsOrder/goodsReview", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoodsReview(@RequestParam("index") int index) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsOrderIndex", index);
        modelAndView.setViewName("/myPage/goodsReview");
        return modelAndView;
    }

    // 굿즈 리뷰 작성
    @RequestMapping(value = "/myGoodsOrder/goodsReview", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postGoodsReview(
            GoodsReviewEntity goodsReview,
            @RequestParam(value = "files", required = false)MultipartFile[] files,
            HttpSession session
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.writeReview(goodsReview, files, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 내가 쓴 굿즈 리뷰 페이지로
    @RequestMapping(value = "/myGoodsOrder/myGoodsReview", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getMyGoodsReview(@RequestParam("index") int index) {
        GoodsReviewDto goodsReview = this.myPageService.getGoodsReviewByGoodsOrderIndex(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsReview", goodsReview);
        if (goodsReview != null) {
            GoodsReviewImageEntity[] goodsReviewImages = this.myPageService.getGoodsReviewImages(goodsReview.getIndex());
            modelAndView.addObject("goodsReviewImages", goodsReviewImages);
        }
        modelAndView.setViewName("/myPage/goodsReviewInfo");
        return modelAndView;
    }

    // 내 토너먼트 삭제
    @RequestMapping(value = "/deleteTournament", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteTournament(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.deleteTournament(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 토너먼트 수정 페이지로
    @RequestMapping(value = "/myTournament/modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getModify(
            @RequestParam("index") int index
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournament", this.myPageService.getTournamentByIndex(index));
        modelAndView.setViewName("/myPage/modifyTournamentTitle");
        return modelAndView;
    }

    // 토너먼트 타이틀 수정
    @RequestMapping(value = "/myTournament/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postModify(
            TournamentEntity tournament,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.modifyTournament(tournament, file, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 토너먼트 요소 페이지로
    @RequestMapping(value = "/myTournament/product", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getProduct(
            @RequestParam("index") int index
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("products", this.myPageService.getProductByTournamentIndex(index));
        modelAndView.setViewName("/myPage/tournamentProduct");
        return modelAndView;
    }

    // 토너먼트 요소 수정 페이지로
    @RequestMapping(value = "/myTournament/product/modify", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getProductModify(
            @RequestParam("index") int index
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("product", this.myPageService.getProductByIndex(index));
        modelAndView.setViewName("/myPage/modifyTournamentProduct");
        return modelAndView;
    }

    // 토너먼트 요소 수정
    @RequestMapping(value = "/myTournament/product/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postProductModify(
            TournamentProductEntity product,
            @RequestParam(value = "file", required = false) MultipartFile file,
            HttpSession session
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.myPageService.modifyProduct(product, file, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}
