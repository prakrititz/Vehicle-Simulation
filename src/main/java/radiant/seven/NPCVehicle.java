package radiant.seven;

import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class NPCVehicle extends EV {
    private Random random = new Random();

    public NPCVehicle(int x, int y) {
        super(x, y, 3, 100, 0); // Type 3 for NPC, full charge, no charging needed
        this.setName("NPC_" + System.currentTimeMillis());
    }
    public NPCVehicle(int x, int y, String vehicleType) {
        super(x, y, 3, 100, 0, vehicleType); // Type 3 for NPC, full charge, no charging needed
        this.setName("NPC_" + System.currentTimeMillis());
    }

    public void pickRandomDestination() {
        List<Node> reachableNodes = new ArrayList<>(GameMap.getInstance().getRoadNetwork().values());
        if (!reachableNodes.isEmpty()) {
            Node randomDest = reachableNodes.get(random.nextInt(reachableNodes.size()));
            this.setEndLocation(randomDest.x, randomDest.y);
        }
    }

    @Override
    public void changeEnd() {
        if (this.getCurrentX() == getEndX() && this.getCurrentY() == getEndY()) {
            GameMap gameMap = GameMap.getInstance();
            Random random = new Random();
            Node endNode;
            int endX, endY;

            // Keep trying until we find a valid road position
            do {
                endX = 1 + random.nextInt(35);
                endY = 1 + random.nextInt(35);
                endNode = gameMap.getRoadNode(endX, endY);
            } while (endNode == null);

            this.setEndLocation(endX, endY);

            // Calculate new path to valid destination
            long[] pathArray = EVController.pathfinder.findPath(getCurrentX(), getCurrentY(), endX, endY);
            List<PathNode> newPath = convertToPathNodes(pathArray);
            this.setPath(newPath);
            this.currentPathIndex = 0;
        }
    }

}
