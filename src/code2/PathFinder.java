package code2;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/**
*  Back end of the application
* @author RISHAV
* @version 2.0
* @since 2024-09-20
*/
public class PathFinder extends JPanel {
    private static final int CELL_SIZE = 20;
    private static final int GRID_WIDTH = 63;
    private static final int GRID_HEIGHT = 31;

    private List<Customer> customers; // List to store customers with pickup and drop-off
    private Point carPosition; // Position of the car
    private boolean[][] barriers;
    private Timer timer;
    private List<Point> traces;
    private Color currentTraceColor;
    private List<Point> currentPath;
    private int customerCounter; // Counter to number the customers

    public PathFinder() {
        setPreferredSize(new Dimension(GRID_WIDTH * CELL_SIZE, GRID_HEIGHT * CELL_SIZE));
        customers = new ArrayList<>();
        carPosition = new Point(0, 0);
        traces = new ArrayList<>();
        currentTraceColor = Color.MAGENTA;
        currentPath = new ArrayList<>();
        customerCounter = 1; // Initializing the counter for numbering customers

        barriers = new boolean[GRID_WIDTH][GRID_HEIGHT];
        createBarriers();

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point clickedPoint = e.getPoint();
                int gridX = clickedPoint.x / CELL_SIZE;
                int gridY = clickedPoint.y / CELL_SIZE;

                if (isValidPoint(gridX, gridY)) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // Add a new customer with both pickup and drop-off
                        Point pickupPoint = new Point(gridX, gridY);
                        Point dropOffPoint = getRandomDropOffPoint();
                        customers.add(new Customer(pickupPoint, dropOffPoint, customerCounter++));
                    }
                    repaint();
                }
            }
        });
    }

    private void createBarriers() {
        // Example barriers with gaps
        for (int i = 0; i < GRID_WIDTH; i++) {
            if (i % 10 != 0) {
                barriers[i][10] = true; // Horizontal barrier
            }
        }
        for (int j = 5; j < 15; j++) {
            if (j % 5 != 0) {
                barriers[30][j] = true; // Vertical barrier
            }
        }
    }

    private boolean isValidPoint(int x, int y) {
        return x >= 0 && x < GRID_WIDTH && y >= 0 && y < GRID_HEIGHT && !barriers[x][y];
    }

    private Point getRandomDropOffPoint() {
        Random random = new Random();
        int x, y;
        do {
            x = random.nextInt(GRID_WIDTH);
            y = random.nextInt(GRID_HEIGHT);
        } while (!isValidPoint(x, y)); // Ensure drop-off point is valid
        return new Point(x, y);
    }

    public void startRideSharingSimulation() {
        if (!customers.isEmpty() && timer == null) {
            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (currentPath.isEmpty()) {
                        Customer nearestCustomer = getNearestCustomer();
                        if (nearestCustomer != null) {
                            // First move to pickup, then drop-off
                            Point nextTarget = nearestCustomer.hasPickedUp ? nearestCustomer.dropOff : nearestCustomer.pickUp;
                            currentPath = aStar(carPosition, nextTarget);
                        }
                        // If no path is found, stop
                        if (currentPath.isEmpty()) {
                            return;
                        }
                    }

                    if (!currentPath.isEmpty()) {
                        carPosition = currentPath.remove(0); // Move along the path
                        traces.add(new Point(carPosition)); // Leave a trace
                        repaint();
                    }

                    // Check if car has reached a customer's pickup point
                    for (Customer customer : customers) {
                        if (!customer.hasPickedUp && customer.pickUp.equals(carPosition)) {
                            customer.hasPickedUp = true; // Mark the customer as picked up
                            changeTraceColor();
                            break;
                        } else if (customer.hasPickedUp && customer.dropOff.equals(carPosition)) {
                            customers.remove(customer); // Drop off the customer
                            break;
                        }
                    }

                    if (customers.isEmpty()) {
                        timer.cancel();
                        timer = null;
                    }
                }
            }, 0, 100); // Reduce delay to make it faster (50 ms per move)
        }
    }

    private Customer getNearestCustomer() {
        if (customers.isEmpty()) return null;

        Customer nearestCustomer = null;
        int shortestDistance = Integer.MAX_VALUE;

        for (Customer customer : customers) {
            Point targetPoint = customer.hasPickedUp ? customer.dropOff : customer.pickUp;
            int distance = manhattanDistance(carPosition, targetPoint);
            if (distance < shortestDistance) {
                shortestDistance = distance;
                nearestCustomer = customer;
            }
        }

        return nearestCustomer;
    }

    private void changeTraceColor() {
        currentTraceColor = new Color((int) (Math.random() * 0x1000000)); // Random vibrant color
    }

    public void reset() {
        customers.clear();
        carPosition = new Point(0, 0);
        traces.clear();
        currentTraceColor = Color.MAGENTA;
        currentPath.clear();
        customerCounter = 1;
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (int i = 0; i < GRID_WIDTH; i++) {
            for (int j = 0; j < GRID_HEIGHT; j++) {
                if (barriers[i][j]) {
                    g.setColor(Color.GRAY);
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                } else {
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
                }
                g.setColor(Color.BLACK);
                g.drawRect(i * CELL_SIZE, j * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }

        // Draw traces
        g.setColor(currentTraceColor);
        for (Point trace : traces) {
            g.fillRect(trace.x * CELL_SIZE, trace.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
        }

        // Draw customers (pickup and drop-off points)
        for (Customer customer : customers) {
            // Draw pickup point
            g.setColor(Color.GREEN);
            g.fillRect(customer.pickUp.x * CELL_SIZE, customer.pickUp.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(customer.id), customer.pickUp.x * CELL_SIZE + 5, customer.pickUp.y * CELL_SIZE + 15);

            // Draw drop-off point
            g.setColor(Color.RED);
            g.fillRect(customer.dropOff.x * CELL_SIZE, customer.dropOff.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            g.setColor(Color.BLACK);
            g.drawString(String.valueOf(customer.id), customer.dropOff.x * CELL_SIZE + 5, customer.dropOff.y * CELL_SIZE + 15);
        }

        // Draw the car
        g.setColor(Color.BLUE);
        g.fillRect(carPosition.x * CELL_SIZE, carPosition.y * CELL_SIZE, CELL_SIZE, CELL_SIZE);
    }

    private List<Point> aStar(Point start, Point goal) {
        PriorityQueue<Node> openList = new PriorityQueue<>(Comparator.comparingInt(n -> n.gCost + n.hCost));
        Map<Point, Node> allNodes = new HashMap<>();

        Node startNode = new Node(start, null, 0, manhattanDistance(start, goal));
        openList.add(startNode);
        allNodes.put(start, startNode);

        while (!openList.isEmpty()) {
            Node currentNode = openList.poll();

            if (currentNode.position.equals(goal)) {
                return reconstructPath(currentNode);
            }

            for (Point neighbor : getNeighbors(currentNode.position)) {
                int newCost = currentNode.gCost + 1;
                Node neighborNode = allNodes.getOrDefault(neighbor, new Node(neighbor, null, Integer.MAX_VALUE, 0));

                if (newCost < neighborNode.gCost) {
                    neighborNode.gCost = newCost;
                    neighborNode.hCost = manhattanDistance(neighbor, goal);
                    neighborNode.parent = currentNode;
                    openList.add(neighborNode);
                    allNodes.put(neighbor, neighborNode);
                }
            }
        }

        return new ArrayList<>(); // Return empty list if no path found
    }

    private List<Point> reconstructPath(Node node) {
        List<Point> path = new ArrayList<>();
        while (node != null) {
            path.add(0, node.position);
            node = node.parent;
        }
        return path;
    }

    private int manhattanDistance(Point p1, Point p2) {
        return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
    }

    private List<Point> getNeighbors(Point point) {
        List<Point> neighbors = new ArrayList<>();
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}}; // Right, Left, Down, Up
        for (int[] direction : directions) {
            int newX = point.x + direction[0];
            int newY = point.y + direction[1];
            if (isValidPoint(newX, newY)) {
                neighbors.add(new Point(newX, newY));
            }
        }
        return neighbors;
    }

    private static class Node {
        Point position;
        Node parent;
        int gCost;
        int hCost;

        Node(Point position, Node parent, int gCost, int hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
        }
    }

    private static class Customer {
        Point pickUp;
        Point dropOff;
        int id;
        boolean hasPickedUp;

        Customer(Point pickUp, Point dropOff, int id) {
            this.pickUp = pickUp;
            this.dropOff = dropOff;
            this.id = id;
            this.hasPickedUp = false;
        }
    }
}
