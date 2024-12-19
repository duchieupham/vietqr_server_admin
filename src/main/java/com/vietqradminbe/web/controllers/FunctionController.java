package com.vietqradminbe.web.controllers;

import com.vietqradminbe.application.services.ActivityUserLogService;
import com.vietqradminbe.application.services.FunctionService;
import com.vietqradminbe.application.services.UserService;
import com.vietqradminbe.domain.exceptions.BadRequestException;
import com.vietqradminbe.domain.models.ActivityUserLog;
import com.vietqradminbe.domain.models.Function;
import com.vietqradminbe.domain.models.User;
import com.vietqradminbe.infrastructure.configuration.security.utils.JwtUtil;
import com.vietqradminbe.infrastructure.configuration.timehelper.TimeHelperUtil;
import com.vietqradminbe.web.dto.request.FunctionCreationRequest;
import com.vietqradminbe.web.dto.response.APIResponse;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FunctionController {

    static Logger logger = Logger.getLogger(FunctionController.class.getName());
    FunctionService functionService;
    JwtUtil jwtUtil;
    UserService userService;
    ActivityUserLogService activityUserLogService;

    @GetMapping("functions")
    public ResponseEntity<APIResponse<List<Function>>> getFunctions() {
        APIResponse<List<Function>> response = new APIResponse<>();
        try {

            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            List<Function> functions = functionService.getAllFunctions();

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                token = authorizationHeader.substring(7);  // Remove "Bearer " prefix

                //lay username tu token va lay user tu username
                String username = jwtUtil.extractUsernameFromToken(token.replace("Bearer ", ""));
                User user = userService.getUserByUsername(username);

                ActivityUserLog activityUserLog = new ActivityUserLog();
                activityUserLog.setUsername(username);
                activityUserLog.setId(UUID.randomUUID().toString());
                activityUserLog.setEmail(user.getEmail());
                activityUserLog.setFirstname(user.getFirstname());
                activityUserLog.setLastname(user.getLastname());
                activityUserLog.setPhoneNumber(user.getPhoneNumber());
                activityUserLog.setTimeLog(TimeHelperUtil.getCurrentTime());
                activityUserLog.setUser(user);
                activityUserLog.setActionJson(functions.toString());
                activityUserLog.setGroupFunctionId("7a461d48-d25b-47f5-977e-471ab534a6b0");
                activityUserLog.setFunctionId("1af7fea7-87c2-4cfd-91fa-77db3f1b0bf2");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have get all function at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }
            logger.info(FunctionController.class + ": INFO: getFunctions: " + functions.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(functions);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException e) {
            logger.error(FunctionController.class + ": ERROR: getFunctions: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(400);
            response.setMessage("E1006");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error(FunctionController.class + ": ERROR: getFunctions: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("functions")
    @Transactional
    public ResponseEntity<APIResponse<String>> createFunction(@RequestBody @Valid FunctionCreationRequest request) {
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

                ActivityUserLog activityUserLog = new ActivityUserLog();
                activityUserLog.setUsername(username);
                activityUserLog.setId(UUID.randomUUID().toString());
                activityUserLog.setEmail(user.getEmail());
                activityUserLog.setFirstname(user.getFirstname());
                activityUserLog.setLastname(user.getLastname());
                activityUserLog.setPhoneNumber(user.getPhoneNumber());
                activityUserLog.setTimeLog(TimeHelperUtil.getCurrentTime());
                activityUserLog.setUser(user);
                activityUserLog.setActionJson("");
                activityUserLog.setGroupFunctionId("7a461d48-d25b-47f5-977e-471ab534a6b0");
                activityUserLog.setFunctionId("2f955125-fac6-4986-83a6-fb08410b29bd");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just create function at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }
            functionService.createFunctionRequest(request);
            logger.info(FunctionController.class + ": INFO: createFunction: " + request.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Create successfully!");
            response.setResult("SUCCESS");
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (BadRequestException e) {
            logger.error(FunctionController.class + ": ERROR: createFunction: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(400);
            response.setMessage("E1006");
            response.setResult("FAILED");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (Exception e) {
            logger.error(FunctionController.class + ": ERROR: createFunction: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            response.setResult("FAILED");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
