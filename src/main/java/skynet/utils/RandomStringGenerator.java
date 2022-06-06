package skynet.utils;

import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class RandomStringGenerator {

    public static String randomString(int len) {
        final String allowedChars = "abcdefghijklmnopqrstuvwxyz";
        SecureRandom rnd = new SecureRandom();

        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {
            sb.append(allowedChars.charAt(rnd.nextInt(allowedChars.length())));
        }
        return sb.toString();
    }

    public static String randomDigits(int len) {
        StringBuilder sb = new StringBuilder(len);
        SecureRandom rnd = new SecureRandom();
        for (int i = 0; i < len; i++) {
            sb.append(rnd.nextInt(10));
        }
        return sb.toString();
    }

    public static String randomDate(String fromDate, String toDate, String fromFormat, String toFormat) throws ParseException {
        SimpleDateFormat sp = new SimpleDateFormat(fromFormat);
        long fromTime = sp.parse(fromDate).getTime();
        long toTime = sp.parse(toDate).getTime();

        long diff = toTime - fromTime + 1;

        if (diff <= 0) {
            return fromDate;
        }

        long randomTime = fromTime + (long) (Math.random() * diff);
        return new SimpleDateFormat(toFormat).format(new Date(randomTime));
    }
}