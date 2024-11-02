package parasar.dev;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.*;

enum CellType {
    EMPTY, START, GOAL, WALL, PATH
}

class Cell {
    int row, col;
    CellType type;
    Cell parent;
    double g, h, f;

    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        this.type = CellType.EMPTY;
    }

    public void setType(CellType type) {
        this.type = type;
    }

    public void calculateF() {
        this.f = this.g + this.h;
    }
}

public class PathFinder extends JPanel implements MouseListener, MouseMotionListener {
    private int cellSize;
    private int rows;
    private int columns;
    private final int WIDTH = 1261;
    private final int HEIGHT = 625;

    private Color colorStart = new Color(0, 179, 179);
    private Color colorGoal = new Color(255, 102, 102);
    private Color colorWall = new Color(20, 31, 31);
    private Color colorPath = new Color(51, 255, 51);

    private boolean startSelected;
    private boolean goalSelected;

    private Timer timer;
    private List<Cell> pathToRender;
    private int currentPathIndex = 0;
    private long startTime;
    private long endTime;
    
    private Cell[][] board;

    public PathFinder() {
        initPanel();
    }

    private void initPanel() {
        addMouseListener(this);
        addMouseMotionListener(this);
        setBackground(Color.WHITE);
        setFocusable(true);
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        initBoard(31, 63, 20); // Adjusted for 1261x625 with cell size 20
    }

