package com.vietqradminbe.web.controllers;

import com.vietqradminbe.application.services.ActionLogService;
import com.vietqradminbe.application.services.FeatureService;
import com.vietqradminbe.application.services.UserService;
import com.vietqradminbe.domain.exceptions.BadRequestException;
import com.vietqradminbe.domain.models.ActionLog;
import com.vietqradminbe.domain.models.User;
import com.vietqradminbe.infrastructure.configuration.security.utils.JwtUtil;
import com.vietqradminbe.infrastructure.configuration.timehelper.TimeHelperUtil;
import com.vietqradminbe.web.dto.request.FeatureCreationRequest;
import com.vietqradminbe.web.dto.response.APIResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FeatureController {

    static Logger logger = Logger.getLogger(FeatureController.class.getName());
    FeatureService featureService;
    JwtUtil jwtUtil;
    UserService userService;
    ActionLogService actionLogService;

    @PostMapping("features")
    @Transactional
    public APIResponse<String> createFeature(@RequestBody @Valid FeatureCreationRequest request) {
        APIResponse<String> response = new APIResponse<>();
        try {

            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Remove "Bearer " prefix

                //lay username tu token va lay user tu username
                String username = jwtUtil.extractUsernameFromToken(token.replace("Bearer ", ""));
                User user = userService.getUserByUsername(username);

                ActionLog actionLog = new ActionLog();
                actionLog.setUsername(username);
                actionLog.setId(UUID.randomUUID().toString());
                actionLog.setEmail(user.getEmail());
                actionLog.setFirstname(user.getFirstname());
                actionLog.setLastname(user.getLastname());
                actionLog.setPhoneNumber(user.getPhoneNumber());
                actionLog.setCreateAt(TimeHelperUtil.getCurrentTime());
                actionLog.setUpdateAt("");
                actionLog.setUser(user);
                actionLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just create role at " + TimeHelperUtil.getCurrentTime());
                actionLogService.createActionLog(actionLog);
            }
            featureService.createFeatureRequest(request);
            logger.info(FeatureController.class + ": INFO: createFeature: " + request.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Create successfully!");
            response.setResult("SUCCESS");
        } catch (BadRequestException e) {
            logger.error(FeatureController.class + ": ERROR: createFeature: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(400);
            response.setMessage("E1007");
            response.setResult("FAILED");
        } catch (Exception e) {
            logger.error(FeatureController.class + ": ERROR: createFeature: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(400);
            response.setMessage("E1005");
            response.setResult("FAILED");
        }
        return response;
    }

}