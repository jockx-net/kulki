package net.jockx.kulki.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

public class PathFinder {

    PathFinder() {
    }

    public LinkedList<Cell> findShortestPathToCell(Cell from, Cell to) {
        if (from == null || to == null) {
            return new LinkedList<>();
        }

        Queue<Cell> queue = new LinkedList<>();
        Map<Cell, Cell> parent = new HashMap<>();
        Set<Cell> visited = new HashSet<>();

        queue.add(from);
        visited.add(from);
        parent.put(from, null);

        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            for (Cell neighbour : getSimpleNeighbours(current)) {
                if (neighbour == null || visited.contains(neighbour)) {
                    continue;
                }

                if (!neighbour.equals(to) && !neighbour.isFree()) {
                    continue;
                }

                visited.add(neighbour);
                parent.put(neighbour, current);

                if (neighbour.equals(to)) {
                    LinkedList<Cell> path = new LinkedList<>();
                    Cell step = to;
                    while (step != null) {
                        path.add(0, step);
                        step = parent.get(step);
                    }
                    return path;
                }

                queue.add(neighbour);
            }
        }

        return new LinkedList<>();
    }

    private Cell[] getSimpleNeighbours(Cell cell) {
        return new Cell[]{cell.up, cell.right, cell.down, cell.left};
    }
}
