package dev.hcs.mytournament.mappers;

import dev.hcs.mytournament.entities.EmailAuthEntity;
import dev.hcs.mytournament.entities.UserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UserMapper {
    int insertUser(UserEntity user);

    int updateUser(UserEntity user);

    int insertEmailAuth(EmailAuthEntity emailAuth);

    int updateEmailAuth(EmailAuthEntity emailAuth);

    EmailAuthEntity selectEmailAuthByEmailCodeSalt(@Param("email") String email,
                                                   @Param("code") String code,
                                                   @Param("salt") String salt);

    UserEntity selectUserByEmail(@Param("email") String email);

    UserEntity selectUserByNickname(@Param("nickname") String nickname);
}
