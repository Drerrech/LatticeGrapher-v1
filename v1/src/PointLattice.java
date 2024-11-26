import java.util.ArrayList;

@FunctionalInterface
interface EquationFunction {
    double apply(double x, double y);
}

class LatticeLine {

    public double x1, y1, x2, y2;

    public LatticeLine(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }
}

class PointLattice {
    public double[] region;
    public int[] squareDensity;
    public double[][] lattice;
    ArrayList<LatticeLine> lines = new ArrayList<>();
    private final EquationFunction leftSide;
    private final EquationFunction rightSide;

    public PointLattice(double[] region, int[] squareDensity, EquationFunction leftSide, EquationFunction rightSide) {
        this.region = region; // x top left, y top left, x bottom right, y bottom right
        this.squareDensity = squareDensity; // rows(y) and columns(x)
        this.leftSide = leftSide;
        this.rightSide = rightSide;

        lattice = new double[squareDensity[0] + 1][squareDensity[1] + 1]; // latice of points

        // calculate pointDeltas in lattice
        double yStep = (region[3] - region[1]) / squareDensity[0];
        double xStep = (region[2] - region[0]) / squareDensity[1];
        for (int i = 0; i < lattice.length; i++) { // i goes down
            for (int j = 0; j < lattice[i].length; j++) { // j goes right
                lattice[i][j] = pointDelta(region[0] + j * xStep, region[1] + i * yStep);
            }
        }

        // generate lines
        for (int i = 0; i < squareDensity[0]; i++) {
            for (int j = 0; j < squareDensity[1]; j++) {
                // square pivots start at bottom left and go anti-clockwise
                double[] squareDeltas = {lattice[i][j], lattice[i][j+1], lattice[i+1][j+1], lattice[i+1][j]};
                int caseIndex = 0;
                for (int _i = 0; _i < 4; _i++) caseIndex = (caseIndex << 1) | (squareDeltas[_i] <= 0 ? 1 : 0); // less or equals? or just less? what if it passes through?
                
                double x1, y1, d1, x2, y2, d2; // p1 is negative delta, p2 is positive delta
                double[][] points;
                switch (caseIndex) { // for reference on each case: https://commons.wikimedia.org/wiki/File:Marching_squares_algorithm_schematic.svg#/media/File:Marching_squares_algorithm_schematic.svg
                    case 0 -> {
                    }
                    case 1 -> {
                        points = new double[2][2]; // two points, xy coordinated
                        
                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 2 -> {
                        points = new double[2][2];
                        
                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 3 -> {
                        points = new double[2][2];
                        
                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 4 -> {
                        points = new double[2][2];
                        
                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 5 -> {
                        points = new double[4][2];
                        
                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[2] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[3] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                        lines.add(new LatticeLine(points[2][0], points[2][1], points[3][0], points[3][1]));
                    }
                    case 6 -> {
                        points = new double[2][2];
                        
                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 7 -> {
                        points = new double[2][2];
                        
                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 8 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 9 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 10 -> {
                        points = new double[4][2];
                        
                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[2] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[3] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                        lines.add(new LatticeLine(points[2][0], points[2][1], points[3][0], points[3][1]));
                    }
                    case 11 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + i * yStep; d2 = lattice[i][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 12 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 13 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j+1];
                        x2 = region[0] + (j+1) * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j+1];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 14 -> {
                        points = new double[2][2];

                        x1 = region[0] + j * xStep; y1 = region[1] + i * yStep; d1 = lattice[i][j];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[0] = getPoint(x1, y1, d1, x2, y2, d2);

                        x1 = region[0] + (j+1) * xStep; y1 = region[1] + (i+1) * yStep; d1 = lattice[i+1][j+1];
                        x2 = region[0] + j * xStep; y2 = region[1] + (i+1) * yStep; d2 = lattice[i+1][j];
                        points[1] = getPoint(x1, y1, d1, x2, y2, d2);

                        lines.add(new LatticeLine(points[0][0], points[0][1], points[1][0], points[1][1]));
                    }
                    case 15 -> {
                    }
                }
                // for reference on each case: https://commons.wikimedia.org/wiki/File:Marching_squares_algorithm_schematic.svg#/media/File:Marching_squares_algorithm_schematic.svg
            }
        }
    }

    final double pointDelta(double x, double y) {
        return leftSide.apply(x, y) - rightSide.apply(x, y);
    }

    final double[] getPoint(double x1, double y1, double delta1, double x2, double y2, double delta2) {
        double coef = Math.abs(delta1) / (Math.abs(delta1) + Math.abs(delta2));
        if (Double.isNaN(coef)) coef = 0; // hacky fix but idk what to do if java functions simply return NaN
        double cx = x1 + (x2 - x1) * coef; // x point of current c point
        double cy = y1 + (y2 - y1) * coef; // y point of current c point
        return new double[]{cx, cy};
    }
}
