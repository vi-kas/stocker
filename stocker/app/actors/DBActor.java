package actors;

import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;
import com.datastax.driver.core.Session;
import models.*;
import play.libs.F;
import util.CassandraSessionProvider;

import java.util.List;

/**
 * Created by vika on 18/08/16.
 */
public class DBActor extends UntypedActor {

    private String TABLE_NAME = "stock_info";
    private String INSERT_QUERY = "INSERT INTO " + TABLE_NAME + "(ticker, exchangeName, lastPrice, lastTimestamp, change, changePercent, previousClosePrice) VALUES (";
    private String SELECT_QUERY = "SELECT * FROM " + TABLE_NAME + " WHERE ticker='";

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof DBStoreRequest){
            System.out.println("DBActor :: Received Message of Type: DBStoreRequest");
            DBStoreRequest mesg = (DBStoreRequest) message;
            Patterns.pipe(handleDBStoreRequest(mesg).wrapped(),context().dispatcher()).to(sender());
        }else if(message instanceof DBFetchRequest){
            System.out.println("DBActor :: Received Message of Type: DBFetchRequest");
            DBFetchRequest mesg = (DBFetchRequest) message;
            Patterns.pipe(handleDBFetchRequest(mesg).wrapped(),context().dispatcher()).to(sender());
        }else {
            DBResponse response = new DBResponse();
            response.setError("DBActor :: Unexpected Message.");
            response.setSuccess(false);
            Patterns.pipe(F.Promise.pure(response).wrapped(),context().dispatcher()).to(sender());
        }
    }

    public F.Promise<DBResponse> handleDBStoreRequest(DBStoreRequest request){
        String query = createBatchQueryToExecute(request.getStocks());
        System.out.println("Query to Execute: " + query);

        try{
            DBResponse response = new DBResponse();
            Session cassandraSession = CassandraSessionProvider.getSession();
            cassandraSession.execute(query);
            System.out.println("Data Stored");
            response.setSuccess(Boolean.TRUE);
            return F.Promise.pure(response);
        }catch (Exception exception){
            System.out.println("Could not Execute Query: "+exception.fillInStackTrace());
        }

        return F.Promise.pure(new DBResponse());
    }

    public F.Promise<DBResponse> handleDBFetchRequest(DBFetchRequest request){
        String query = createSelectQuery(request);
        System.out.println("Query to Execute: " + query);
        try{
            DBFetchResponse response = new DBFetchResponse();
            Session cassandraSession = CassandraSessionProvider.getSession();
            ResultSet resultSet = cassandraSession.execute(query);
            List<Row> resultRows = resultSet.all();
            System.out.println("Stage 1: "+ resultRows);
            Stock[] stocks = new Stock[resultRows.size()];
            System.out.println("Stage 2: "+ resultRows);
            for (int i = 0; i < resultRows.size(); i++){
                System.out.println("Row from ResultSet: "+ resultRows.get(i));
                stocks[i] = new Stock();
                stocks[i].setTicker(resultRows.get(i).getString("ticker"));
                stocks[i].setExchangeName(resultRows.get(i).getString("exchangeName"));
                stocks[i].setLastPrice(resultRows.get(i).getString("lastPrice"));
                stocks[i].setChange(resultRows.get(i).getString("change"));
                stocks[i].setChangePercent(resultRows.get(i).getString("changePercent"));
                System.out.println("Stock: " + stocks[i]);
            }
            System.out.println("Data Retrieved!");
            response.setStocks(stocks);
            response.setSuccess(Boolean.TRUE);
            return F.Promise.pure(response);
        }catch (Exception exception){
            System.out.println("Could not Execute Query: " + exception.fillInStackTrace());
            exception.printStackTrace();
        }

        return F.Promise.pure(new DBResponse());
    }

    private String createSelectQuery(DBFetchRequest request) {
        return SELECT_QUERY + request.getTicker()+"';";
    }

    private String createBatchQueryToExecute(Stock[] stockObjectArray) {
        StringBuilder batchQuery = new StringBuilder("BEGIN BATCH ");
        for(Stock stock: stockObjectArray){
            batchQuery.append(INSERT_QUERY
                    + "'" + stock.getTicker() + "'" + ", "
                    + "'" + stock.getExchangeName() + "'" + ", "
                    + "'" + stock.getLastPrice() + "'" + ", "
                    + "'" + stock.getLastTimestamp() + "'" + ", "
                    + "'" + stock.getChange() + "'" + ", "
                    + "'" + stock.getChangePercent() + "'" + ", "
                    + "'" + stock.getPreviousClosePrice() + "'" + ") ");
        }
        batchQuery.append(" APPLY BATCH;");
        return String.valueOf(batchQuery);
    }
}
