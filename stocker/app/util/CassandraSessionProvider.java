package util;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Session;

import javax.inject.Inject;
import java.util.Optional;

/**
 * Created by vika on 17/08/16.
 */
public class CassandraSessionProvider {


    public static Optional<Session> sessionOptional = Optional.empty();
    public static Object sessionInitializationSyncObject = new Object();

    public static Session getSession(){
        return sessionOptional.orElseGet(() -> {
            synchronized (sessionInitializationSyncObject){
                return sessionOptional.orElseGet(() -> {
                    Cluster cluster = Cluster
                            .builder()
                            .addContactPoint("localhost")
                            .withPort(9042)
                            .build();
                    Session session = cluster.connect("stock_one");
                    return Optional.ofNullable(session).get();
                });
            }
        });
    }

}
