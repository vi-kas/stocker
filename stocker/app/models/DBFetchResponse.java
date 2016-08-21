package models;

/**
 * Created by vika on 22/08/16.
 */
public class DBFetchResponse extends DBResponse {

    private Stock[] stocks;

    @Override
    public Stock[] getStocks() {
        return stocks;
    }

    @Override
    public void setStocks(Stock[] stocks) {
        this.stocks = stocks;
    }
}
