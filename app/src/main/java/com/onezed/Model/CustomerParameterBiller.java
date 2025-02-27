package com.onezed.Model;

public class CustomerParameterBiller {
    String billerName,billerId,billerCategory,billerType,adhocBiller,paymentAmountExactness,billerCoverage,billerDescription;

    public String getBillerDescription() {
        return billerDescription;
    }

    public void setBillerDescription(String billerDescription) {
        this.billerDescription = billerDescription;
    }

    public String getBillerCoverage() {
        return billerCoverage;
    }

    public void setBillerCoverage(String billerCoverage) {
        this.billerCoverage = billerCoverage;
    }

    public String getPaymentAmountExactness() {
        return paymentAmountExactness;
    }

    public void setPaymentAmountExactness(String paymentAmountExactness) {
        this.paymentAmountExactness = paymentAmountExactness;
    }

    public String getBillerType() {
        return billerType;
    }

    public String getAdhocBiller() {
        return adhocBiller;
    }

    public void setAdhocBiller(String adhocBiller) {
        this.adhocBiller = adhocBiller;
    }

    public void setBillerType(String billerType) {
        this.billerType = billerType;
    }

    public String getBillerName() {
        return billerName;
    }

    public void setBillerName(String billerName) {
        this.billerName = billerName;
    }

    public String getBillerId() {
        return billerId;
    }

    public void setBillerId(String billerId) {
        this.billerId = billerId;
    }

    public String getBillerCategory() {
        return billerCategory;
    }

    public void setBillerCategory(String billerCategory) {
        this.billerCategory = billerCategory;
    }
}
