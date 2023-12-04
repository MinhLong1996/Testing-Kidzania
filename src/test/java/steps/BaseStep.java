package steps;

import context.ScenarioContext;
import core.util.DateTimeUtil;
import core.util.RandomUtil;
import core.util.WebDriverUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class BaseStep extends WebDriverUtil {
    private static final String RANDOM_PREFIX = "random.";
    private static final String DATE_PREFIX = "date.";
    protected ScenarioContext context = ScenarioContext.instance();

    public String translateText(String key) {
        if (null == key) {
            return null;
        }

        if (key.toLowerCase().startsWith(RANDOM_PREFIX)) {
            String type = key.substring(RANDOM_PREFIX.length());
            return RandomUtil.random(type);
        } else if (key.toLowerCase().startsWith(DATE_PREFIX)) {
            String type = key.substring(DATE_PREFIX.length());
            return DateTimeUtil.get(type);
        } else {
            return context.translate(key);
        }
    }

    public List<String> translateText(List<String> data, int size) {
        data = data.stream().map(this::translateText).collect(Collectors.toList());
        List<String> finalColumns = data;
        List<String> list = new ArrayList<>(Collections.nCopies(size, ""));
        IntStream.range(0, data.size()).forEach(index -> list.set(index, finalColumns.get(index)));
        return list;
    }
}
