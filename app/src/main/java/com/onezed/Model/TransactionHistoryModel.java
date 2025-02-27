package com.onezed.Model;

public class TransactionHistoryModel {
    private String status;
    private String amount;
    private String transactionId;
    private String date;

//    public TransactionHistoryModel(String status, String amount, String transactionId, String date) {
//        this.status = status;
//        this.amount = amount;
//        this.transactionId = transactionId;
//        this.date = date;
//    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
