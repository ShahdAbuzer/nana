package org.project.projectstep1zanix.chapter;

import java.math.BigDecimal;

public class ChapterPaymentSummaryDto {

    private BigDecimal total;
    private String currency;

    public ChapterPaymentSummaryDto() {
    }

    public ChapterPaymentSummaryDto(BigDecimal total, String currency) {
        this.total = total;
        this.currency = currency;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public String getCurrency() {
        return currency;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}