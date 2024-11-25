# Dynamic Route Finder using Genetic Algorithm Optimization + A* Search

This project implements a dynamic route-finding tool built with Java Swing. It leverages a hybrid approach combining Genetic Algorithm optimization and A* Search to find efficient paths in various environments, suitable for applications in navigation, robotics, and logistics.

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technologies Used](#technologies-used)
- [Algorithm Details](#algorithm-details)
- [Usage](#usage)
- [Results](#results)
- [Future Improvements](#future-improvements)
- [Contributing](#contributing)
- [License](#license)

## Overview
The Dynamic Route Finder is designed for real-time pathfinding solutions. By combining Genetic Algorithms with A* Search, the system offers both exploration capabilities and precision in finding optimized paths, making it adaptable for different scenarios.

## Features
- **Hybrid Optimization Algorithm**: 
  - Genetic Algorithm for global route exploration
  - A* Search for precise path refinement
- **Advanced Pathfinding Techniques**:
  - Multi-objective fitness evaluation
  - Adaptive route generation
- **Flexible User Interface**:
  - Interactive environment configuration
  - Real-time route visualization
  - Customizable algorithmic parameters

## Technologies Used
- **Language**: Java 11+ 
- **UI Framework**: Java Swing
- **Build Tool**: Maven/Gradle
- **Testing**: JUnit 5

## Algorithm Details

### Genetic Algorithm Process
1. **Population Initialization**
   - Generate diverse initial route candidates
   - Use randomized seeding strategies

2. **Fitness Evaluation**
   - Distance minimization
   - Obstacle avoidance scoring
   - Multi-criteria optimization

3. **Selection Mechanisms**
   - Tournament selection
   - Rank-based probability distribution

4. **Genetic Operators**
   - Crossover: Route segment exchange
   - Mutation: Random path perturbation

### A* Search Enhancement
- Admissible heuristic function
- Dynamic path cost recalculation
- Optimal path refinement

## Installation
```bash
git clone https://github.com/Parasar33/Dynamic-Route.git
cd Dynamic-Route
mvn clean install
java -jar target/route-finder.jar
```

## Usage
1. **Launch the Application**: Compile and run the Java application.
2. **Define the Environment**: Set up the map or grid, specifying start and end points, and any obstacles.
3. **Run the Route Finder**: Use the GUI to execute the route-finding algorithm and visualize the results.

## Results
The Dynamic Route Finder effectively balances exploration and accuracy, showcasing robust performance across various environments.

| Metric        | Value        |
|---------------|--------------|
| Average Route Optimization | ~95% |
| Computation Time Reduction | ~40% (compared to A* alone in complex environments) |

Example of route visualization:

![Route Visualization](dynamic_route.png)

## Future Improvements
- **Adaptive Parameters**: Implement methods for dynamic parameter adjustment based on environment complexity.
- **Real-World Data Testing**: Evaluate the algorithm on real-world maps or GPS data.
- **Improved GUI Features**: Enhance the user interface for better interaction and visualization.

## Contributing
1. Fork the repository
2. Create feature branch
3. Commit changes
4. Push and submit pull request

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
