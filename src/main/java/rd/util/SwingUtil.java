package rd.util;

import java.awt.*;

public class SwingUtil {

    public static void changeFontRecursive(Component rootComponent, Font font ) {
        rootComponent.setFont ( font );
        if ( rootComponent instanceof Container ) {
            for ( Component child : ( ( Container ) rootComponent ).getComponents () ) {
                changeFontRecursive( child, font );
            }
        }
    }

    public static int getFontHeight(Graphics g, Font font) {
        FontMetrics m= g.getFontMetrics(font); // g is your current Graphics object
        float totalSize= (float) (font.getSize() * (m.getAscent() + m.getDescent())) / m.getAscent();
        return Math.round(totalSize);
    }
}
