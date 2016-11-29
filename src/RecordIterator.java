import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Created by Lorenzo Toso on 29.11.2016.
 */
public class RecordIterator implements Iterator<String> {
    private Record record;
    private Map<String, Integer> headerMap;
    private int i = 0;

    public RecordIterator(Record record, Map<String, Integer> headerMap) {
        this.record = record;
        this.headerMap = headerMap;
    }

    @Override
    public boolean hasNext() {
        return i < headerMap.size();
    }

    @Override
    public String next() {
        String column = getKeyByValue(headerMap, i);
        i++;
        return record.get(column);
    }

    private static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
