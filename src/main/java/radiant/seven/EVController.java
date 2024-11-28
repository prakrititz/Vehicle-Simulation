package radiant.seven;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * REST Controller for managing Electric Vehicles (EVs).
 * Provides endpoints for creating, updating, and monitoring EVs.
 */
@RestController
@RequestMapping("/api/ev")
public class EVController {
    public static Map<String, EV> evMap;
    public static PathfindingVisualizer pathfinder;
    private TrafficManager trafficManager;

    /**
     * Constructor for EVController.
     * Initializes the EV map, pathfinding visualizer, and traffic manager.
     */
    public static Map<String, EV> getEvMap() {
        return EVController.evMap;
    }

    public EVController() {
        evMap = new HashMap<>();
        pathfinder = new PathfindingVisualizer(GameMap.getInstance());
        trafficManager = new TrafficManager();
    }

    /**
     * Creates a new EV and assigns it a path.
     *
     * @param request The request payload containing EV details.
     * @return ResponseEntity with the created EV object.
     *         type = 3 indicates it is an NPC Vehicle
     */
    @PostMapping("/new")
    public ResponseEntity<EV> newEV(@RequestBody EVCreateRequest request) {
        EV ev;
        if (request.getType() == 3) {
            // Create NPC Vehicle
            ev = new NPCVehicle(request.getStartX(), request.getStartY());
        } else {
            // Create regular EV
            ev = new EV(
                    request.getStartX(),
                    request.getStartY(),
                    request.getType(),
                    request.getCharge(),
                    request.getChargingRate(),
                    request.getVehicleType());
        }

        ev.setEndLocation(request.getEndX(), request.getEndY());
        ev.setName(request.getName());

        // Calculate path
        long[] pathArray = pathfinder.findPath(
                ev.getStartX(),
                ev.getStartY(),
                ev.getEndX(),
                ev.getEndY());
        List<PathNode> path = convertToPathNodes(pathArray);
        ev.setPath(path);
        ev.setMoving(true);

        evMap.put(ev.getName(), ev);
        simulateEVMovement(ev.getName());

        return ResponseEntity.ok(ev);
    }

    /**
     * Simulates the movement of an EV along its predefined path in a separate
     * thread.
     * Checks traffic conditions before each movement and introduces delays between
     * moves.
     * 
     * @param evName The name/identifier of the EV to simulate movement for
     */
    private void simulateEVMovement(String evName) {
        EV ev = evMap.get(evName);
        new Thread(() -> {
            while (ev.isMoving() && ev.currentPathIndex < ev.getPath().size() - 1) {
                PathNode nextPos = ev.getPath().get(ev.currentPathIndex + 1);
                if (TrafficManager.getInstance().canMoveToPosition(ev, nextPos.getX(), nextPos.getY())) {
                    ev.updateDirection(nextPos.getX(), nextPos.getY());
                    ev.currentPathIndex++;
                    if(ev.getType()==3)
                        ev.changeEnd();
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }
            }
            ev.setMoving(false);
        }).start();
    }

    @PostMapping("/npc/spawn")
    public ResponseEntity<List<EV>> spawnNPCs() {
        List<EV> spawnedNPCs = new ArrayList<>();
        GameMap gameMap = GameMap.getInstance();
        Random random = new Random();

        // Define available vehicle types
        String[] vehicleTypes = { "ambulance", "coupe", "ev", "hatchback", "luxury",
                "pickup", "sedan", "sport", "super", "suv", "van" };

        for (int i = 0; i < 10; i++) {
            int startX, startY, endX, endY;
            Node startNode, endNode;

            do {
                startX = 1 + random.nextInt(35);
                startY = 1 + random.nextInt(35);
                startNode = gameMap.getRoadNode(startX, startY);
            } while (startNode == null);

            do {
                endX = 1 + random.nextInt(35);
                endY = 1 + random.nextInt(35);
                endNode = gameMap.getRoadNode(endX, endY);
            } while (endNode == null);

            // Select random vehicle type
            String randomVehicleType = vehicleTypes[random.nextInt(vehicleTypes.length)];

            NPCVehicle npc = new NPCVehicle(startX, startY, randomVehicleType);
            npc.setEndLocation(endX, endY);

            long[] pathArray = pathfinder.findPath(startX, startY, endX, endY);
            List<PathNode> path = convertToPathNodes(pathArray);
            npc.setPath(path);
            npc.setMoving(true);

            evMap.put(npc.getName(), npc);
            simulateEVMovement(npc.getName());
            spawnedNPCs.add(npc);
        }

        return ResponseEntity.ok(spawnedNPCs);
    }

