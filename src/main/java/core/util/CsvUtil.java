package core.util;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import core.helper.LogHelper;
import org.junit.Assert;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class CsvUtil {
    private static final LogHelper logger = LogHelper.getInstance();
    private final String filePath;
    private List<List<String>> csv = null;

    public CsvUtil(String filePath) {
        this.filePath = filePath;
    }

    public List<List<String>> get() {
        return null != csv ? csv : read();
    }

    public List<Map<String, String>> getAsMap() {
        List<Map<String, String>> ret = new ArrayList<>();
        List<String> headers = get().get(0);
        for (int i = 1; i < get().size(); i++) {
            Map<String, String> row = new HashMap<>();
            for (int col = 0; col < headers.size(); col++) {
                row.put(headers.get(col), get().get(i).get(col));
            }
            ret.add(row);
        }
        return ret;
    }

    private List<List<String>> read() {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            Reader in = new BufferedReader(isr);
            CSVReader reader = new CSVReader(in);
            List<String[]> ret = reader.readAll();
            return ret.stream().map(Arrays::asList).collect(Collectors.toList());
        } catch (IOException | CsvException e) {
            throw new RuntimeException(e);
        }
    }

    public void compare(List<List<String>> dst) {
        List<List<String>> csv = get();
        if (!csv.equals(dst)) {
            if (csv.size() != dst.size()) {
                String msg = String.format("Size of source is %s but destination is %s", csv.size(), dst.size());
                logger.fail(msg);
            }
            for (int i = 0; i < csv.size(); i++) {
                String msg = String.format("CSV Comparison / Line %s", i + 1);
                Assert.assertEquals(msg, csv.get(i), dst.get(i));
            }
        }
    }

    public void compare(CsvUtil dstCsv) {
        compare(dstCsv.get());
    }

    public void write(List<List<String>> csv) {
        File file = new File(filePath);
        try {
            FileWriter fileWriter = new FileWriter(file);
            CSVWriter writer = new CSVWriter(fileWriter);
            List<String[]> data = new ArrayList<>();
            csv.forEach(e -> data.add(e.toArray(new String[0])));
            writer.writeAll(data);
            writer.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
    }
}
