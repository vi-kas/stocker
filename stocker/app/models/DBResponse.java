package models;

/**
 * Created by vika on 18/08/16.
 */
public class DBResponse {
    private String error;
    private Stock[] stocks;
    private boolean success;

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

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
