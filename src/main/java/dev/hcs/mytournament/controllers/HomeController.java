package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.survices.TournamentService;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping(value = "/")
public class HomeController {
    private final TournamentService tournamentService;

    @Autowired
    public HomeController(TournamentMapper tournamentMapper, TournamentService tournamentService) {
        this.tournamentService = tournamentService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView getIndex(
            @RequestParam(value = "by", required = false, defaultValue = "latest") String by,
            @RequestParam(value = "keyword", required = false, defaultValue = "") String keyword,
            @RequestParam(value = "page", required = false, defaultValue = "1") int page,
            SearchDto search,
            HttpSession session
    ) {
        search.setBy(by);
        search.setKeyword(keyword);
        search.setRequestPage(page);
        // 만약 카카오 로그인 실패시(정지 or 삭제) 이곳으로 오게 되는데, 이 세션을 지워야 다시 로그인 페이지 이동 가능
        session.setAttribute("loginFailure", null);
        TournamentEntity[] tournaments = tournamentService.getTournaments(search);
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", tournaments);
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/home/index");
        return modelAndView;
    }
}
