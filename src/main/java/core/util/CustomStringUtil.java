package core.util;

import java.nio.file.FileSystems;
import java.security.SecureRandom;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

public class CustomStringUtil {
    private final String fileSeparator = FileSystems.getDefault().getSeparator();
    private final String userDir = System.getProperty("user.dir");

    public String getFullPathFromFragments(String[] frags) {
        StringBuilder r = new StringBuilder();
        for (String s : frags) {
            r.append(s).append(fileSeparator);
        }
        return userDir + fileSeparator + r.substring(0, r.length() - 1);
    }

    public String extractPageName(String elementName) {
        if (elementName.indexOf("PAGE") > 0) {
            int i = elementName.indexOf("PAGE") + 4;
            return elementName.substring(0, i);
        } else {
            throw new IllegalArgumentException("Missing PAGE in element name: " + elementName);
        }

    }

    public String getPureString(String input) {
        return input.replaceAll("^\"+|\"+$", "");
    }

    public String convertToParsableDateString(String date) {
        String year = date.split("/")[0];
        String month = date.split("/")[1];
        String day = date.split("/")[2];

        if (Integer.parseInt(month) < 10)
            month = "0" + month;
        if (Integer.parseInt(day) < 10)
            day = "0" + day;

        return year + "/" + month + "/" + day;
    }

    public String getCurrentTimeAsString() {
        SimpleDateFormat sdf3 = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        return sdf3.format(timestamp);
    }

    public static String randomNumeric(int length) {
        String numberChars = "0123456789";
        SecureRandom secureRandom = new SecureRandom();
        return secureRandom.ints(length, 0, numberChars.length()).mapToObj(numberChars::charAt)
                .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
    }

}
