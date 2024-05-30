package dev.hcs.mytournament.survices;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hcs.mytournament.entities.UserEntity;
import dev.hcs.mytournament.mappers.UserMapper;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class KakaoService {

    private final UserMapper userMapper;

    @Autowired
    public KakaoService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public String getAccessTokenFromKakao(String client_id, String code) throws IOException {
        //------kakao POST 요청------
        String reqURL = "https://kauth.kakao.com/oauth/token?grant_type=authorization_code&client_id="+client_id+"&code=" + code;
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("POST");

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
        });

        System.out.println("Response Body : " + result);

        String accessToken = (String) jsonMap.get("access_token");
        String refreshToken = (String) jsonMap.get("refresh_token");
        String scope = (String) jsonMap.get("scope");

        System.out.println("Access Token : " + accessToken);
        System.out.println("Refresh Token : " + refreshToken);
        System.out.println("Scope : " + scope);

        return accessToken;
    }

    public HashMap<String, Object> getUserInfo(String access_Token) throws IOException {
        // 클라이언트 요청 정보
        HashMap<String, Object> userInfo = new HashMap<String, Object>();


        //------kakao GET 요청------
        String reqURL = "https://kapi.kakao.com/v2/user/me";
        URL url = new URL(reqURL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", "Bearer " + access_Token);

        int responseCode = conn.getResponseCode();
        System.out.println("responseCode : " + responseCode);

        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String line = "";
        String result = "";

        while ((line = br.readLine()) != null) {
            result += line;
        }

        System.out.println("Response Body : " + result);

        // jackson objectmapper 객체 생성
        ObjectMapper objectMapper = new ObjectMapper();
        // JSON String -> Map
        Map<String, Object> jsonMap = objectMapper.readValue(result, new TypeReference<Map<String, Object>>() {
        });


        //사용자 정보 추출
        Map<String, Object> properties = (Map<String, Object>) jsonMap.get("properties");
        Map<String, Object> kakao_account = (Map<String, Object>) jsonMap.get("kakao_account");

        Long id = (Long) jsonMap.get("id");
        String nickname = properties.get("nickname").toString();
        String profileImage = properties.get("profile_image").toString();
        String email = kakao_account.get("email").toString();
        System.out.println("아이디" + id);
        System.out.println("닉네임" + nickname);
        System.out.println("프로필이미지" + profileImage);
        System.out.println("이메일" + email);

        //userInfo에 넣기
        userInfo.put("id", id);
        userInfo.put("nickname", nickname);
        userInfo.put("profileImage", profileImage);
        userInfo.put("email", email);
        return userInfo;
    }

    // 카카오 회원가입
    public void kakaoRegister(HashMap<String, Object> userInfo, UserEntity user) {
        user.setEmail("kakao_" + userInfo.get("email").toString());
        user.setPassword( new BCryptPasswordEncoder().encode( userInfo.get("id").toString() ) );
        user.setNickname( RandomStringUtils.randomAlphanumeric(8) );
        user.setCreatedAt(LocalDateTime.now());
        user.setAdmin(false);
        user.setDeleted(false);
        user.setSuspended(false);
        user.setKakao(true);
        this.userMapper.insertUser(user);
    }

    // 카카오 로그인
    public UserEntity kakaoLogin(HashMap<String, Object> userInfo, UserEntity user) {
        // db에 해당 아이디가 있는지 확인
        UserEntity dbUser = this.userMapper.selectUserByEmail("kakao_" + userInfo.get("email").toString());
        if (dbUser == null) {
            return null;    // 없으면 로그인 X
        }

        if (dbUser.isDeleted() || dbUser.isSuspended()) {
            return null;    // 정지된 계정이거나 삭제된 계정일 경우
        }

        // 유저 Entity에 db유저의 데이터를 저장
        user.setEmail(dbUser.getEmail());
        user.setPassword(dbUser.getPassword());
        user.setNickname(dbUser.getNickname());
        user.setCreatedAt(dbUser.getCreatedAt());
        user.setAdmin(dbUser.isAdmin());
        user.setDeleted(dbUser.isDeleted());
        user.setSuspended(dbUser.isSuspended());
        user.setKakao(dbUser.isKakao());
        return user;
    }

    // 카카오 로그아웃

}
