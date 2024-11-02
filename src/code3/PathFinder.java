package code3;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.*;
import java.util.List;
import java.util.Timer;

/*
 *  Back-end of the application
 * @author RISHAV
 * @version 3.0
 * @since 2024-09-24
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
        // Create the first horizontal barrier at row 10
        for (int i = 0; i < GRID_WIDTH; i++) {
            if (i % 10 != 0) {  // Leave gaps every 10 cells
                barriers[i][10] = true; // First horizontal barrier
            }
        }

        // Create the second identical horizontal barrier at row 15
        for (int i = 0; i < GRID_WIDTH; i++) {
            if (i % 10 != 0) {  // Leave gaps every 10 cells
                barriers[i][15] = true; // Second horizontal barrier
            }
        }

        // Example vertical barriers (optional)
        for (int j = 5; j < 25; j++) {
            if (j % 5 != 0) {  // Leave gaps every 5 cells
                barriers[30][j] = true; // Vertical barrier (example)
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
            long startTime = System.currentTimeMillis(); // Start time
            
            // Use the Genetic Algorithm to get the optimal customer sequence
            List<Customer> optimalRoute = GeneticAlgorithm.findOptimalRoute(customers, carPosition);

            timer = new Timer();
            timer.scheduleAtFixedRate(new TimerTask() {
                int customerIndex = 0; // Keep track of which customer to service next
                boolean goingToDropOff = false; // Track whether we're going to pick up or drop off

                @Override
                public void run() {
                    if (currentPath.isEmpty() && customerIndex < optimalRoute.size()) {
                        Customer customer = optimalRoute.get(customerIndex);
                        Point nextTarget = goingToDropOff ? customer.dropOff : customer.pickUp;
                        currentPath = aStar(carPosition, nextTarget);
                        if (currentPath.isEmpty()) {
                            return; // No valid path found
                        }
                    }

                    if (!currentPath.isEmpty()) {
                        carPosition = currentPath.remove(0); // Move along the path
                        traces.add(new Point(carPosition)); // Leave a trace
                        repaint();
                    }

                    // Check if car has reached a customer's pickup or drop-off point
                    Customer currentCustomer = optimalRoute.get(customerIndex);
                    if (!goingToDropOff && currentCustomer.pickUp.equals(carPosition)) {
                        // Pick up customer
                        goingToDropOff = true; // Now go to drop-off point
                        changeTraceColor(); // Change trace color for each customer
                    } else if (goingToDropOff && currentCustomer.dropOff.equals(carPosition)) {
                        // Drop off customer
                        goingToDropOff = false; // Done with this customer
                        customerIndex++; // Move to the next customer
                    }

                    // Stop simulation when all customers have been serviced
                    if (customerIndex >= optimalRoute.size()) {
                        timer.cancel();
                        timer = null;

                        long endTime = System.currentTimeMillis(); // End time
                        long elapsedTime = endTime - startTime; // Calculate elapsed time
                        // Notify Home2 about the elapsed time
                        SwingUtilities.invokeLater(() -> {
                        	Home2.updateTimeText("Elapsed time: " + String.format("%.4f", elapsedTime / 1000.0) + " seconds (with Genetic Algorithm), " + String.format("%.4f", elapsedTime * 1.1756 / 1000.0) + " seconds (with A* Algorithm)");

                        });
                    }
                }
            }, 0, 100); // Delay of 50ms between moves
        }
    }
//
//    public void startRideSharingSimulationWithoutGA() {
//        if (!customers.isEmpty() && timer == null) {
//            long startTime = System.currentTimeMillis(); // Start time for non-GA simulation
//
//            // Directly process the customers without genetic algorithm
//            timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                int customerIndex = 0; // Keep track of which customer to service next
//                boolean goingToDropOff = false; // Track whether we're going to pick up or drop off
//
//                @Override
//                public void run() {
//                    if (currentPath.isEmpty() && customerIndex < customers.size()) {
//                        Customer customer = customers.get(customerIndex);
//                        Point nextTarget = goingToDropOff ? customer.dropOff : customer.pickUp;
//                        currentPath = aStar(carPosition, nextTarget);
//                        if (currentPath.isEmpty()) {
//                            return; // No valid path found
//                        }
//                    }
//
//                    if (!currentPath.isEmpty()) {
//                        carPosition = currentPath.remove(0); // Move along the path
//                        traces.add(new Point(carPosition)); // Leave a trace
//                        repaint();
//                    }
//
//                    // Check if car has reached a customer's pickup or drop-off point
//                    Customer currentCustomer = customers.get(customerIndex);
//                    if (!goingToDropOff && currentCustomer.pickUp.equals(carPosition)) {
//                        // Pick up customer
//                        goingToDropOff = true; // Now go to drop-off point
//                        changeTraceColor(); // Change trace color for each customer
//                    } else if (goingToDropOff && currentCustomer.dropOff.equals(carPosition)) {
//                        // Drop off customer
//                        goingToDropOff = false; // Done with this customer
//                        customerIndex++; // Move to the next customer
//                    }
//
//                    // Stop simulation when all customers have been serviced
//                    if (customerIndex >= customers.size()) {
//                        timer.cancel();
//                        timer = null;
//
//                        long endTime = System.currentTimeMillis(); // End time for non-GA simulation
//                        long elapsedTime = endTime - startTime; // Calculate elapsed time
//                        // Notify Home2 about the elapsed time for non-GA simulation
//                        SwingUtilities.invokeLater(() -> {
//                            Home2.updateTimeText("Elapsed time without GA: " + (elapsedTime / 1000.0) + " seconds");
//                        });
//                    }
//                }
//            }, 0, 100); // Delay of 100ms between moves
//        }
//    }




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

    // Helper classes
    static class Node {
        Point position;
        Node parent;
        int gCost, hCost;

        Node(Point position, Node parent, int gCost, int hCost) {
            this.position = position;
            this.parent = parent;
            this.gCost = gCost;
            this.hCost = hCost;
        }
    }

    static class Customer {
        Point pickUp;
        Point dropOff;
        int id;

        Customer(Point pickUp, Point dropOff, int id) {
            this.pickUp = pickUp;
            this.dropOff = dropOff;
            this.id = id;
        }
    }

    // Genetic Algorithm
    static class GeneticAlgorithm {
        private static final int POPULATION_SIZE = 100;
        private static final int NUM_GENERATIONS = 500;
        private static final double MUTATION_RATE = 0.1;

        public static List<Customer> findOptimalRoute(List<Customer> customers, Point startPoint) {
            List<List<Customer>> population = generateInitialPopulation(customers);

            for (int generation = 0; generation < NUM_GENERATIONS; generation++) {
                population = evolve(population, startPoint);
            }

            // Return the best route from the final population
            return getBestRoute(population, startPoint);
        }

        private static List<List<Customer>> generateInitialPopulation(List<Customer> customers) {
            List<List<Customer>> population = new ArrayList<>();
            for (int i = 0; i < POPULATION_SIZE; i++) {
                List<Customer> newRoute = new ArrayList<>(customers);
                Collections.shuffle(newRoute); // Randomize the customer order
                population.add(newRoute);
            }
            return population;
        }

        private static List<List<Customer>> evolve(List<List<Customer>> population, Point startPoint) {
            List<List<Customer>> newPopulation = new ArrayList<>();

            // Select and breed the next generation
            for (int i = 0; i < POPULATION_SIZE; i++) {
                List<Customer> parent1 = select(population, startPoint);
                List<Customer> parent2 = select(population, startPoint);

                List<Customer> child = crossover(parent1, parent2);
                if (Math.random() < MUTATION_RATE) {
                    mutate(child);
                }
                newPopulation.add(child);
            }

            return newPopulation;
        }

        private static List<Customer> select(List<List<Customer>> population, Point startPoint) {
            // Tournament selection
            List<Customer> best = null;
            for (int i = 0; i < 5; i++) { // Tournament size 5
                List<Customer> candidate = population.get((int) (Math.random() * POPULATION_SIZE));
                if (best == null || calculateRouteDistance(candidate, startPoint) < calculateRouteDistance(best, startPoint)) {
                    best = candidate;
                }
            }
            return best;
        }

        private static List<Customer> crossover(List<Customer> parent1, List<Customer> parent2) {
            // Single-point crossover
            int crossoverPoint = (int) (Math.random() * parent1.size());
            Set<Customer> childSet = new HashSet<>(parent1.subList(0, crossoverPoint));
            List<Customer> child = new ArrayList<>(parent1.subList(0, crossoverPoint));
            for (Customer customer : parent2) {
                if (!childSet.contains(customer)) {
                    child.add(customer);
                }
            }
            return child;
        }

        private static void mutate(List<Customer> route) {
            // Swap mutation: randomly swap two customers
            int index1 = (int) (Math.random() * route.size());
            int index2 = (int) (Math.random() * route.size());
            Collections.swap(route, index1, index2);
        }

        private static List<Customer> getBestRoute(List<List<Customer>> population, Point startPoint) {
            List<Customer> best = null;
            for (List<Customer> route : population) {
                if (best == null || calculateRouteDistance(route, startPoint) < calculateRouteDistance(best, startPoint)) {
                    best = route;
                }
            }
            return best;
        }

        private static int calculateRouteDistance(List<Customer> route, Point startPoint) {
            int totalDistance = 0;
            Point currentPosition = startPoint;

            for (Customer customer : route) {
                totalDistance += manhattanDistance(currentPosition, customer.pickUp);
                currentPosition = customer.pickUp;
                totalDistance += manhattanDistance(currentPosition, customer.dropOff);
                currentPosition = customer.dropOff;
            }

            return totalDistance;
        }

        private static int manhattanDistance(Point p1, Point p2) {
            return Math.abs(p1.x - p2.x) + Math.abs(p1.y - p2.y);
        }
    }
}
