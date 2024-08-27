package com.daewon.xeno_backend.controller;

import com.daewon.xeno_backend.domain.auth.UserRole;
import com.daewon.xeno_backend.exception.UnauthorizedException;
import com.daewon.xeno_backend.exception.UserNotFoundException;
import com.daewon.xeno_backend.service.AuthService;
import com.daewon.xeno_backend.service.ManagerService;
import com.daewon.xeno_backend.utils.JWTUtil;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
@Log4j2
public class ManagerController {

    private final AuthService authService;
    private final ManagerService managerService;
    private final JWTUtil jwtUtil;

    @DeleteMapping("/userDelete/{targetUserId}")
    public ResponseEntity<?> deleteUser(Authentication authentication, @RequestHeader("Authorization") String token,
                                        @PathVariable Long targetUserId) {

        // token의 claim값에서 email값을 추출 후 문제 없으면 정상적으로 {targetUserId}값이 삭제 됨.
        try {
            String currentToken = token.replace("Bearer ", "");
            Map<String, Object> claims = jwtUtil.validateToken(currentToken);
            String managerEmail = claims.get("email").toString();
            log.info("managerEmail: " + managerEmail);

            String deletedUserEmail = managerService.deleteUserByManager(managerEmail, targetUserId);
            log.info("delete중인 manager의 email은? : " + deletedUserEmail);
            return ResponseEntity.ok("해당 user의 탈퇴가 완료되었습니다.");
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생", e);
            return ResponseEntity.status(401).body("token이 유효하지 않거나 만료됨");
        } catch (UserNotFoundException e) {
            log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnauthorizedException e) {
            log.warn("권한 없는 작업 시도: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("사용자 삭제 중 오류 발생", e);
            return ResponseEntity.status(500).body("사용자를 삭제하는 도중 오류가 발생 : " + e.getMessage());
        }
    }

    @PutMapping("/roles/{targetUserId}")
    public ResponseEntity<String> updateUserRoles(Authentication authentication,
                                                  @RequestHeader("Authorization") String token,
                                                  @RequestBody Set<UserRole> newRole,
                                                  @PathVariable Long targetUserId) {
        try {
            String currentToken = token.replace("Bearer ", "");
            Map<String, Object> claims = jwtUtil.validateToken(currentToken);
            String managerEmail = claims.get("email").toString();

            managerService.updateUserRoleByManager(managerEmail, targetUserId, newRole);
            return ResponseEntity.ok("해당 user의 role값 변경이 완료되었습니다.");
        } catch (JwtException e) {
            log.error("JWT 토큰 처리 중 오류 발생", e);
            return ResponseEntity.status(401).body("Invalid or expired token");
        } catch (UserNotFoundException e) {
            log.warn("사용자를 찾을 수 없음: {}", e.getMessage());
            return ResponseEntity.status(404).body(e.getMessage());
        } catch (UnauthorizedException e) {
            log.warn("권한 없는 작업 시도: {}", e.getMessage());
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            log.error("사용자 역할 업데이트 중 오류 발생", e);
            return ResponseEntity.status(500)
                    .body("An error occurred while updating user roles: " + e.getMessage());
        }
    }

}
