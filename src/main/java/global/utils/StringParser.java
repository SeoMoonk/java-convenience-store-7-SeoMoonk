package global.utils;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class StringParser {

    public static int parseInt(String input) {
        int number;
        try {
            number = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("파일의 데이터를 숫자로 변환할 수 없습니다 : " + input);
        }
        return number;
    }

    public static LocalDate parseDate(String input) {
        LocalDate date;
        try {
            date = LocalDate.parse(input);
        } catch (DateTimeParseException e) {
            throw new DateTimeParseException("파일의 데이터를 날짜로 변환할 수 없습니다 : " + input, input, e.getErrorIndex());
        }
        return date;
    }

}
