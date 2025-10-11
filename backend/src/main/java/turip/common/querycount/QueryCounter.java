package turip.common.querycount;

import lombok.Getter;

@Getter
public class QueryCounter {

    private final Long time;
    private Long count;

    public QueryCounter(Long count, Long time) {
        this.count = count;
        this.time = time;
    }

    public void increaseCount() {
        count++;
    }
}
