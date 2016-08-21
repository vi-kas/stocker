package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import models.*;
import play.libs.F;

/**
 * Created by vika on 21/08/16.
 */
public class BuyStockActor extends UntypedActor {

    private ActorRef dbActor;

    @Override
    public void onReceive(Object message) throws Throwable {
        if(message instanceof StockBuyRequest){
            System.out.println("BuyStockActor :: Received Message of Type: StockBuyRequest");
            Patterns.pipe(handleRequest((StockBuyRequest) message).wrapped(),context().dispatcher()).to(sender());
        }else{
            StockResponse stockResponse = new StockResponse();
            stockResponse.setError("BuyStockActor :: Unexpected Message Type.");
            Patterns.pipe(F.Promise.pure(stockResponse).wrapped(),context().dispatcher()).to(sender());
        }
    }

    public F.Promise<StockBuyResponse> handleRequest(StockBuyRequest buyRequest){
        System.out.println("Received a Buy Request for: "+ buyRequest.getQuantity() + " "+ buyRequest.getTicker() + " at Price: " + buyRequest.getLastPrice());
        try{
            Double currentPrice = Double.parseDouble(buyRequest.getLastPrice());
            Double quantity = Double.parseDouble(buyRequest.getQuantity());
            Double totalPrice = currentPrice * quantity;
            return processForStockBuyResponse(buyRequest, totalPrice);
        }catch (Exception ex){
            StockBuyResponse stockBuyResponse = new StockBuyResponse();
            stockBuyResponse.setError("Could not complete Action!");
            return F.Promise.pure(stockBuyResponse);
        }
    }

    private F.Promise<StockBuyResponse> processForStockBuyResponse(StockBuyRequest buyRequest, Double totalPrice) {
        StockBuyResponse stockBuyResponse = new StockBuyResponse();
        stockBuyResponse.setQuantity(buyRequest.getQuantity());
        stockBuyResponse.setTotalPrice(totalPrice);
        stockBuyResponse.setSuccess(Boolean.TRUE);
        return getMeStock(buyRequest.getTicker()).map(stock -> {
            stockBuyResponse.setStock(stock);
            return stockBuyResponse;
        });
    }

    private F.Promise<Stock> getMeStock(String ticker){
        dbActor = context().actorOf(Props.create(DBActor.class));
        DBFetchRequest dbFetchRequest = new DBFetchRequest();
        dbFetchRequest.setTicker(ticker);
        scala.concurrent.Future<Object> fetchResponse = Patterns.ask(dbActor, dbFetchRequest, 9000);
        return F.Promise.wrap(fetchResponse).map(response -> {
            DBResponse resp = new DBResponse();
            if (response instanceof DBResponse){
                System.out.println("1"+ response);
                resp = (DBFetchResponse)response;
                System.out.println("2: "+ resp.isSuccess());
                return resp.getStocks()[0];
            }else {
                resp.setSuccess(Boolean.FALSE);
                return new Stock();
            }
        });
    }

    private Stock getMeMockStock(){
        Stock stockMock = new Stock();
        stockMock.setTicker("GOOG");
        stockMock.setExchangeName("NASDAQ");
        stockMock.setLastPrice("779.52");
        stockMock.setLastTimestamp("Aug 16, 12:09PM EDT");
        stockMock.setChange("-2.92");
        stockMock.setChangePercent("-0.37");
        stockMock.setPreviousClosePrice("782.44");
        return stockMock;
    }

    public static Props props(){
        return Props.create(BuyStockActor.class);
    }
}
