package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.GoodsOrderDto;
import dev.hcs.mytournament.dtos.GoodsReviewDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.*;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.StoreService;
import jakarta.servlet.http.HttpSession;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Controller
@RequestMapping(value = "/store")
public class StoreController {
    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    // 상점 홈페이지
    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getStore(
            @RequestParam(value = "by", required = false, defaultValue = "latest") String by,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setBy(by);
        search.setKeyword(keyword);
        search.setRequestPage(page);
        GoodsEntity[] goodsList = this.storeService.getGoods(search);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsList", goodsList);
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/store/index");
        return modelAndView;
    }

    // 굿즈 판매 페이지
    @RequestMapping(value = "/goods", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoods(
            @RequestParam(value = "index", required = false, defaultValue = "1") int index,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            HttpSession session,
            SearchDto search
    ) {
        search.setRequestPage(page);
        search.setCountPerPage(5);
        UserEntity user = (UserEntity) session.getAttribute("user");
        GoodsEntity goods = this.storeService.getGoodsByIndex(index);
        GoodsImageEntity[] goodsImages = this.storeService.getGoodsImageByGoodsIndex(index);
        UserAddressEntity[] userAddress = this.storeService.selectUserAddresses(user);
        GoodsReviewDto[] goodsReviews = this.storeService.getGoodsReviewDto(search, index);
        double average = this.storeService.getAverage(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("goodsImages", goodsImages);
        modelAndView.addObject("userAddress", userAddress);
        modelAndView.addObject("average", average);
        if ( goodsReviews.length == 0 ) {
            modelAndView.addObject("goodsReviews", null);
        } else {
            modelAndView.addObject("goodsReviews", goodsReviews);
        }
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/store/goods");
        return modelAndView;
    }

    // 굿즈 업로드 페이지로
    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/store/upload");
        return modelAndView;
    }

    // 굿즈 업로드
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUpload(
            GoodsEntity goods,
            GoodsImageEntity goodsImage,
            MultipartFile[] files,
            HttpSession session
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.uploadGoods(user,goods, goodsImage, files);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 썸네일
    @RequestMapping(value = "/thumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getGoodsThumbnail(@RequestParam("index") int index) {
        GoodsEntity goods = this.storeService.getGoodsByIndex(index);
        if (goods == null) {
            return ResponseEntity.notFound().build();   // Not Found (404)
        }
        return ResponseEntity.ok()  // OK (200)
                .contentType(MediaType.parseMediaType(goods.getThumbnailContentType()))
                .contentLength(goods.getThumbnail().length)
                .body(goods.getThumbnail());
    }

    // 굿즈 이미지들
    @RequestMapping(value = "/image", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getGoodsImage(@RequestParam("index") int index) {
        GoodsImageEntity goodsImage = this.storeService.getGoodsImageByIndex(index);
        if (goodsImage == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(goodsImage.getImageContentType()))
                .contentLength(goodsImage.getImage().length)
                .body(goodsImage.getImage());
    }

    // 굿즈 주문
    @RequestMapping(value = "/order", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postOrder(GoodsOrderEntity goodsOrder, UserAddressEntity userAddress, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.orderGoods(goodsOrder, userAddress, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        responseObject.put("orderIndex", goodsOrder.getIndex());
        return responseObject.toString();
    }

    // 굿즈 주문 취소
    @RequestMapping(value = "/order", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteOrder(
            @RequestParam("index") int index,
            @RequestParam("amount") int amount,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.deleteGoodsOrder(index, user, amount);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 저장된 내 주소 삭제
    @RequestMapping(value = "/myAddress", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteMyAddress(@RequestParam("addressIndex")int index) {
        Result result = this.storeService.deleteMyAddress(index);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 찜하기
    @RequestMapping(value = "/wishlist", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postWishlist(
            @RequestParam("goodsIndex")int goodsIndex,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.postWishlist(user, goodsIndex);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 굿즈 주문서 페이지
    @RequestMapping(value = "/purchaseOrder", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPurchaseOrder(
            @RequestParam(value = "index", required = true) int index
    ) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsOrder", this.storeService.goodsOrderByIndex(index));
        modelAndView.setViewName("/store/purchaseOrder");
        return modelAndView;
    }

    // 굿즈 결제
    @RequestMapping(value = "/purchaseOrder", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchPurchaseOrder(
            GoodsOrderEntity goodsOrder,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.payGoods(goodsOrder, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 해당 유저의 아직 결재 안 된 제품 목록 불러오기
    @RequestMapping(value = "/orderList", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getOrderList(HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        GoodsOrderDto[] goodsOrder = this.storeService.getOrderList(user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("goodsOrder", goodsOrder);
        return responseObject.toString();
    }

    // 리뷰 이미지
    @RequestMapping(value = "/reviewImage", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getReviewImage(@RequestParam("index") int index) {
        GoodsReviewImageEntity reviewImage = this.storeService.getReviewImageByIndex(index);
        if (reviewImage == null) {
            return ResponseEntity.notFound().build();   // Not Found (404)
        }
        return ResponseEntity.ok()  // OK (200)
                .contentType(MediaType.parseMediaType(reviewImage.getImageContentType()))
                .contentLength(reviewImage.getImage().length)
                .body(reviewImage.getImage());
    }

    // 해당 인덱스의 리뷰와 이미지들 불러옴
    @RequestMapping(value = "/reviewDetail", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getReviewDetail(@RequestParam("index") int index) {
        GoodsReviewDto goodsReview = this.storeService.getGoodsReviewByIndex(index);
        GoodsReviewImageEntity[] goodsReviewImages = this.storeService.getGoodsReviewImage(index);
        JSONObject responseObject = new JSONObject();
        responseObject.put("nickname", goodsReview.getNickname());
        responseObject.put("content", goodsReview.getContent());
        responseObject.put("createdAt", goodsReview.getCreatedAt());
        responseObject.put("ratingStar", goodsReview.getRatingStar());
        responseObject.put("goodsReviewImages", goodsReviewImages);
        return responseObject.toString();
    }

    // 리뷰 신고
    @RequestMapping(value = "/reviewReport", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchReviewReport(
            @RequestParam("index") int index,
            HttpSession session
    ) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.storeService.reportReview(index, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}
