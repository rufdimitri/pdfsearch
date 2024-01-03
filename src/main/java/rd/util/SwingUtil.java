package rd.util;

import java.awt.*;

public class SwingUtil {

    public static void changeFont (Component component, Font font ) {
        component.setFont ( font );
        if ( component instanceof Container ) {
            for ( Component child : ( ( Container ) component ).getComponents () ) {
                changeFont ( child, font );
            }
        }
    }

    public static int getFontHeight(Graphics g, Font font) {
        FontMetrics m= g.getFontMetrics(font); // g is your current Graphics object
        float totalSize= (float) (font.getSize() * (m.getAscent() + m.getDescent())) / m.getAscent();
        return Math.round(totalSize);
    }
}
