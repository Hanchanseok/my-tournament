package dev.hcs.mytournament.survices;

import dev.hcs.mytournament.entities.EmailAuthEntity;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.UserMapper;
import dev.hcs.mytournament.misc.MailSender;
import dev.hcs.mytournament.regexes.EmailAuthRegex;
import dev.hcs.mytournament.regexes.UserRegex;
import dev.hcs.mytournament.results.CommonResult;
import dev.hcs.mytournament.results.Result;
import dev.hcs.mytournament.results.user.*;
import jakarta.mail.MessagingException;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.token.Sha512DigestUtils;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Autowired
    public UserService(UserMapper userMapper, JavaMailSender mailSender, SpringTemplateEngine templateEngine) {
        this.userMapper = userMapper;
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    // 이메일인증 엔티티 set
    private static void prepareEmailAuth(EmailAuthEntity emailAuth) throws NoSuchAlgorithmException {
        emailAuth.setCode(RandomStringUtils.randomNumeric(6));
        emailAuth.setSalt(Sha512DigestUtils.shaHex(String.format("%s%s%f%f",
                emailAuth.getEmail(),
                emailAuth.getCode(),
                SecureRandom.getInstanceStrong().nextDouble(),
                SecureRandom.getInstanceStrong().nextDouble())));
        emailAuth.setCreatedAt(LocalDateTime.now());
        emailAuth.setExpiresAt(LocalDateTime.now().plusMinutes(3));
        emailAuth.setExpired(false);
        emailAuth.setVerified(false);
        emailAuth.setUsed(false);
    }


    // 이메일 코드 인증
    public Result sendRegisterEmail(EmailAuthEntity emailAuth) throws NoSuchAlgorithmException, MessagingException {
        if (emailAuth == null || !EmailAuthRegex.email.tests(emailAuth.getEmail())) {
            return CommonResult.FAILURE;
        }
        // 이메일 중복일 경우
        if (this.userMapper.selectUserByEmail(emailAuth.getEmail()) != null) {
            return SendRegisterEmailResult.FAILURE_DUPLICATE_EMAIL;
        }
        // 이메일 인증 엔티티에 값들을 집어 넣는다.
        prepareEmailAuth(emailAuth);
        // 그리고 DB에 저장
        if (this.userMapper.insertEmailAuth(emailAuth) != 1) {
            return CommonResult.FAILURE;
        }

        // 이메일 보내는 로직 작성
        Context context = new Context();
        context.setVariable("code", emailAuth.getCode());   //6자 코드
        new MailSender(this.mailSender)
                .setFrom("hcs2804@gmail.com")
                .setSubject("[이상형 월드컵] 회원가입 인증번호")
                .setTo(emailAuth.getEmail())
                .setText(this.templateEngine.process("user/registerEmail", context), true)
                .send();

        return CommonResult.SUCCESS;
    }

    // 이메일 코드 확인
    public Result verifyEmailAuth(EmailAuthEntity emailAuth) {
        if (emailAuth == null || !EmailAuthRegex.email.tests(emailAuth.getEmail())
                || !EmailAuthRegex.code.tests(emailAuth.getCode())
                || !EmailAuthRegex.salt.tests(emailAuth.getSalt())) {
            return CommonResult.FAILURE;
        }
        EmailAuthEntity dbEmailAuth = this.userMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(), emailAuth.getCode(), emailAuth.getSalt());

        // 없거나, 이미 인증완료 되었거나, 이미 사용되었거나
        if (dbEmailAuth == null || dbEmailAuth.isVerified() || dbEmailAuth.isUsed()) {
            return CommonResult.FAILURE;
        }
        // 만료되었을 경우
        if (dbEmailAuth.isExpired() || dbEmailAuth.getExpiresAt().isBefore(LocalDateTime.now())) {
            return VerifyEmailAuthResult.FAILURE_EXPIRED;
        }

        dbEmailAuth.setVerified(true);
        return this.userMapper.updateEmailAuth(dbEmailAuth) > 0
                ? CommonResult.SUCCESS
                : CommonResult.FAILURE;
    }

    // 최종 회원가입
    @Transactional
    public Result register(EmailAuthEntity emailAuth, UserEntity user, String passwordCheck) {
        if (emailAuth == null || !EmailAuthRegex.email.tests(emailAuth.getEmail())
                || !EmailAuthRegex.code.tests(emailAuth.getCode())
                || !EmailAuthRegex.salt.tests(emailAuth.getSalt())) {
            return CommonResult.FAILURE;
        }
        if (user == null || !UserRegex.email.tests(user.getEmail())) {
            return CommonResult.FAILURE;
        }
        if (!UserRegex.password.tests(user.getPassword())) {
            return RegisterResult.FAILURE_PASSWORD_REGEX;
        }
        if (!user.getPassword().equals(passwordCheck)) {
            return RegisterResult.FAILURE_PASSWORD_CHECK;
        }
        if (!UserRegex.nickname.tests(user.getNickname())) {
            return RegisterResult.FAILURE_NICKNAME_REGEX;
        }
        EmailAuthEntity dbEmailAuth = this.userMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(), emailAuth.getCode(), emailAuth.getSalt());

        if (dbEmailAuth == null || !dbEmailAuth.isVerified()) {
            return RegisterResult.FAILURE_EMAIL_AUTH_VERIFIED;
        }

        // 닉네임 중복 여부 확인
        if (this.userMapper.selectUserByNickname(user.getNickname()) != null) {
            return RegisterResult.FAILURE_DUPLICATE_NICKNAME;
        }

        // 비밀번호 암호화
        user.setPassword( new BCryptPasswordEncoder().encode(user.getPassword()) );
        user.setCreatedAt(LocalDateTime.now());
        user.setAdmin(false);
        user.setDeleted(false);
        user.setSuspended(false);
        user.setKakao(false);
        this.userMapper.insertUser(user);   // 회원가입

        dbEmailAuth.setUsed(true);
        this.userMapper.updateEmailAuth(dbEmailAuth);   // 이메일 인증 사용됨

        return CommonResult.SUCCESS;
    }

    // 비번 변경 이메일 코드 인증
    public Result sendResetPasswordEmail(EmailAuthEntity emailAuth) throws NoSuchAlgorithmException, MessagingException {
        if (emailAuth == null || !EmailAuthRegex.email.tests(emailAuth.getEmail())) {
            return CommonResult.FAILURE;
        }
        // 등록되지 않은 이메일일 경우
        if (this.userMapper.selectUserByEmail(emailAuth.getEmail()) == null) {
            return SendResetPasswordEmailResult.FAILURE_NONE_REGISTER_EMAIL;
        }
        // 이메일 인증 엔티티에 값들을 집어 넣는다.
        prepareEmailAuth(emailAuth);
        // 그리고 DB에 저장
        if (this.userMapper.insertEmailAuth(emailAuth) != 1) {
            return CommonResult.FAILURE;
        }

        // 이메일 보내는 로직 작성
        Context context = new Context();
        context.setVariable("code", emailAuth.getCode());   //6자 코드
        new MailSender(this.mailSender)
                .setFrom("hcs2804@gmail.com")
                .setSubject("[이상형 월드컵] 비밀번호 변경 인증번호")
                .setTo(emailAuth.getEmail())
                .setText(this.templateEngine.process("user/resetPasswordEmail", context), true)
                .send();

        return CommonResult.SUCCESS;
    }

    // 최종 비밀번호 변경
    public Result resetPassword(EmailAuthEntity emailAuth, UserEntity user, String passwordCheck) {
        if (emailAuth == null || !EmailAuthRegex.email.tests(emailAuth.getEmail())
                || !EmailAuthRegex.code.tests(emailAuth.getCode())
                || !EmailAuthRegex.salt.tests(emailAuth.getSalt())) {
            return CommonResult.FAILURE;
        }
        if (user == null || !UserRegex.email.tests(user.getEmail())) {
            return CommonResult.FAILURE;
        }
        if (!UserRegex.password.tests(user.getPassword())) {
            return ResetPasswordResult.FAILURE_PASSWORD_REGEX;
        }
        if (!user.getPassword().equals(passwordCheck)) {
            return ResetPasswordResult.FAILURE_PASSWORD_CHECK;
        }

        EmailAuthEntity dbEmailAuth = this.userMapper.selectEmailAuthByEmailCodeSalt(
                emailAuth.getEmail(), emailAuth.getCode(), emailAuth.getSalt());

        if (dbEmailAuth == null || !dbEmailAuth.isVerified()) {
            return ResetPasswordResult.FAILURE_EMAIL_AUTH_VERIFIED;
        }

        // 해당 유저 데이터 가져오기
        UserEntity dbUser = this.userMapper.selectUserByEmail(user.getEmail());

        // 비밀번호 암호화
        dbUser.setPassword( new BCryptPasswordEncoder().encode(user.getPassword()) );
        this.userMapper.updateUser(dbUser);   // 비밀번호 변경

        dbEmailAuth.setUsed(true);
        this.userMapper.updateEmailAuth(dbEmailAuth);   // 이메일 인증 사용됨

        return CommonResult.SUCCESS;
    }

    // 로그인
    public Result login(UserEntity loginUser) {
        if (loginUser == null || !UserRegex.email.tests(loginUser.getEmail())
                || !UserRegex.password.tests(loginUser.getPassword())) {
            return CommonResult.FAILURE;
        }

        UserEntity dbUser = this.userMapper.selectUserByEmail(loginUser.getEmail());

        if (dbUser == null) {
            return LoginResult.FAILURE_EMAIL;
        }
        if (!BCrypt.checkpw(loginUser.getPassword(), dbUser.getPassword())) {
            return LoginResult.FAILURE_PASSWORD;
        }
        if (dbUser.isDeleted()) {
            return LoginResult.FAILURE_IS_DELETED;
        }
        if (dbUser.isSuspended()) {
            return LoginResult.FAILURE_IS_SUSPENDED;
        }

        loginUser.setEmail(dbUser.getEmail());
        loginUser.setPassword(dbUser.getPassword());
        loginUser.setNickname(dbUser.getNickname());
        loginUser.setCreatedAt(dbUser.getCreatedAt());
        loginUser.setAdmin(dbUser.isAdmin());
        loginUser.setDeleted(dbUser.isDeleted());
        loginUser.setSuspended(dbUser.isSuspended());
        loginUser.setKakao(dbUser.isKakao());
        return CommonResult.SUCCESS;
    }

    // 유저 가져오기
    public UserEntity getUserByEmail(String email) {
        return this.userMapper.selectUserByEmail(email);
    }
}
