import java.awt.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;
import javax.swing.event.ChangeListener;



public class MultiGradientViewPort extends JScrollPane {

    private static final long serialVersionUID = 1L;
    private final int h = 50;
    private BufferedImage imgRed = null;
    private BufferedImage imgBlue = null;
    private BufferedImage shadowRed = new BufferedImage(1, h,
            BufferedImage.TYPE_INT_ARGB);
    private BufferedImage shadowBlue = new BufferedImage(1, h,
            BufferedImage.TYPE_INT_ARGB);
    private JViewport viewPort;
    private boolean recVisible = true;

    public MultiGradientViewPort(JComponent com) {
        super(com);
        viewPort = this.getViewport();
        viewPort.setScrollMode(JViewport.BLIT_SCROLL_MODE);
        viewPort.setScrollMode(JViewport.BACKINGSTORE_SCROLL_MODE);
        viewPort.setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
        createShadow(new Color(250, 150, 150),shadowRed);
        createShadow(Color.BLUE,shadowBlue);

        final JTable table = (JTable) com;
        viewport.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                Rectangle RECT = table.getCellRect(0, 0, true);

                Rectangle viewRect = viewport.getViewRect();
                if (viewRect.intersects(RECT)) {
                    System.out.println("Visible RECT -> " + RECT);
                    recVisible = true;

                } else {
                    recVisible = false;
                }
            }
        });

    }

    private void createShadow(Color color, BufferedImage shadow) {
        Graphics2D g2 = shadow.createGraphics();
        g2.setPaint(color);
        g2.fillRect(0, 0, 1, h);
        g2.setComposite(AlphaComposite.DstIn);
        g2.setPaint(new GradientPaint(0, 0, new Color(0, 0, 0, 0f), 0, h,
                new Color(0.5f, 0.8f, 0.8f, 0.5f)));
        g2.fillRect(0, 0, 1, h);
        g2.dispose();
    }

    @Override
    public void paint(Graphics g) {
        if(recVisible){
        paintShadow(g,imgRed,shadowRed);
        } else {
        paintShadow(g,imgBlue,shadowBlue);
        }
    }

    private void paintShadow(Graphics g,BufferedImage img, BufferedImage shadow) {
        if (img == null || img.getWidth() != getWidth()
                || img.getHeight() != getHeight()) {
            img = new BufferedImage(getWidth(), getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
        }
        Graphics2D g2 = img.createGraphics();
        super.paint(g2);
        Rectangle bounds = getViewport().getVisibleRect();
        g2.scale(bounds.getWidth(), -1);
        int y = (getColumnHeader() == null) ? 0 : getColumnHeader().getHeight();
        g2.drawImage(shadow, bounds.x, -bounds.y - y - h, null);
        g2.scale(1, -1);
        g2.drawImage(shadow, bounds.x, bounds.y + bounds.height - h + y, null);
        g2.dispose();
        g.drawImage(img, 0, 0, null);
    }
}
