package com.vietqradminbe.application.services.grpc.response;

import com.vietqradminbe.web.dto.response.interfaces.TransactionReceiveAdminListDTO;

public class TransactionReceiveAdminListDTOImpl implements TransactionReceiveAdminListDTO {

    private final String id;
    private final Long amount;
    private final Long timePaid;
    private final String referenceNumber;
    private final String orderId;
    private final String terminalCode;
    private final String subCode;
    private final Long timeCreated;
    private final String bankAccount;
    private final String bankShortName;
    private final Integer status;
    private final String content;
    private final Integer transStatus;
    private final Integer type;
    private final Integer statusResponse;
    private final String transType;
    private final String note;

    public TransactionReceiveAdminListDTOImpl(com.example.grpc.TransactionReceiveAdminListDTO grpcDto) {
        this.id = grpcDto.getId();
        this.amount = grpcDto.getAmount();
        this.timePaid = grpcDto.getTimePaid();
        this.referenceNumber = grpcDto.getReferenceNumber();
        this.orderId = grpcDto.getOrderId();
        this.terminalCode = grpcDto.getTerminalCode();
        this.subCode = grpcDto.getSubCode();
        this.timeCreated = grpcDto.getTimeCreated();
        this.bankAccount = grpcDto.getBankAccount();
        this.bankShortName = grpcDto.getBankShortName();
        this.status = grpcDto.getStatus();
        this.content = grpcDto.getContent();
        this.transStatus = grpcDto.getTransStatus();
        this.type = grpcDto.getType();
        this.statusResponse = grpcDto.getStatusResponse();
        this.transType = grpcDto.getTransType();
        this.note = grpcDto.getNote();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public Long getAmount() {
        return amount;
    }

    @Override
    public Long getTimePaid() {
        return timePaid;
    }

    @Override
    public String getReferenceNumber() {
        return referenceNumber;
    }

    @Override
    public String getOrderId() {
        return orderId;
    }

    @Override
    public String getTerminalCode() {
        return terminalCode;
    }

    @Override
    public String getSubCode() {
        return subCode;
    }

    @Override
    public Long getTimeCreated() {
        return timeCreated;
    }

    @Override
    public String getBankAccount() {
        return bankAccount;
    }

    @Override
    public String getBankShortName() {
        return bankShortName;
    }

    @Override
    public Integer getStatus() {
        return status;
    }

    @Override
    public String getContent() {
        return content;
    }

    @Override
    public Integer getTransStatus() {
        return transStatus;
    }

    @Override
    public Integer getType() {
        return type;
    }

    @Override
    public Integer getStatusResponse() {
        return statusResponse;
    }

    @Override
    public String getTransType() {
        return transType;
    }

    @Override
    public String getNote() {
        return note;
    }
}

