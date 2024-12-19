package com.vietqradminbe.application.services.grpc;

import com.example.grpc.TransactionReceiveServiceServerGrpc;
import com.vietqradminbe.application.services.grpc.response.TransactionReceiveAdminListDTOImpl;
import com.vietqradminbe.web.dto.request.RequestFilterTransactionRequest;
import com.vietqradminbe.web.dto.response.TransactionReceivePaginationResponseDTO;
import com.vietqradminbe.web.dto.response.interfaces.TransactionReceiveAdminListDTO;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionReceiveServiceClient {

    private static final Logger logger = LoggerFactory.getLogger(TransactionReceiveServiceClient.class);

    @GrpcClient("transaction-receive-service")
    private TransactionReceiveServiceServerGrpc.TransactionReceiveServiceServerBlockingStub transactionReceiveServiceStub;

//    public List<TransactionReceiveAdminListDTO> mapGrpcToWebDto(List<com.example.grpc.TransactionReceiveAdminListDTO> grpcList) {
//        return grpcList.stream()
//                .map(TransactionReceiveAdminListDTOImpl::new)
//                .collect(Collectors.toList());
//    }

    public TransactionReceivePaginationResponseDTO getTransactionsWithPaginationByOption(RequestFilterTransactionRequest request) {

        // Convert custom TypeA to gRPC TypeA
        com.example.grpc.TypeA grpcTypeA = null;
        if (request.getTypeA() != null) {
            grpcTypeA = com.example.grpc.TypeA.newBuilder()
                    .setType(request.getTypeA().getType())
                    .setFrom(request.getTypeA().getFrom())
                    .setTo(request.getTypeA().getTo())
                    .build();
        }

        // Convert TypeB
        com.example.grpc.TypeB grpcTypeB = null;
        if (request.getTypeB() != null) {
            grpcTypeB = com.example.grpc.TypeB.newBuilder()
                    .setType(request.getTypeB().getType())
                    .setMerchantName(request.getTypeB().getMerchantName())
                    .build();
        }

        // Convert TypeC
        com.example.grpc.TypeC grpcTypeC = null;
        if (request.getTypeC() != null) {
            grpcTypeC = com.example.grpc.TypeC.newBuilder()
                    .setType(request.getTypeC().getType())
                    .setBankAccount(request.getTypeC().getBankAccount())
                    .setReferenceNumber(request.getTypeC().getReferenceNumber())
                    .setOrderId(request.getTypeC().getOrderId())
                    .setTerminalCode(request.getTypeC().getTerminalCode())
                    .setSubCode(request.getTypeC().getSubCode())
                    .setContent(request.getTypeC().getContent())
                    .build();
        }

        // Convert TypeD
        com.example.grpc.TypeD grpcTypeD = null;
        if (request.getTypeD() != null) {
            grpcTypeD = com.example.grpc.TypeD.newBuilder()
                    .setType(request.getTypeD().getType())
                    .setStatus(request.getTypeD().getStatus())
                    .build();
        }

        com.example.grpc.RequestFilterTransactionRequest filterRequest =
                com.example.grpc.RequestFilterTransactionRequest.newBuilder()
                        .setPage(request.getPage())
                        .setSize(request.getSize())
                        .setTypeA(grpcTypeA)
                        .setTypeB(grpcTypeB)
                        .setTypeC(grpcTypeC)
                        .setTypeD(grpcTypeD)
                        .build();

        com.example.grpc.TransactionReceivePaginationResponseDTO responses =
                transactionReceiveServiceStub.getTransactionsWithPaginationByOption(filterRequest);

        if (responses != null) {
            System.out.println("Received response: " + responses);
        } else {
            System.out.println("No data received from the server.");
        }

        TransactionReceivePaginationResponseDTO transactionReceivePaginationResponseDTO = new TransactionReceivePaginationResponseDTO();
        transactionReceivePaginationResponseDTO.setItems(responses.getItemsList().stream()
                .map(grpcDto -> new TransactionReceiveAdminListDTOImpl(grpcDto))
                .collect(Collectors.toList()));
        transactionReceivePaginationResponseDTO.setLimit(responses.getLimit());
        transactionReceivePaginationResponseDTO.setTotal(responses.getTotal());
        transactionReceivePaginationResponseDTO.setPage(responses.getPage());
        transactionReceivePaginationResponseDTO.setHasNext(responses.getHasNext());
        return transactionReceivePaginationResponseDTO;
    }
}
