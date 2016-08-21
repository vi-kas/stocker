package models;

/**
 * Created by vika on 21/08/16.
 */
public class DBFetchRequest extends DBRequest{

    private String ticker;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }
}
