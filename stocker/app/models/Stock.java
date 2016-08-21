package models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by vika on 16/08/16.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Stock {

    @JsonProperty("t")
    public String ticker;

    @JsonProperty("e")
    public String exchangeName;

    @JsonProperty("l")
    public String lastPrice;

    @JsonProperty("lt")
    public String lastTimestamp;

    @JsonProperty("c")
    public String change;

    @JsonProperty("cp")
    public String changePercent;

    @JsonProperty("pcls_fix")
    public String previousClosePrice;

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getExchangeName() {
        return exchangeName;
    }

    public void setExchangeName(String exchangeName) {
        this.exchangeName = exchangeName;
    }

    public String getLastPrice() {
        return lastPrice;
    }

    public void setLastPrice(String lastPrice) {
        this.lastPrice = lastPrice;
    }

    public String getLastTimestamp() {
        return lastTimestamp;
    }

    public void setLastTimestamp(String lastTimestamp) {
        this.lastTimestamp = lastTimestamp;
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }

    public String getChangePercent() {
        return changePercent;
    }

    public void setChangePercent(String changePercent) {
        this.changePercent = changePercent;
    }

    public String getPreviousClosePrice() {
        return previousClosePrice;
    }

    public void setPreviousClosePrice(String previousClosePrice) {
        this.previousClosePrice = previousClosePrice;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "ticker='" + ticker + '\'' +
                ", exchangeName='" + exchangeName + '\'' +
                ", lastPrice='" + lastPrice + '\'' +
                ", lastTimestamp='" + lastTimestamp + '\'' +
                ", change='" + change + '\'' +
                ", changePercent='" + changePercent + '\'' +
                ", previousClosePrice='" + previousClosePrice + '\'' +
                '}';
    }
}
