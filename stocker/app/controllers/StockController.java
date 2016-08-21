package controllers;

import actors.BuyStockActor;
import actors.StockingActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import models.StockBuyRequest;
import models.StockBuyResponse;
import models.StockResponse;
import play.data.DynamicForm;
import play.data.Form;
import play.libs.Akka;
import play.libs.F;
import play.libs.ws.WSClient;
import play.mvc.Result;

import javax.inject.Inject;
import views.html.*;

/**
 * Created by vika on 16/08/16.
 */
public class StockController extends BaseController {

    @Inject WSClient ws;
    private ActorRef stockingActor;
    private ActorRef buyStockActor;
    private ActorSystem actorSystem;

    public F.Promise<Result> getStocks() {
        System.out.println("GET STOCKS API");
        actorSystem = Akka.system();
        stockingActor = actorSystem.actorOf(StockingActor.props(ws, actorSystem.dispatcher()));
        String stocks =
                "NASDAQ:GOOG,NASDAQ:YHOO,NASDAQ:LMLP,NYSE:FLT,OTCMKTS:RFNS,OTCMKTS:BTGDF,OTCMKTS:WSHE,OTCMKTS:TNCGF," +
                "NYSE:CVX,NYSE:PSX,NYSE:PBR,NYSE:XOM,NYSE:TOT,NYSE:E,NYSE:MPC,NYSE:OGZPY,NYSE:RDS.A,NYSE:BP," +
                "NYSE:VHI,NYSE:NL,NYSE:HUN,NYSE:CC,NYSEMKT:CIX,NASDAQ:ECOL,NASDAQ:PESI,NASDAQ:TORM,NASDAQ:AIMC," +
                "NASDAQ:URBN,NYSE:JWN,NYSE:TLYS,NYSE:BKE,NYSE:AEO,NYSE:GPS,NYSE:ANF,NYSE:EXPR,NYSE:GES,NYSE:TJX";

        scala.concurrent.Future<Object> stockResponseFuture = Patterns.ask(stockingActor, stocks, 9000);
        return F.Promise.wrap(stockResponseFuture).map(response -> {
            StockResponse resp = new StockResponse();
            if(response instanceof StockResponse){
                resp = (StockResponse) response;
            }
           //return ok(Json.toJson(response));
            return ok(stock.render(resp.getStocks()));
        });
    }

    public F.Promise<Result> buyStock(){
        System.out.println("BUY STOCKS API");
        buyStockActor = actorSystem.actorOf(BuyStockActor.props());
        DynamicForm dynamicForm = Form.form().bindFromRequest();
        if (dynamicForm == null){
            System.out.println("It's Null!!");
            return F.Promise.pure(badRequest());
        }else {
            System.out.println("Not Null Hurray!!: "+ dynamicForm.data());
            StockBuyRequest stockBuyRequest = new StockBuyRequest();
            stockBuyRequest.setTicker(dynamicForm.data().get("ticker"));
            stockBuyRequest.setLastPrice(dynamicForm.data().get("lastPrice"));
            if(dynamicForm.data().get("quantity").length() > 0){
                stockBuyRequest.setQuantity(dynamicForm.data().get("quantity"));
            }else {
                stockBuyRequest.setQuantity("10");
            }

            scala.concurrent.Future<Object> stockBuyResponseFuture = Patterns.ask(buyStockActor, stockBuyRequest, 9000);
            return F.Promise.wrap(stockBuyResponseFuture).map(response -> {
                StockBuyResponse resp = new StockBuyResponse();
                if(response instanceof StockBuyResponse){
                    resp = (StockBuyResponse) response;
                    System.out.println("Response for buyStock: "+ resp);
                }
                return ok(buystock.render(resp));
            });
        }

    }
}
