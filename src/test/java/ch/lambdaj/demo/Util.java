package ch.lambdaj.demo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class Util {

    public static final DateFormat DEFAUALT_DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

    public static Date formatDate(final String date) {
        try {
            return DEFAUALT_DATE_FORMAT.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getCurrentYear() {
        return getYear(new Date());
    }

    public static int getYear(final Date date) {
        final Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }

    public static boolean listsAreEqual(final List<?> list1, final List<?> list2) {
        if (list1 == null && list2 == null) return true;
        if (list1 == null || list2 == null) return false;
        if (list1.size() != list2.size()) return false;
        for (int i = 0; i < list1.size(); i++) if (!list1.get(i).equals(list2.get(i))) return false;
        return true;
    }
}