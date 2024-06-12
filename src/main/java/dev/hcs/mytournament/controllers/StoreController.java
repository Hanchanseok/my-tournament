package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.GoodsEntity;
import dev.hcs.mytournament.entities.GoodsImageEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
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

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getStore() {
        GoodsEntity[] goodsList = this.storeService.getGoods();
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goodsList", goodsList);
        modelAndView.setViewName("/store/index");
        return modelAndView;
    }

    @RequestMapping(value = "/goods", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getGoods(@RequestParam(value = "index", required = false, defaultValue = "1") int index) {
        GoodsEntity goods = this.storeService.getGoodsByIndex(index);
        GoodsImageEntity[] goodsImages = this.storeService.getGoodsImageByGoodsIndex(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("goods", goods);
        modelAndView.addObject("goodsImages", goodsImages);
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
}
