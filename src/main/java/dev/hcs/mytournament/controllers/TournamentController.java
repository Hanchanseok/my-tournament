package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.RankingDto;
import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.TournamentCommentEntity;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.TournamentService;
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
@RequestMapping(value = "/tournament")
public class TournamentController {
    private final TournamentService tournamentService;

    @Autowired
    public TournamentController(TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    // 업로드 페이지로
    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/tournament/upload");
        return modelAndView;
    }

    // 대회 업로드
    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUpload(
            TournamentEntity tournament,
            TournamentProductEntity product,
            HttpSession session,
            @RequestParam("files")MultipartFile[] files,
            @RequestParam("productNames") String[] productNames,
            @RequestParam("tournamentThumbnail") MultipartFile thumbnail
    ) throws IOException {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.tournamentService.uploadTournament(
                tournament, product, user, thumbnail, files, productNames
        );
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 대회 썸네일
    @RequestMapping(value = "/thumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getTournamentThumbnail(@RequestParam("index") int index) {
        TournamentEntity tournament = this.tournamentService.get(index);
        if (tournament == null) {
            return ResponseEntity.notFound().build();   // Not Found (404)
        }
        return ResponseEntity.ok()  // OK (200)
                .contentType(MediaType.parseMediaType(tournament.getThumbnailContentType()))
                .contentLength(tournament.getThumbnail().length)
                .body(tournament.getThumbnail());
    }

    // 요소들의 썸네일
    @RequestMapping(value = "/productThumbnail", method = RequestMethod.GET)
    public ResponseEntity<byte[]> getProductThumbnail(@RequestParam("index") int index) {
        TournamentProductEntity product = this.tournamentService.getProduct(index);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(product.getProductThumbnailContentType()))
                .contentLength(product.getProductThumbnail().length)
                .body(product.getProductThumbnail());
    }

    // 대회 상세보기
    @RequestMapping(value = "/detail", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getDetail(@RequestParam("index") int index) {
        TournamentEntity tournament = this.tournamentService.get(index);
        TournamentProductEntity[] products = this.tournamentService.getProducts(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournament", tournament);
        modelAndView.addObject("products", products);
        modelAndView.setViewName("/tournament/detail");
        return modelAndView;
    }

    // 랭킹 조회
    @RequestMapping(value = "/ranking", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getRanking(
            @RequestParam("index") int index,
            @RequestParam(value = "page",required = false, defaultValue = "1") int page,
            SearchDto search
    ) {
        search.setRequestPage(page);
        search.setCountPerPage(5);
        RankingDto[] products = this.tournamentService.getRanking(index);
        TournamentEntity tournament = this.tournamentService.get(index);
        TournamentCommentDto[] comments = this.tournamentService.getComments(index, search);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("products", products);
        modelAndView.addObject("tournament", tournament);
        modelAndView.addObject("comments", comments);
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/tournament/ranking");
        return modelAndView;
    }

    // 랭킹 조회 부분에서 댓글 쓰기
    @RequestMapping(value = "/ranking", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postComment(TournamentCommentEntity comment, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.tournamentService.writeComment(comment, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 랭킹 조회 부분 댓글 업데이트
    @RequestMapping(value = "/ranking", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchComment(TournamentCommentEntity comment, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.tournamentService.updateComments(comment, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 랭킹 조회 부분 댓글 삭제
    @RequestMapping(value = "/ranking", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String deleteComment(TournamentCommentEntity comment, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.tournamentService.deleteComment(comment, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

    // 랭킹 조회 부분 댓글 신고
    @RequestMapping(value = "/ranking/report", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String reportComment(TournamentCommentEntity comment, HttpSession session) {
        UserEntity user = (UserEntity) session.getAttribute("user");
        Result result = this.tournamentService.reportComment(comment, user);
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }


    // 플레이
    @RequestMapping(value = "/play", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getPlay(@RequestParam("index") int index) {
        TournamentEntity tournament = this.tournamentService.get(index);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("index", index);
        modelAndView.addObject("tournament", tournament);
        modelAndView.setViewName("/tournament/play");
        return modelAndView;
    }

    // 플레이 시 대회 불러오기
    @RequestMapping(value = "/loadTournament", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String getLoadTournament(@RequestParam("index") int index) {
        TournamentEntity tournament = this.tournamentService.get(index);
        TournamentProductEntity[] products = this.tournamentService.getProducts(index);
        JSONObject responseObject = new JSONObject();
        responseObject.put("tournamentTitle", tournament.getTitle());
        responseObject.put("products", products);
        return responseObject.toString();
    }
    
    // 플레이 완료 후 점수 수정
    @RequestMapping(value = "/loadTournament", method = RequestMethod.PATCH, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String patchLoadTournament(
            @RequestParam("round4One") String round4One,
            @RequestParam("round4Two") String round4Two,
            @RequestParam("runnerUp") String runnerUp,
            @RequestParam("champion") String champion,
            @RequestParam("tournamentIndex") int tournamentIndex
    ) {
        Result result = this.tournamentService.updateProduct(
                Integer.parseInt(round4One), Integer.parseInt(round4Two), Integer.parseInt(runnerUp), Integer.parseInt(champion), tournamentIndex
        );
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }
}
