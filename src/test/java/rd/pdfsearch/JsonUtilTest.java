package rd.pdfsearch;

import com.google.gson.reflect.TypeToken;
import junit.framework.TestCase;
import rd.pdfsearch.model.CachedPdfFile;
import rd.pdfsearch.model.FileIdentity;
import rd.util.JsonUtil;

import java.util.*;

public class JsonUtilTest extends TestCase {

    static class TestPOJO {
        private int age;
        private String name;
        private String vorname;
        List<String> address;


        public int getAge() {
            return age;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getVorname() {
            return vorname;
        }

        public void setVorname(String vorname) {
            this.vorname = vorname;
        }

        public List<String> getAddress() {
            return address;
        }

        public void setAddress(List<String> address) {
            this.address = address;
        }
    }

    public JsonUtilTest(String name) {
        super(name);
    }

    public void testMarshallUnmarshall() {
        String filename = "target/file.json";

        TestPOJO testObject1 = new TestPOJO();
        testObject1.setAge(33);
        testObject1.setName("ABC");
        testObject1.setVorname("def");
        testObject1.setAddress(Arrays.asList("street 1", "house 2"));
        JsonUtil.marshallToFile(filename, testObject1);

        TestPOJO testObject1Recovered = JsonUtil.unmarshallFromFile(filename, new TypeToken<>() {
        });
        assertEquals(33, testObject1Recovered.getAge());
        assertEquals("ABC", testObject1Recovered.getName());
        assertEquals("def", testObject1Recovered.getVorname());
        assertEquals(Arrays.asList("street 1", "house 2"), testObject1Recovered.getAddress());

        //simpleMap
        //create object and marshall
        Map<String, Integer> initialSimpleMapObject = simpleMap();
        String simpleMapStringIntegerJsonString = JsonUtil.marshallToJson(initialSimpleMapObject);
        //unmarshall
        Map<String, Integer> simpleMapStringIntegerObject = JsonUtil.unmarshallFromJson(simpleMapStringIntegerJsonString, new TypeToken<>() {
        });
        assertTrue(initialSimpleMapObject.equals(simpleMapStringIntegerObject));

        //create object and marshall
        Map<Integer, List<CachedPdfFile>> initialComplexMapObject = cachedFilesPerFileIdentityHashCode();
        String complexMapJsonString = JsonUtil.marshallToJson(initialComplexMapObject);
        //unmarshall
        Map<Integer, List<CachedPdfFile>> complexMapObject = JsonUtil.unmarshallFromJson(complexMapJsonString, new TypeToken<>() {
        });
        assertTrue(initialComplexMapObject.equals(complexMapObject));
    }

    static Map<String,Integer> simpleMap() {
        Map<String,Integer> map = new HashMap<>();
        map.put("Abc", 3);
        map.put("Abcd", 4);
        map.put("Abcde", 5);
        return map;
    }

    Map<Integer, List<CachedPdfFile>> cachedFilesPerFileIdentityHashCode() {
        Map<Integer, List<CachedPdfFile>> result = new HashMap<>();
        FileIdentity fileIdentity = new FileIdentity("file.txt", "folder1", 256L, 512L);
        CachedPdfFile cachedPdfFile = new CachedPdfFile(List.of("page1", "page2"), fileIdentity, 559L);
        result.put(777, List.of(cachedPdfFile, cachedPdfFile));
        return result;
    }
}