    private void initBoard(int rows, int columns, int cellSize) {
        startSelected = false;
        goalSelected = false;
        this.rows = rows;
        this.columns = columns;
        this.cellSize = cellSize;
        
        board = new Cell[rows][columns];
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                board[y][x] = new Cell(y, x);
            }
        }

        repaint();
    }

    public void updateBoard(int slideSize) {
        if (slideSize >= 66) {
            initBoard(31, 63, 20);
        } else if (slideSize >= 33) {
            initBoard(17, 34, 30);
        } else {
            initBoard(10, 21, 60);
        }
    }

    public void resetBoard() {
        initBoard(rows, columns, cellSize);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                switch (board[y][x].type) {
                    case START:
                        g2.setColor(colorStart);
                        break;
                    case GOAL:
                        g2.setColor(colorGoal);
                        break;
                    case WALL:
                        g2.setColor(colorWall);
                        break;
                    case PATH:
                        g2.setColor(colorPath);
                        break;
                    default:
                        g2.setColor(Color.WHITE);
                        break;
                }
                g2.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
            }
        }

        g2.setColor(Color.BLACK);
        for (int y = 0; y < rows; y++) {
            g2.drawLine(0, y * cellSize, WIDTH, y * cellSize);
        }
        for (int x = 0; x < columns; x++) {
            g2.drawLine(x * cellSize, 0, x * cellSize, HEIGHT);
        }
    }

    private List<Cell> findPath() {
        PriorityQueue<Cell> openList = new PriorityQueue<>((a, b) -> Double.compare(a.f, b.f));
        List<Cell> closedList = new ArrayList<>();

        Cell startCell = getCellWithType(CellType.START);
        if (startCell == null) return null;  // No start cell
        startCell.g = 0;
        startCell.h = heuristic(startCell, getCellWithType(CellType.GOAL));
        startCell.calculateF();

        openList.add(startCell);

        while (!openList.isEmpty()) {
            Cell currentCell = openList.poll();

            if (currentCell.type == CellType.GOAL) {
                return buildPathFromCell(currentCell);
            }

            closedList.add(currentCell);

            for (Cell neighbor : getNeighbors(currentCell)) {
                if (closedList.contains(neighbor) || neighbor.type == CellType.WALL) {
                    continue;
                }

                double tentativeG = currentCell.g + 1;

                if (openList.contains(neighbor) && tentativeG >= neighbor.g) {
                    continue;
                }

                neighbor.parent = currentCell;
                neighbor.g = tentativeG;
                neighbor.h = heuristic(neighbor, getCellWithType(CellType.GOAL));
                neighbor.calculateF();

                if (!openList.contains(neighbor)) {
                    openList.offer(neighbor);
                }
            }
        }

        return null; // Return null if no path is found
    }


    public String noSolution() {
        if (pathToRender == null || pathToRender.isEmpty()) {
            return "No solution found";
        }
        return "Solution found";
    }


    private List<Cell> buildPathFromCell(Cell cell) {
        List<Cell> path = new ArrayList<>();
        while (cell != null) {
            path.add(cell);
            cell = cell.parent;
        }
        Collections.reverse(path);
        return path;
    }

    private double heuristic(Cell a, Cell b) {
        return Math.abs(a.row - b.row) + Math.abs(a.col - b.col);
    }

    private Cell getCellWithType(CellType cellType) {
        for (int y = 0; y < rows; y++) {
            for (int x = 0; x < columns; x++) {
                if (board[y][x].type == cellType) {
                    return board[y][x];
                }
            }
        }
        return null;
    }

    private List<Cell> getNeighbors(Cell cell) {
        List<Cell> neighbors = new ArrayList<>();
        int[] dx = {-1, 0, 1, 0};
        int[] dy = {0, -1, 0, 1};

        for (int i = 0; i < 4; i++) {
            int newRow = cell.row + dy[i];
            int newCol = cell.col + dx[i];

            if (newRow >= 0 && newRow < rows && newCol >= 0 && newCol < columns) {
                neighbors.add(board[newRow][newCol]);
            }
        }

        return neighbors;
    }

    private Runnable timeDisplayUpdater;

    public void addTimeDisplayUpdater(Runnable updater) {
        this.timeDisplayUpdater = updater;
    }

    private void notifyTimeDisplayUpdater() {
        if (timeDisplayUpdater != null) {
            timeDisplayUpdater.run();
        }
    }

    public void solvePath() {
        if (startSelected && goalSelected) {
            startTime = System.currentTimeMillis(); // Record start time
            pathToRender = findPath(); // Find the path
            endTime = System.currentTimeMillis(); // Record end time
            currentPathIndex = 0;

            if (timer != null && timer.isRunning()) timer.stop();

            // If a path is found, animate the path
            if (pathToRender != null && !pathToRender.isEmpty()) {
                timer = new Timer(80, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (currentPathIndex < pathToRender.size()) {
                            Cell cell = pathToRender.get(currentPathIndex);
                            if (cell.type != CellType.START && cell.type != CellType.GOAL) {
                                cell.setType(CellType.PATH);
                            }
                            currentPathIndex++;
                            repaint();
                        } else {
                            timer.stop();
                            notifyTimeDisplayUpdater(); // Update time and status after pathfinding completes
                        }
                    }
                });
                timer.start();
            } else {
                // If no path is found, immediately update the time and solution status
                notifyTimeDisplayUpdater(); // Update the JTextArea even if no path is found
            }
        }
    }





    public long timeTaken() {
        return endTime - startTime;
    }

    @Override
    public void mouseClicked(MouseEvent mouseEvent) {
        int x = mouseEvent.getX() / cellSize;
        int y = mouseEvent.getY() / cellSize;

        if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
            if (!startSelected) {
                board[y][x].setType(CellType.START);
                startSelected = true;
            } else if (!goalSelected) {
                board[y][x].setType(CellType.GOAL);
                goalSelected = true;
            } else {
                board[y][x].setType(CellType.WALL);
            }
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            if (board[y][x].type == CellType.START) startSelected = false;
            else if (board[y][x].type == CellType.GOAL) goalSelected = false;

            board[y][x].setType(CellType.EMPTY);
        }

        repaint();
    }

    @Override
    public void mouseDragged(MouseEvent mouseEvent) {
        int x = mouseEvent.getX() / cellSize;
        int y = mouseEvent.getY() / cellSize;

        if (x < 0 || x >= columns || y < 0 || y >= rows) {
            return;
        }

        if (SwingUtilities.isLeftMouseButton(mouseEvent) && board[y][x].type != CellType.START && board[y][x].type != CellType.GOAL) {
            board[y][x].setType(CellType.WALL);
        } else if (SwingUtilities.isRightMouseButton(mouseEvent)) {
            board[y][x].setType(CellType.EMPTY);
        }

        repaint();
    }

    public void mousePressed(MouseEvent mouseEvent) {}
    public void mouseReleased(MouseEvent mouseEvent) {}
    public void mouseEntered(MouseEvent mouseEvent) {}
    public void mouseExited(MouseEvent mouseEvent) {}
    public void mouseMoved(MouseEvent mouseEvent) {}
}
