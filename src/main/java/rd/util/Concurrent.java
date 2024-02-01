package rd.util;

import rd.pdfsearch.model.CachedPdfFile;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

public class Concurrent {
    /** used to create same type of map for cache map
     * @param baseMap
     * @return
     */
    static public Map<Integer, List<CachedPdfFile>> concurrentMap(Map<Integer, List<CachedPdfFile>> baseMap) {
        return new ConcurrentSkipListMap<>(baseMap);
    }
}
