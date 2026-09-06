package turip.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateRangeCalculator {

    private static final int ONE_WEEK = 1;
    private static final int DAYS_UNTIL_SUNDAY = 6;

    private DateRangeCalculator() {
    }

    public record DateRange(LocalDate startDate, LocalDate endDate) {
    }

    public static DateRange lastWeekRange() {
        LocalDate lastWeekMonday = LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(ONE_WEEK);
        LocalDate lastWeekSunday = lastWeekMonday.plusDays(DAYS_UNTIL_SUNDAY);
        return new DateRange(lastWeekMonday, lastWeekSunday);
    }
}
