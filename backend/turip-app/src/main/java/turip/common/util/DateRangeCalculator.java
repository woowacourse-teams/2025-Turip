package turip.common.util;

import java.time.DayOfWeek;
import java.time.LocalDate;

public class DateRangeCalculator {

    private static final int ONE_WEEK = 1;
    private static final int DAYS_UNTIL_SUNDAY = 6;

    private DateRangeCalculator() {
    }

    public static LocalDate lastWeekMonday() {
        return LocalDate.now().with(DayOfWeek.MONDAY).minusWeeks(ONE_WEEK);
    }

    public static LocalDate lastWeekSunday() {
        return lastWeekMonday().plusDays(DAYS_UNTIL_SUNDAY);
    }
}
