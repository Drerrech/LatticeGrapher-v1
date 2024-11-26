import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Drawer extends JPanel {
    private static final int CANVAS_WIDTH = 800;
    private static final int CANVAS_HEIGHT = 800;
    private static final int POINT_SIZE = 4;
    private static final float LINE_THICKNESS = 1.0f;
    private final BufferedImage canvas;
    private final PointLattice lattice;

    public Drawer() {
        setPreferredSize(new Dimension(CANVAS_WIDTH, CANVAS_HEIGHT));
        canvas = new BufferedImage(CANVAS_WIDTH, CANVAS_HEIGHT, BufferedImage.TYPE_INT_RGB);
        
        double[] region = {-2, 2, 2, -2};  // The region in logical coordinates (xMin, yMax, xMax, yMin)
        int[] squareDensity = {16, 16}; // FOR I(neg y) AND J(pos x)
        EquationFunction leftSide = (x, y) -> Math.pow(x + y + 0.7, 2) + Math.pow(x - y, 2) - 1;
        EquationFunction rightSide = (x, y) -> 0;
        
        lattice = new PointLattice(region, squareDensity, leftSide, rightSide);
        setup();
    }

    private void setup() {
        Graphics2D g2d = canvas.createGraphics();
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        // Draw lattice points
        for (int i = 0; i < lattice.lattice.length; i++) {
            for (int j = 0; j < lattice.lattice[i].length; j++) {
                int x = (int) map(lattice.region[0] + j * (lattice.region[2] - lattice.region[0]) / (lattice.squareDensity[1]), 
                                  lattice.region[0], lattice.region[2], 0, CANVAS_WIDTH);
                int y = (int) map(lattice.region[1] + i * (lattice.region[3] - lattice.region[1]) / (lattice.squareDensity[0]), 
                                  lattice.region[1], lattice.region[3], 0, CANVAS_HEIGHT);
                
                if (lattice.lattice[i][j] > 0) {
                    g2d.setColor(Color.GREEN);
                } else {
                    g2d.setColor(Color.RED);
                }
                g2d.fillOval(x - POINT_SIZE/2, y - POINT_SIZE/2, POINT_SIZE, POINT_SIZE);
            }
        }

        // Draw lines
        g2d.setColor(Color.WHITE);
        g2d.setStroke(new BasicStroke(LINE_THICKNESS));
        for (LatticeLine l : lattice.lines) {
            int x1 = (int) map(l.x1, lattice.region[0], lattice.region[2], 0, CANVAS_WIDTH);
            int y1 = (int) map(l.y1, lattice.region[1], lattice.region[3], 0, CANVAS_HEIGHT);
            int x2 = (int) map(l.x2, lattice.region[0], lattice.region[2], 0, CANVAS_WIDTH);
            int y2 = (int) map(l.y2, lattice.region[1], lattice.region[3], 0, CANVAS_HEIGHT);
            g2d.drawLine(x1, y1, x2, y2);
        }

        g2d.dispose();
    }

    @Override
    protected void paintComponent(java.awt.Graphics g) {
        super.paintComponent(g);
        g.drawImage(canvas, 0, 0, null);
    }

    private float map(double value, double start1, double stop1, double start2, double stop2) {
        return (float)((value - start1) / (stop1 - start1) * (stop2 - start2) + start2);
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Lattice Drawing");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new Drawer());
        frame.pack();
        frame.setVisible(true);
    }
}
