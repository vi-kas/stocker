package models;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Created by vika on 21/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockBuyResponse {

    private String error;
    private Stock stock;
    private String quantity;
    private Double totalPrice;
    private boolean success;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Stock getStock() {
        return stock;
    }

    public void setStock(Stock stock) {
        this.stock = stock;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
