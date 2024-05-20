package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.entities.TournamentEntity;
import dev.hcs.mytournament.entities.TournamentProductEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.TournamentMapper;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@Service
public class TournamentService {
    private final TournamentMapper tournamentMapper;

    @Autowired
    public TournamentService(TournamentMapper tournamentMapper) {
        this.tournamentMapper = tournamentMapper;
    }

    @Transactional
    public Result uploadTournament(
            TournamentEntity tournament,
            TournamentProductEntity product,
            UserEntity user,
            MultipartFile thumbnail,
            MultipartFile[] files,
            String[] productNames
    ) throws IOException {
        if (tournament == null) {
            return CommonResult.FAILURE;
        }
        if (tournament.getTitle().length() > 50 || tournament.getTitle().length() < 2) {
            return CommonResult.FAILURE;
        }
        if (tournament.getContent().length() > 1000 || tournament.getContent().isEmpty()) {
            return CommonResult.FAILURE;
        }

        tournament.setUserEmail(user.getEmail());
        tournament.setThumbnail(thumbnail.getBytes());
        tournament.setThumbnailFileName(thumbnail.getOriginalFilename());
        tournament.setThumbnailContentType(thumbnail.getContentType());
        tournament.setPlayCount(0);
        tournament.setCreatedAt(LocalDateTime.now());
        tournament.setModifiedAt(null);
        tournament.setRecognized(false);
        this.tournamentMapper.insertTournament(tournament);

        if (files == null || files.length != 16) {
            return CommonResult.FAILURE;
        }
        if (productNames == null || productNames.length != 16) {
            return CommonResult.FAILURE;
        }
        for (int i = 0; i < files.length; i++) {
            MultipartFile file = files[i];
            String productName = productNames[i];
            if (productName.length() > 50 || productName.isEmpty()) {
                return CommonResult.FAILURE;
            }
            product.setTournamentIndex(tournament.getIndex());
            product.setProductThumbnail(file.getBytes());
            product.setProductThumbnailFileName(file.getOriginalFilename());
            product.setProductThumbnailContentType(file.getContentType());
            product.setName(productName);
            product.setPoint(0);
            this.tournamentMapper.insertTournamentProduct(product);
        }
        return CommonResult.SUCCESS;
    }

    // 토너먼트 선택
    public TournamentEntity get(int index) {
        return this.tournamentMapper.selectTournamentByIndex(index);
    }

    // 토너먼트 요소 선택
    public TournamentProductEntity getProduct(int index) {
        return this.tournamentMapper.selectTournamentProductByIndex(index);
    }
}
