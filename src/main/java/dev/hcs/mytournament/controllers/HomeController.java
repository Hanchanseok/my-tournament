package dev.hcs.mytournament.controllers;

import dev.hcs.mytournament.dtos.SearchDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.survices.TournamentService;
import jakarta.servlet.http.HttpSession;
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
            SearchDto search
    ) {
        search.setBy(by);
        search.setKeyword(keyword);
        search.setRequestPage(page);
        TournamentEntity[] tournaments = tournamentService.getTournaments(search);
//        System.out.println("requestPage : " + search.getRequestPage());
//        System.out.println("totalCount : " + search.getTotalCount());
//        System.out.println("maxPage : " + search.getMaxPage());
//        System.out.println("minPage : " + search.getMinPage());
//        System.out.println("offset : " + search.getOffset());
//        System.out.println("totalPage : " + search.getTotalPage());
//        System.out.println("beginPage : " + search.getBeginPage());
//        System.out.println("endPage : " + search.getEndPage());
//        System.out.println("showPrev : " + search.isShowPrev());
//        System.out.println("showNext : " + search.isShowNext());
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("tournaments", tournaments);
        modelAndView.addObject("paging", search);
        modelAndView.setViewName("/home/index");
        return modelAndView;
    }
}
