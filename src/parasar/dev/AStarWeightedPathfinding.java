//package parasar.dev;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseAdapter;
//import java.awt.event.MouseEvent;
//import java.util.*;
//import java.util.List;
//
//public class AStarWeightedPathfinding extends JPanel {
//
//    private static final long serialVersionUID = 1L;
//    private final int gridSize = 50; // Size of each cell
//    private final int rows = 10;
//    private final int cols = 10;
//    private final Cell[][] cells = new Cell[rows][cols];
//    private Cell startCell;
//    private Cell endCell;
//
//    private List<Cell> path = new ArrayList<>();
//
//    public AStarWeightedPathfinding() {
//        setLayout(new GridLayout(rows, cols));
//        initializeGrid();
//        setupControls();
//    }
//
//    private void initializeGrid() {
//        for (int row = 0; row < rows; row++) {
//            for (int col = 0; col < cols; col++) {
//                cells[row][col] = new Cell(row, col);
//                add(cells[row][col]);
//            }
//        }
//    }
//
//    private void setupControls() {
//        JButton findPathButton = new JButton("Find Path");
//        findPathButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                findPath();
//                repaint(); // Repaint to visualize the path
//            }
//        });
//        add(findPathButton);
//    }
//
//    private void handleCellClick(Cell clickedCell) {
//        if (startCell == null) {
//            startCell = clickedCell;
//            clickedCell.setBackground(Color.GREEN);
//        } else if (endCell == null) {
//            endCell = clickedCell;
//            clickedCell.setBackground(Color.RED);
//        } else {
//            startCell.setBackground(Color.WHITE);
//            endCell.setBackground(Color.WHITE);
//            startCell = clickedCell;
//            clickedCell.setBackground(Color.GREEN);
//        }
//    }
//
//    private void findPath() {
//        if (startCell == null || endCell == null) {
//            JOptionPane.showMessageDialog(this, "Please select start and end points.");
//            return;
//        }
//
//        // A* Pathfinding algorithm
//        PriorityQueue<Cell> openSet = new PriorityQueue<>(Comparator.comparingInt(Cell::getFCost));
//        Set<Cell> closedSet = new HashSet<>();
//
//        startCell.setGCost(0);
//        startCell.setHCost(heuristic(startCell, endCell));
//        openSet.add(startCell);
//
//        while (!openSet.isEmpty()) {
//            Cell current = openSet.poll();
//
//            if (current.equals(endCell)) {
//                path = reconstructPath(current);
//                break;
//            }
//
//            closedSet.add(current);
//
//            for (Cell neighbor : getNeighbors(current)) {
//                if (closedSet.contains(neighbor)) continue;
//
//                int tentativeGCost = current.getGCost() + neighbor.getWeight();
//
//                if (tentativeGCost < neighbor.getGCost()) {
//                    neighbor.setGCost(tentativeGCost);
//                    neighbor.setHCost(heuristic(neighbor, endCell));
//                    neighbor.setParent(current);
//
//                    if (!openSet.contains(neighbor)) {
//                        openSet.add(neighbor);
//                    }
//                }
//            }
//        }
//    }
//
//    private List<Cell> getNeighbors(Cell cell) {
//        List<Cell> neighbors = new ArrayList<>();
//        int[] dRow = { -1, 1, 0, 0 };
//        int[] dCol = { 0, 0, -1, 1 };
//
//        for (int i = 0; i < 4; i++) {
//            int newRow = cell.getRow() + dRow[i];
//            int newCol = cell.getCol() + dCol[i];
//
//            if (isValid(newRow, newCol)) {
//                neighbors.add(cells[newRow][newCol]);
//            }
//        }
//        return neighbors;
//    }
//
//    private boolean isValid(int row, int col) {
//        return row >= 0 && row < rows && col >= 0 && col < cols;
//    }
//
//    private int heuristic(Cell a, Cell b) {
//        return Math.abs(a.getRow() - b.getRow()) + Math.abs(a.getCol() - b.getCol());
//    }
//
//    private List<Cell> reconstructPath(Cell current) {
//        List<Cell> path = new ArrayList<>();
//        while (current != null) {
//            path.add(current);
//            current = current.getParent();
//        }
//        Collections.reverse(path);
//        return path;
//    }
//
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        for (Cell[] row : cells) {
//            for (Cell cell : row) {
//                if (path.contains(cell)) {
//                    g.setColor(Color.BLUE);
//                    g.fillRect(cell.getCol() * gridSize, cell.getRow() * gridSize, gridSize, gridSize);
//                }
//                g.setColor(Color.BLACK);
//                g.drawRect(cell.getCol() * gridSize, cell.getRow() * gridSize, gridSize, gridSize);
//            }
//        }
//    }
//
//    private class Cell extends JPanel {
//        private static final long serialVersionUID = 1L;
//
//        private final int row;
//        private final int col;
//        private int weight = 1; // Default weight
//        private int gCost = Integer.MAX_VALUE;
//        private int hCost = 0;
//        private Cell parent;
//
//        public Cell(int row, int col) {
//            this.row = row;
//            this.col = col;
//            setPreferredSize(new Dimension(gridSize, gridSize));
//            setBackground(Color.WHITE);
//            setBorder(BorderFactory.createLineBorder(Color.BLACK));
//
//            addMouseListener(new MouseAdapter() {
//                @Override
//                public void mouseClicked(MouseEvent e) {
//                    handleCellClick(Cell.this);
//                }
//            });
//        }
//
//        public int getRow() { return row; }
//        public int getCol() { return col; }
//        public int getWeight() { return weight; }
//        public void setWeight(int weight) { this.weight = weight; }
//        public int getGCost() { return gCost; }
//        public void setGCost(int gCost) { this.gCost = gCost; }
//        public int getHCost() { return hCost; }
//        public void setHCost(int hCost) { this.hCost = hCost; }
//        public int getFCost() { return gCost + hCost; }
//        public Cell getParent() { return parent; }
//        public void setParent(Cell parent) { this.parent = parent; }
//    }
//}
