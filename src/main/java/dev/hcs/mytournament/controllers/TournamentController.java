package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.survices.TournamentService;
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

    @RequestMapping(value = "/upload", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getUpload() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/tournament/upload");
        return modelAndView;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public String postUpload(
            TournamentEntity tournament,
            TournamentProductEntity product,
            UserEntity user,
            @RequestParam("files")MultipartFile[] files,
            @RequestParam("productNames") String[] productNames,
            @RequestParam("tournamentThumbnail") MultipartFile thumbnail
    ) throws IOException {
        Result result = this.tournamentService.uploadTournament(
                tournament, product, user, thumbnail, files, productNames
        );
        JSONObject responseObject = new JSONObject();
        responseObject.put("result", result.name().toLowerCase());
        return responseObject.toString();
    }

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
}
