package com.vietqradminbe.web.controllers;

import com.vietqradminbe.application.services.ActivityUserLogService;
import com.vietqradminbe.application.services.UserService;
import com.vietqradminbe.application.services.grpc.TransactionReceiveServiceClient;
import com.vietqradminbe.domain.exceptions.BadRequestException;
import com.vietqradminbe.domain.exceptions.ErrorCode;
import com.vietqradminbe.domain.models.ActivityUserLog;
import com.vietqradminbe.domain.models.User;
import com.vietqradminbe.infrastructure.adapters.database.mysql.transaction.services.TransactionService;
import com.vietqradminbe.infrastructure.configuration.security.utils.JwtUtil;
import com.vietqradminbe.infrastructure.configuration.timehelper.TimeHelperUtil;
import com.vietqradminbe.web.dto.request.RequestFilterTransactionRequest;
import com.vietqradminbe.web.dto.response.APIResponse;
import com.vietqradminbe.web.dto.response.TransactionReceivePaginationResponseDTO;
import com.vietqradminbe.web.dto.response.interfaces.TransReceiveAdminDetailDTO;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TransactionController {
    static Logger logger = Logger.getLogger(TransactionController.class);

    TransactionService transactionService;
    TransactionReceiveServiceClient transactionReceiveServiceClient;
    JwtUtil jwtUtil;
    UserService userService;
    ActivityUserLogService activityUserLogService;


    @GetMapping("/transactions")
    public ResponseEntity<APIResponse<TransactionReceivePaginationResponseDTO>> getTransactions(
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {
        APIResponse<TransactionReceivePaginationResponseDTO> response = new APIResponse<>();
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            TransactionReceivePaginationResponseDTO trans = transactionService.getTransactionsWithPaginationAllFilter(page, size);


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
                activityUserLog.setActionJson(trans.toString());
                activityUserLog.setGroupFunctionId("6fcdc4d1-f3f7-4862-bdc7-bfef31194fb6");
                activityUserLog.setFunctionId("2e4c4a4c-3fd6-4231-b780-a0865490a8e0");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just get all trans at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }

            logger.info(TransactionController.class + ": INFO: trans: " + trans.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(trans);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            logger.error(TransactionController.class + ": ERROR: trans: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @GetMapping("/transactions/v2")
    public ResponseEntity<APIResponse<TransactionReceivePaginationResponseDTO>> getTransactionsV2(
            @RequestParam(value = "from", required = false) Long from,
            @RequestParam(value = "to", required = false) Long to,
            @RequestParam(value = "bankAccount", required = false) String bankAccount,
            @RequestParam(value = "referenceNumber", required = false) String referenceNumber,
            @RequestParam(value = "orderId", required = false) String orderId,
            @RequestParam(value = "terminalCode", required = false) String terminalCode,
            @RequestParam(value = "subCode", required = false) String subCode,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "transStatus", required = false) Integer transStatus,
            @RequestParam(value = "page") int page,
            @RequestParam(value = "size") int size
    ) {
        APIResponse<TransactionReceivePaginationResponseDTO> response = new APIResponse<>();
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            TransactionReceivePaginationResponseDTO transResponse = transactionService.getTransactionsWithPagination(from, to, bankAccount,
                    referenceNumber, orderId, terminalCode, subCode, transStatus, page, size);

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
                activityUserLog.setActionJson(transResponse.toString());
                activityUserLog.setGroupFunctionId("6fcdc4d1-f3f7-4862-bdc7-bfef31194fb6");
                activityUserLog.setFunctionId("2e4c4a4c-3fd6-4231-b780-a0865490a8e0");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just get all users at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }


            logger.info(TransactionController.class + ": INFO: trans: " + transResponse.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(transResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            logger.error(TransactionController.class + ": ERROR: trans: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/transactions/v3")
    public ResponseEntity<APIResponse<TransactionReceivePaginationResponseDTO>> getTransactionsV3(
            @RequestBody RequestFilterTransactionRequest filterTransactionRequest
    ) {
        APIResponse<TransactionReceivePaginationResponseDTO> response = new APIResponse<>();
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            TransactionReceivePaginationResponseDTO transResponse = transactionService.getTransactionsWithPaginationByOption(filterTransactionRequest);


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
                activityUserLog.setActionJson(transResponse.toString());
                activityUserLog.setGroupFunctionId("6fcdc4d1-f3f7-4862-bdc7-bfef31194fb6");
                activityUserLog.setFunctionId("2e4c4a4c-3fd6-4231-b780-a0865490a8e0");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just get all users at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }

            logger.info(TransactionController.class + ": INFO: trans: " + transResponse.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(transResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            logger.error(TransactionController.class + ": ERROR: trans: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/transactions/v3/demo-grpc")
    public ResponseEntity<APIResponse<TransactionReceivePaginationResponseDTO>> getTransactionsV3GRPC(
            @RequestBody RequestFilterTransactionRequest filterTransactionRequest
    ) {
        APIResponse<TransactionReceivePaginationResponseDTO> response = new APIResponse<>();
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            TransactionReceivePaginationResponseDTO transResponse = transactionReceiveServiceClient.getTransactionsWithPaginationByOption(filterTransactionRequest);


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
                activityUserLog.setActionJson(transResponse.toString());
                activityUserLog.setGroupFunctionId("6fcdc4d1-f3f7-4862-bdc7-bfef31194fb6");
                activityUserLog.setFunctionId("2e4c4a4c-3fd6-4231-b780-a0865490a8e0");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just get all users at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }

            logger.info(TransactionController.class + ": INFO: trans: " + transResponse.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(transResponse);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            logger.error(TransactionController.class + ": ERROR: trans: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @GetMapping("/transactions/{id}")
    public ResponseEntity<APIResponse<TransReceiveAdminDetailDTO>> getTransactionDetailAdmin(
            @PathVariable String id) {
        APIResponse<TransReceiveAdminDetailDTO> response = new APIResponse<>();
        try {
            HttpServletRequest currentRequest = ((ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes()).getRequest();

            // Extract Bearer token from the Authorization header
            String authorizationHeader = currentRequest.getHeader("Authorization");
            String token = null;
            TransReceiveAdminDetailDTO tran = transactionService.getDetailTransReceiveAdmin(id);


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
                activityUserLog.setActionJson(tran.toString());
                activityUserLog.setGroupFunctionId("6fcdc4d1-f3f7-4862-bdc7-bfef31194fb6");
                activityUserLog.setFunctionId("2e5ec391-b0ed-412e-a928-09dcf578ccda");
                activityUserLog.setDescription("User :" + user.getUsername() + " " + user.getEmail() + " " + user.getFirstname() + " " + user.getLastname() + " " + user.getPhoneNumber() + " have just get detail tran at " + TimeHelperUtil.getCurrentTime());
                activityUserLogService.createActivityUserLog(activityUserLog);
            }

            logger.info(TransactionController.class + ": INFO: tran: " + tran.toString()
                    + " at: " + System.currentTimeMillis());
            response.setCode(200);
            response.setMessage("Get successfully!");
            response.setResult(tran);
            return ResponseEntity.status(HttpStatus.OK).body(response);
        } catch (ExpiredJwtException e) {
            throw new BadRequestException(ErrorCode.TOKEN_EXPIRED);
        } catch (Exception e) {
            logger.error(TransactionController.class + ": ERROR: trans: " + e.getMessage()
                    + " at: " + System.currentTimeMillis());
            response.setCode(500);
            response.setMessage("E1005");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
