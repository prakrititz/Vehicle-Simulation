# 🚗 Smart Vehicle Traffic Simulation

A sophisticated traffic simulation system that models electric vehicles (EVs) navigating through a dynamic traffic network with intelligent pathfinding and traffic signal management.

![Vehicle Simulation Demo](demo.gif)

## ✨ Features

- **Real-time EV Simulation**

  - Dynamic vehicle movement
  - Battery charge monitoring
  - Intelligent charging system
  - Multiple vehicle types support

- **Traffic Management**

  - Adaptive traffic signals
  - Collision prevention
  - Smart pathfinding using native algorithms
  - Interactive traffic node system

- **Visualization**
  - Web-based GUI using Phaser.js
  - Terminal-based visualization option
  - Real-time position tracking
  - Traffic signal status display

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven
- Modern web browser (for GUI version)

### System Configuration

1. Add the DLL to System Path:
   - Copy `dijkstra_jni.dll` from `Vehicle-Simulation/lib` to a known location
   - Add the DLL directory to your system's PATH environment variable:
     - Windows:
       - Open System Properties > Advanced > Environment Variables
       - Add the DLL directory path to the PATH variable
     - Linux/Mac:
       - Add to LD_LIBRARY_PATH: `export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/path/to/dll`

### Running the Application

1. Clone the repository:

```bash
git clone https://github.com/yourusername/smart-vehicle-simulation.git
cd smart-vehicle-simulation
mvn clean install
mvn spring-boot:run
```
