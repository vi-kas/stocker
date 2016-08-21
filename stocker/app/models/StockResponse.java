package models;

import com.fasterxml.jackson.annotation.JsonInclude;


/**
 * Created by vika on 17/08/16.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StockResponse {

    private String error;
    private Stock[] stocks;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Stock[] getStocks() {
        return stocks;
    }

    public void setStocks(Stock[] stocks) {
        this.stocks = stocks;
    }

}
