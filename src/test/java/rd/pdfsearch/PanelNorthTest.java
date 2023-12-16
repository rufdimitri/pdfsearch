package rd.pdfsearch;

import junit.framework.TestCase;

import java.awt.*;
import java.util.Map;

public class PanelNorthTest extends TestCase {
    public PanelNorthTest(String name) {
        super(name);
    }

    public void testConstraints() {
        GridBagConstraints constraints = PanelNorth.constraints(
                Map.of("gridx", "1"
                        ,"gridy", "2"
                        ,"fill", String.valueOf(GridBagConstraints.BOTH)
                        ,"weightx", "0.5"
                        ,"weighty", "0.7"
                ));

        assertEquals(constraints.gridx, 1);
        assertEquals(constraints.gridy, 2);
        assertEquals(constraints.fill, GridBagConstraints.BOTH);
        assertTrue(Math.abs(0.5 - constraints.weightx) < 0.000001);
        assertTrue(Math.abs(0.7 - constraints.weighty) < 0.000001);
    }
}
