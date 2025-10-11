package turip.common.querycount;

import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
public class QueryCountInspector implements StatementInspector {

    private final ThreadLocal<QueryCounter> queryCount = new ThreadLocal<>();

    public void startCounter() {
        queryCount.set(new QueryCounter(0L, System.currentTimeMillis()));
    }

    public QueryCounter getQueryCount() {
        return queryCount.get();
    }

    public void clearCounter() {
        queryCount.remove();
    }

    @Override
    public String inspect(String sql) {
        QueryCounter queryCounter = queryCount.get();
        if (queryCounter != null) {
            queryCounter.increaseCount();
        }
        return sql;
    }
}
