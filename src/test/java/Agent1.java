import com.codingame.game.Coordinate;

import java.io.InputStream;
import java.util.*;

public class Agent1 {
    static int h, w;
    static char[] grid;
    static Map<Character, List<Integer>> numbers = new HashMap<>();
    static Map<Character, List<Long>> paths = new HashMap<>();
    static List<Integer> validSolution = new ArrayList<>();
    static final int[][] directions = {{1, 0}, {0, 1}, {-1, 0}, {0, -1}};
    static long goalState;
    static boolean foundSolution = false;

    public static void main(String[] args) {
        InputStream inStream = ClassLoader.getSystemResourceAsStream("test9data.txt");
        if (inStream == null){return;}
        long startTime = 0;
        Map<Character,Coordinate> start_nodes = new HashMap<>();
        Map<Character,Coordinate> end_nodes = new HashMap<>();
        try (Scanner in = new Scanner(inStream)) {
            h = in.nextInt();
            w = in.nextInt();
            in.nextLine();

            grid = new char[h * w];
            int idx = 0;

            for (int i = 0; i < h; i++) {
                String row = in.nextLine();
                startTime = System.nanoTime();
                for (int j = 0; j < w; j++) {
                    char c = row.charAt(j);
                    grid[idx] = c;
                    if (c != '.') {
                        numbers.computeIfAbsent(c, k -> new ArrayList<>()).add(idx);
                        if (start_nodes.containsKey(c)){
                            end_nodes.put(c , new Coordinate(i,j,c));
                        }else{
                            start_nodes.put(c , new Coordinate(i,j,c));
                        }
                    }
                    idx++;
                }
            }
        }

        List<Character> sortedKeys = new ArrayList<>(numbers.keySet());
        sortedKeys.sort(Comparator.comparingInt(k -> distance(numbers.get(k).get(0), numbers.get(k).get(1))));

        for (char number : sortedKeys) {
            List<Long> list = new ArrayList<>();
            getPaths(numbers.get(number).get(0), numbers.get(number).get(1), list);
            list.sort(Comparator.comparingInt(Long::bitCount));
            paths.put(number, list);
        }

        goalState = (1L << (h * w)) - 1;
        findSolution(sortedKeys, 0, 0, new ArrayList<>());

        if (validSolution.isEmpty()) {
            throw new RuntimeException("No valid full-covering path combination found");
        }

        for (int i = 0; i < sortedKeys.size(); i++){
            // Get start for char
            char c = sortedKeys.get(i);
            Coordinate start = start_nodes.get(c);
            Coordinate end = end_nodes.get(c);

            // Reconstruct
            boolean[][] visited = new boolean[h][w];
            Deque<int[]> path = new ArrayDeque<>();
            path.add(new int[]{start.getY(),start.getX()});
            visited[start.getY()][start.getX()] = true;
            reconstructDFS(start.getY(), start.getX(), end.getY(), end.getX(), paths.get(c).get(validSolution.get(i)), visited, path, Long.bitCount(paths.get(c).get(validSolution.get(i)))-1);

            // Print
            while (path.size() > 1) {
                int[] pos1 = path.pollFirst();
                int[] pos2 = path.peekFirst();
                System.out.println(pos1[1] + " " + pos1[0] + " " + pos2[1] + " " + pos2[0] + " " + c);

            }
        }
        long endTime = System.nanoTime();
        System.err.println("Duration:" + (endTime - startTime) / 1000000.0);

    }

    // Reconstructs a valid path from start to end within a bitmask path
    static boolean reconstructDFS(int y, int x, int endY, int endX, long bitmask, boolean[][] visited, Deque<int[]> path, int size) {
        if (y == endY && x == endX){
            return size == 0;
        }
        visited[y][x] = true;

        for (int[] dir : directions) {
            int ny = y + dir[0];
            int nx = x + dir[1];
            if (ny >= 0 && ny < h && nx >= 0 && nx < w && !visited[ny][nx]) {
                int idx = ny * w + nx;
                if (((bitmask >> idx) & 1) == 1) {
                    path.push(new int[]{ny, nx});
                    if (reconstructDFS(ny, nx, endY, endX, bitmask, visited, path, size - 1)) {
                        return true;
                    }
                    path.pop();
                }
            }
        }
        visited[y][x] = false;
        return false;
    }


    static void getPaths(int start, int end, List<Long> pathList) {
        dfs(start, end, 1L << start, 1L << start, pathList);
    }

    static boolean dfs(int index, int end, long path, long visited, List<Long> pathList) {
        int y = index / w, x = index % w;
        for (int[] dir : directions) {
            int ny = y + dir[0], nx = x + dir[1];
            if (ny >= 0 && ny < h && nx >= 0 && nx < w) {
                int newIdx = ny * w + nx;
                if (((visited >> newIdx) & 1L) == 1L) continue;
                if (newIdx == end) {
                    pathList.add(path | (1L << end));
                    if (pathList.size() == 5000){
                        return true;
                    }
                } else if (grid[newIdx] == '.') {
                    boolean res = dfs(newIdx, end, path | (1L << newIdx), visited | (1L << newIdx), pathList);
                    if (res){return true;}
                }
            }
        }
        return false;
    }

    static void findSolution(List<Character> keys, long currentMask, int depth, List<Integer> pathIndex) {
        if (foundSolution){return;}
        if (depth == keys.size()) {
            if (currentMask == goalState) {
                validSolution = new ArrayList<>(pathIndex);
                foundSolution = true;
            }
            return;
        }

        char key = keys.get(depth);
        for (int i = 0; i < paths.get(key).size(); i++) {
            long path = paths.get(key).get(i);
            if ((path & currentMask) == 0) {
                pathIndex.add(i);
                findSolution(keys, currentMask | path, depth + 1, pathIndex);
                pathIndex.remove(pathIndex.size() - 1);
            }
        }
    }

    static int distance(int a, int b) {
        int y1 = a / w, x1 = a % w;
        int y2 = b / w, x2 = b % w;
        return Math.abs(y1 - y2) + Math.abs(x1 - x2);
    }
}
