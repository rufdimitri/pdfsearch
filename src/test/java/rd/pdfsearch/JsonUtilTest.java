package rd.pdfsearch;

import junit.framework.TestCase;
import rd.util.JsonUtil;

import java.util.Arrays;
import java.util.List;

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

        JsonUtil<TestPOJO> util = JsonUtil.of(TestPOJO.class);
        TestPOJO testObject1 = new TestPOJO();
        testObject1.setAge(33);
        testObject1.setName("ABC");
        testObject1.setVorname("def");
        testObject1.setAddress(Arrays.asList("street 1", "house 2"));
        util.marshallToFile(filename, testObject1);

        JsonUtil<TestPOJO> util2 = JsonUtil.of(TestPOJO.class);

        TestPOJO testObject1Recovered = util2.unmarshallFromFile(filename);
        assertEquals(33, testObject1Recovered.getAge());
        assertEquals("ABC", testObject1Recovered.getName());
        assertEquals("def", testObject1Recovered.getVorname());
        assertEquals(Arrays.asList("street 1", "house 2"), testObject1Recovered.getAddress());
    }
}