    /**
     * Checks if an EV can move to a specified position.
     *
     * @param evName The name of the EV.
     * @param x      Target x-coordinate.
     * @param y      Target y-coordinate.
     * @return ResponseEntity with a boolean indicating if the EV can move.
     */
    @PostMapping("/{evName}/canMoveToPosition/{x}/{y}")
    public ResponseEntity<Boolean> canMoveToPosition(
            @PathVariable String evName,
            @PathVariable int x,
            @PathVariable int y) {
        EV ev = evMap.get(evName);
        if (ev != null) {
            if (ev.getCurrentPathIndex() + 1 < ev.getPath().size()) {
                PathNode nextNode = ev.getPath().get(ev.getCurrentPathIndex() + 1);
                if (nextNode.getX() == x && nextNode.getY() == y) {
                    boolean canMove = trafficManager.canMoveToPosition(ev, x, y);
                    return ResponseEntity.ok(canMove);
                }
            }
            return ResponseEntity.ok(false);
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves a list of all EVs.
     *
     * @return ResponseEntity containing a list of all EVs.
     */
    @GetMapping("/all")
    public ResponseEntity<List<EV>> getAllEVs() {
        // System.out.println("Fetching all EVs");
        return ResponseEntity.ok(new ArrayList<>(EVController.evMap.values()));
    }

    /**
     * Starts an EV's movement along its path.
     *
     * @param evName The name of the EV.
     * @return ResponseEntity containing the EV's path.
     */
    @PostMapping("/{evName}/start")
    public ResponseEntity<List<PathNode>> startEV(@PathVariable String evName) {
        EV ev = evMap.get(evName);
        if (ev != null) {
            ev.setMoving(true);
            return ResponseEntity.ok(ev.getPath());
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deletes an EV.
     *
     * @param evName The name of the EV to delete.
     * @return ResponseEntity indicating the result of the operation.
     */
    @DeleteMapping("/{evName}")
    public ResponseEntity<Void> deleteEV(@PathVariable String evName) {
        if (evMap.containsKey(evName)) {
            evMap.remove(evName);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Updates an EV's position to the next point on its path.
     *
     * @param evName The name of the EV.
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/{evName}/updatePosition")
    public ResponseEntity<Void> updateEVPosition(@PathVariable String evName) {
        EV ev = evMap.get(evName);
        if (ev != null && ev.isMoving() && ev.getCurrentPathIndex() < ev.getPath().size() - 1) {
            ev.currentPathIndex++;
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Retrieves the status of an EV.
     *
     * @param evName The name of the EV.
     * @return ResponseEntity containing the EV's status.
     */
    @GetMapping("/{evName}/status")
    public ResponseEntity<EVStatus> getEVStatus(@PathVariable String evName) {
        EV ev = evMap.get(evName);
        if (ev != null) {
            return ResponseEntity.ok(new EVStatus(
                    ev.getCharge(),
                    ev.getCurrentX(),
                    ev.getCurrentY()));
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Converts a raw path array to a list of PathNode objects.
     *
     * @param path The raw path array.
     * @return List of PathNode objects.
     */
    private List<PathNode> convertToPathNodes(long[] path) {
        List<PathNode> nodes = new ArrayList<>();
        for (int i = 0; i < path.length; i += 2) {
            nodes.add(new PathNode((int) path[i], (int) path[i + 1]));
        }
        return nodes;
    }

    /**
     * Retrieves the traffic signal states.
     *
     * @return ResponseEntity containing a list of traffic signal states.
     */
    @GetMapping("/traffic/signals")
    public ResponseEntity<List<TrafficSignalState>> getTrafficSignals() {
        return ResponseEntity.ok(
                TrafficManager.trafficLights.stream()
                        .map(node -> new TrafficSignalState(node.x, node.y, node.isGreen()))
                        .collect(Collectors.toList()));
    }

    /**
     * Changes the state of traffic signals.
     *
     * @return ResponseEntity indicating the result of the operation.
     */
    @PostMapping("/traffic/change")
    public ResponseEntity<Void> changeTrafficSignals() {
        TrafficManager.changeSignals();
        return ResponseEntity.ok().build();
    }
}

class EVCreateRequest {
    private String name;
    private int startX;
    private int startY;
    private int endX;
    private int endY;
    private int type;
    private int charge;
    private int chargingRate;
    private String vehicleType;

    // Getters
    public String getName() {
        return name;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getEndX() {
        return endX;
    }

    public int getEndY() {
        return endY;
    }

    public int getType() {
        return type;
    }

    public int getCharge() {
        return charge;
    }

    public int getChargingRate() {
        return chargingRate;
    }
}

class EVStatus {
    private int charge;
    private int currentX;
    private int currentY;

    public EVStatus(int charge, int currentX, int currentY) {
        this.charge = charge;
        this.currentX = currentX;
        this.currentY = currentY;
    }

    // Getters and setters
}

class TrafficSignalState {
    public int x;
    public int y;
    public boolean isGreen;
    // public int queueSize;

    public TrafficSignalState(int x, int y, boolean isGreen) {
        this.x = x;
        this.y = y;
        this.isGreen = isGreen;
        // this.queueSize = queueSize;
    }
}
