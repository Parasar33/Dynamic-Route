//package parasar.dev;
//
//import javax.swing.*;
//import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
//import java.util.*;
//import java.util.List;
//
//class WeightedNode implements Comparable<WeightedNode> {
//    public int x, y;         // Coordinates
//    public int gCost;        // Cost from start node
//    public int hCost;        // Heuristic cost to destination
//    public WeightedNode parent; // Parent node for path reconstruction
//
//    public WeightedNode(int x, int y) {
//        this.x = x;
//        this.y = y;
//        this.gCost = Integer.MAX_VALUE; // Initialize to a high value
//        this.hCost = 0;
//        this.parent = null;
//    }
//
//    // fCost is the sum of gCost and hCost
//    public int fCost() {
//        return gCost + hCost;
//    }
//
//    @Override
//    public int compareTo(WeightedNode other) {
//        return Integer.compare(this.fCost(), other.fCost());
//    }
//}
//
//public class ComplexMap extends JPanel {
//    private static final int CELL_SIZE = 20;  // Size of each grid cell (adjust for large panel size)
//    private static final int PANEL_WIDTH = 1261;
//    private static final int PANEL_HEIGHT = 378;
//    private static final int GRID_ROWS = PANEL_HEIGHT / CELL_SIZE;
//    private static final int GRID_COLS = PANEL_WIDTH / CELL_SIZE;
//
//    private static final int[][] DIRECTIONS = {
//        {0, 1}, {1, 0}, {0, -1}, {-1, 0},   // Right, Down, Left, Up (4 directions)
//        {1, 1}, {1, -1}, {-1, 1}, {-1, -1}  // Diagonal directions
//    };
//
//    // Weighted grid (adjust as needed)
//    private int[][] grid = new int[GRID_ROWS][GRID_COLS];
//    private WeightedNode start = new WeightedNode(0, 0);
//    private WeightedNode goal = new WeightedNode(GRID_ROWS - 1, GRID_COLS - 1);
//    private List<WeightedNode> path = null;  // The optimal path
//
//    public ComplexMap() {
//        setPreferredSize(new Dimension(PANEL_WIDTH, PANEL_HEIGHT));
//
//        // Fill grid with random weights and some obstacles
//        Random random = new Random();
//        for (int i = 0; i < GRID_ROWS; i++) {
//            for (int j = 0; j < GRID_COLS; j++) {
//                grid[i][j] = random.nextInt(3) + 1; // Random weights between 1 and 3
//            }
//        }
//
//        // Add some obstacles
//        for (int i = 0; i < GRID_ROWS; i++) {
//            grid[i][GRID_COLS / 2] = 100;  // Create a vertical wall of obstacles in the middle
//        }
//
//        JButton findPathButton = new JButton("Find Path");
//        findPathButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                path = aStar(grid, start, goal);  // Run the A* algorithm
//                repaint();  // Repaint to visualize the path
//            }
//        });
//
//        // Reset button
//        JButton resetButton = new JButton("Reset");
//        resetButton.addActionListener(new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                path = null;  // Clear the current path
//                repaint();  // Repaint to clear visualization
//            }
//        });
//
//        // Add buttons to the panel
//        JFrame frame = new JFrame("A* Pathfinding with Weighted Grid on Large Panel");
//        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
//        frame.setLayout(new BorderLayout());
//        JPanel controlPanel = new JPanel();
//        controlPanel.add(findPathButton);
//        controlPanel.add(resetButton);
//        frame.add(controlPanel, BorderLayout.SOUTH);
//        frame.add(this, BorderLayout.CENTER);
//        frame.pack();
//        frame.setVisible(true);
//    }
//
//    // A* algorithm with weighted edges
//    public List<WeightedNode> aStar(int[][] grid, WeightedNode start, WeightedNode goal) {
//        PriorityQueue<WeightedNode> openSet = new PriorityQueue<>();  // Open set
//        Set<WeightedNode> closedSet = new HashSet<>();                // Closed set
//        start.gCost = 0;
//        start.hCost = heuristic(start, goal);                         // Initial heuristic
//        openSet.add(start);
//
//        while (!openSet.isEmpty()) {
//            WeightedNode current = openSet.poll();  // Get the node with the lowest fCost
//
//            if (current.x == goal.x && current.y == goal.y) {
//                return reconstructPath(current);  // Goal reached
//            }
//
//            closedSet.add(current);
//
//            // Explore neighbors
//            for (int[] direction : DIRECTIONS) {
//                int neighborX = current.x + direction[0];
//                int neighborY = current.y + direction[1];
//
//                if (!isValid(neighborX, neighborY, grid)) continue;
//
//                WeightedNode neighbor = new WeightedNode(neighborX, neighborY);
//                if (closedSet.contains(neighbor)) continue;  // Skip if already visited
//
//                int tentativeGCost = current.gCost + grid[neighborX][neighborY];
//
//                if (tentativeGCost < neighbor.gCost || !openSet.contains(neighbor)) {
//                    neighbor.gCost = tentativeGCost;
//                    neighbor.hCost = heuristic(neighbor, goal);
//                    neighbor.parent = current;
//
//                    if (!openSet.contains(neighbor)) {
//                        openSet.add(neighbor);
//                    }
//                }
//            }
//        }
//        return null;  // No path found
//    }
//
//    // Heuristic function (Manhattan distance)
//    private int heuristic(WeightedNode a, WeightedNode b) {
//        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
//    }
//
//    // Backtrack to reconstruct path
//    private List<WeightedNode> reconstructPath(WeightedNode current) {
//        List<WeightedNode> path = new ArrayList<>();
//        while (current != null) {
//            path.add(current);
//            current = current.parent;
//        }
//        Collections.reverse(path);
//        return path;
//    }
//
//    // Check if position is within grid bounds
//    private boolean isValid(int x, int y, int[][] grid) {
//        return x >= 0 && y >= 0 && x < grid.length && y < grid[0].length && grid[x][y] != 100;  // Valid if not obstacle
//    }
//
//    // Paint the grid and path
//    @Override
//    protected void paintComponent(Graphics g) {
//        super.paintComponent(g);
//
//        // Draw the grid
//        for (int i = 0; i < grid.length; i++) {
//            for (int j = 0; j < grid[0].length; j++) {
//                if (grid[i][j] == 100) {  // Represent obstacles
//                    g.setColor(Color.BLACK);
//                } else {
//                    g.setColor(Color.LIGHT_GRAY);
//                }
//                g.fillRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//                g.setColor(Color.BLACK);
//                g.drawRect(j * CELL_SIZE, i * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//
//                // Draw weights on the grid
//                g.setColor(Color.BLACK);
//                g.drawString(String.valueOf(grid[i][j]), j * CELL_SIZE + 5, i * CELL_SIZE + 15);
//            }
//        }
//
//        // Draw start and goal nodes
//        g.setColor(Color.GREEN);
//        g.fillRect(start.y * CELL_SIZE, start.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//
//        g.setColor(Color.RED);
//        g.fillRect(goal.y * CELL_SIZE, goal.x * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//
//        // Draw the path if it exists
//        if (path != null) {
//            g.setColor(Color.BLUE);
//            for (WeightedNode node : path) {
//                g.fillRect(node.y * CELL_SIZE + 5, node.x * CELL_SIZE + 5, CELL_SIZE - 10, CELL_SIZE - 10);
//            }
//        }
//    }
//
//    // Method to return the map panel (getMapPanel function)
//    public JPanel getMapPanel() {
//        return this;
//    }
//}
