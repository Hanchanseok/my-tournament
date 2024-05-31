package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.dtos.TournamentCommentDto;
import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.MyPageMapper;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.mappers.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MyPageService {
    private final UserMapper userMapper;
    private final TournamentMapper tournamentMapper;
    private final MyPageMapper myPageMapper;

    @Autowired
    public MyPageService(UserMapper userMapper, TournamentMapper tournamentMapper, MyPageMapper myPageMapper) {
        this.userMapper = userMapper;
        this.tournamentMapper = tournamentMapper;
        this.myPageMapper = myPageMapper;
    }

    public UserEntity getUser(UserEntity user) {
        if (user == null) return null;
        return this.userMapper.selectUserByEmail(user.getEmail());
    }

    public int getTournamentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectTournamentCountByEmail(user.getEmail());
    }

    public int getCommentCount(UserEntity user) {
        if (user == null) return 0;
        return this.tournamentMapper.selectCommentCountByEmail(user.getEmail());
    }

    public TournamentEntity[] getMyTournaments(UserEntity user) {
        if (user == null) return null;
        return this.myPageMapper.selectTournamentsByEmail(user.getEmail());
    }

    public TournamentCommentDto[] getMyComments(UserEntity user) {
        if (user == null) return null;
        return this.myPageMapper.selectCommentsByEmail(user.getEmail());
    }
}
