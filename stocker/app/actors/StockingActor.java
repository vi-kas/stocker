package actors;

import akka.actor.ActorRef;
import akka.actor.Props;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.DBStoreRequest;
import models.Stock;
import models.StockResponse;
import play.libs.F;
import play.libs.ws.WSClient;
import play.libs.ws.WSResponse;
import scala.concurrent.ExecutionContextExecutor;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletionStage;

/**
 * Created by vika on 17/08/16.
 */
public class StockingActor extends UntypedActor {

    private WSClient ws;
    private ExecutionContextExecutor ec;
    private ActorRef dbActor;
    private String STOCK_URL = "http://finance.google.com/finance/info?client=ig&q=";

    @Inject
    public StockingActor(WSClient ws, ExecutionContextExecutor executor){
        this.ws = ws;
        this.ec = executor;
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        System.out.println("Stage 2");
        if(message instanceof String){
            System.out.println("StockingActor :: Received Message of Type: String");
            Patterns.pipe(handleRequest(String.valueOf(message)).wrapped(),ec).to(sender());
        }else{
            StockResponse stockResponse = new StockResponse();
            stockResponse.setError("StockingActor :: Unexpected Message Type.");
            Patterns.pipe(F.Promise.pure(stockResponse).wrapped(),ec).to(sender());
        }
    }

    public F.Promise<StockResponse> handleRequest(String stocks){
        System.out.println("Stage 4: Stocks:=>  "+ stocks);
        StockResponse stockResponse = new StockResponse();
        CompletionStage<WSResponse> responseCompletionStage = ws.url(STOCK_URL + stocks).get();
        return F.Promise.wrap(responseCompletionStage).map(response -> {
            System.out.println("Stage 5");
            InputStream is = response.getBodyAsStream();
            String responseToParse = getMeJsonParsableString(is);
            //JsonNode jsonResponse =  Json.parse(responseToParse);
            stockResponse.setStocks(processResponse(responseToParse));
            return stockResponse;
        });
    }

    private static String getMeJsonParsableString(InputStream is) {
        System.out.println("Stage 6");
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        String parsableString = s.hasNext() ? s.next() : "";
        return parsableString.substring(4);//To Remove those "//" from  response.
    }

    private Stock[] processResponse(String jsonResponse) {
        System.out.println("Stage 7");
        ObjectMapper mapper = new ObjectMapper();
        try{
            Stock[] stockObjectArray = mapper.readValue(jsonResponse, Stock[].class);
            for (Stock stock: stockObjectArray){
                System.out.println(stock);
            }
            storeInDB(stockObjectArray);
            return stockObjectArray;
        }catch(IOException e){
            e.printStackTrace();
            return new Stock[0];
        }
    }

    private void storeInDB(Stock[] stockObjectArray) {
        dbActor = context().actorOf(Props.create(DBActor.class));
        DBStoreRequest request = new DBStoreRequest();
        request.setStocks(stockObjectArray);
        scala.concurrent.Future<Object> storeResponse = Patterns.ask(dbActor, request, 9000);
        F.Promise.wrap(storeResponse).map(resp -> {
            return resp;
        });
    }



    public static Props props(WSClient ws, ExecutionContextExecutor ec){
        return Props.create(StockingActor.class,ws,ec);
    }
}
