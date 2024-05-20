package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AdminService {
    private final TournamentMapper tournamentMapper;

    @Autowired
    public AdminService(TournamentMapper tournamentMapper) {
        this.tournamentMapper = tournamentMapper;
    }

    public TournamentEntity[] getTournaments() {
        return this.tournamentMapper.selectTournaments();
    }

    public Result recognizeTournament(int index) {
        TournamentEntity dbTournament = this.tournamentMapper.selectTournamentByIndex(index);
        if (dbTournament == null) {
            return CommonResult.FAILURE;
        }

        if (!dbTournament.isRecognized()) {
            dbTournament.setRecognized(true);
        } else {
            dbTournament.setRecognized(false);
        }

        return this.tournamentMapper.updateTournament(dbTournament) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }
}
