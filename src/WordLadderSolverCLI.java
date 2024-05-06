import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class WordLadderSolverCLI {
    private static Set<String> dictionary;

    static {
        // Load English dictionary into a set
        dictionary = loadDictionary("words.txt");
    }

    private static Set<String> loadDictionary(String filename) {
        Set<String> dictionary = new HashSet<>();
        try {
            Scanner scanner = new Scanner(new File(filename));
            while (scanner.hasNextLine()) {
                dictionary.add(scanner.nextLine().trim().toLowerCase());
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            System.err.println("File " + filename + " not found.");
            System.exit(1);
        }
        return dictionary;
    }

    public static boolean isValidWord(String word) {
        return dictionary.contains(word.toLowerCase());
    }

    public static List<String> findWordLadderUCS(String startWord, String endWord) {
        long startTime = System.currentTimeMillis(); // Start time

        Queue<List<String>> queue = new PriorityQueue<>(Comparator.comparingInt(List::size)); // Priority queue sorted by ladder length
        Set<String> visited = new HashSet<>();
        List<String> ladder = new ArrayList<>();
        ladder.add(startWord);
        queue.add(ladder);

        int visitedNodes = 0;

        while (!queue.isEmpty()) {
            List<String> currentLadder = queue.poll();
            String currentWord = currentLadder.get(currentLadder.size() - 1);

            if (currentWord.equals(endWord)) {
                long endTime = System.currentTimeMillis(); // End time
                long elapsedTime = endTime - startTime; // Elapsed time
                System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
                System.out.println("Number of nodes visited: " + visitedNodes);
                return currentLadder;
            }

            visited.add(currentWord);
            visitedNodes++;

            for (int i = 0; i < currentWord.length(); i++) {
                char[] chars = currentWord.toCharArray();
                for (char c = 'a'; c <= 'z'; c++) {
                    chars[i] = c;
                    String nextWord = new String(chars);
                    if (isValidWord(nextWord) && !visited.contains(nextWord)) {
                        List<String> nextLadder = new ArrayList<>(currentLadder);
                        nextLadder.add(nextWord);
                        queue.add(nextLadder);
                    }
                }
            }
        }

        // No word ladder found
        long endTime = System.currentTimeMillis(); // End time
        long elapsedTime = endTime - startTime; // Elapsed time
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        System.out.println("Number of nodes visited: " + visitedNodes);
        return new ArrayList<>();
    }

    public static List<String> findWordLadderGBFS(String startWord, String endWord) {
        long startTime = System.currentTimeMillis(); // Start time

        Queue<List<String>> queue = new PriorityQueue<>(Comparator.comparingInt(List::size)); // Priority queue sorted by ladder length
        Set<String> visited = new HashSet<>();
        List<String> ladder = new ArrayList<>();
        ladder.add(startWord);
        queue.add(ladder);

        int visitedNodes = 0;

        while (!queue.isEmpty()) {
            List<String> currentLadder = queue.poll();
            String currentWord = currentLadder.get(currentLadder.size() - 1);

            if (currentWord.equals(endWord)) {
                long endTime = System.currentTimeMillis(); // End time
                long elapsedTime = endTime - startTime; // Elapsed time
                System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
                System.out.println("Number of nodes visited: " + visitedNodes);
                return currentLadder;
            }

            visited.add(currentWord);
            visitedNodes++;

            List<String> neighbors = getNeighbors(currentWord);
            neighbors.sort(Comparator.comparingInt(w -> calculateHeuristic(w, endWord)));

            for (String neighbor : neighbors) {
                if (!visited.contains(neighbor)) {
                    List<String> nextLadder = new ArrayList<>(currentLadder);
                    nextLadder.add(neighbor);
                    queue.add(nextLadder);
                }
            }
        }

        // No word ladder found
        long endTime = System.currentTimeMillis(); // End time
        long elapsedTime = endTime - startTime; // Elapsed time
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        System.out.println("Number of nodes visited: " + visitedNodes);
        return new ArrayList<>();
    }

    public static List<String> findWordLadderAStar(String startWord, String endWord) {
        long startTime = System.currentTimeMillis(); // Start time
    
        PriorityQueue<SearchNode> queue = new PriorityQueue<>();
        Set<String> visited = new HashSet<>();
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> parents = new HashMap<>();
        queue.add(new SearchNode(startWord, 0, calculateHeuristic(startWord, endWord)));
        distances.put(startWord, 0); // Initialize startWord distance to 0
    
        int visitedNodes = 0;
    
        while (!queue.isEmpty()) {
            SearchNode currentNode = queue.poll();
            String currentWord = currentNode.word;
    
            // If currentWord has been visited before with shorter distance, skip it
            if (visited.contains(currentWord) && currentNode.distance > distances.get(currentWord)) {
                continue;
            }
    
            // Mark currentWord as visited
            visited.add(currentWord);
            visitedNodes++;
    
            // If currentWord is the endWord, reconstruct and return the path
            if (currentWord.equals(endWord)) {
                long endTime = System.currentTimeMillis(); // End time
                long elapsedTime = endTime - startTime; // Elapsed time
                System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
                System.out.println("Number of nodes visited: " + visitedNodes);
                return reconstructPath(parents, currentWord);
            }
    
            // Explore neighbors of currentWord
            List<String> neighbors = getNeighbors(currentWord);
            for (String neighbor : neighbors) {
                int distance = currentNode.distance + 1;
                int heuristic = calculateHeuristic(neighbor, endWord);
                int totalCost = distance + heuristic;
    
                // If neighbor has not been visited yet or we found a shorter path to neighbor
                if (!visited.contains(neighbor) || distance < distances.getOrDefault(neighbor, Integer.MAX_VALUE)) {
                    distances.put(neighbor, distance);
                    parents.put(neighbor, currentWord);
                    queue.add(new SearchNode(neighbor, distance, totalCost));
                }
            }
        }
    
        // No word ladder found
        long endTime = System.currentTimeMillis(); // End time
        long elapsedTime = endTime - startTime; // Elapsed time
        System.out.println("Elapsed time: " + elapsedTime + " milliseconds");
        System.out.println("Number of nodes visited: " + visitedNodes);
        return new ArrayList<>();
    }
    

    private static List<String> getNeighbors(String word) {
        List<String> neighbors = new ArrayList<>();
        for (int i = 0; i < word.length(); i++) {
            char[] chars = word.toCharArray();
            for (char c = 'a'; c <= 'z'; c++) {
                chars[i] = c;
                String nextWord = new String(chars);
                if (isValidWord(nextWord)) {
                    neighbors.add(nextWord);
                }
            }
        }
        return neighbors;
    }

    private static int calculateHeuristic(String word, String target) {
        int diff = 0;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) != target.charAt(i)) {
                diff++;
            }
        }
        return diff;
    }

    private static List<String> reconstructPath(Map<String, String> parents, String endWord) {
        List<String> path = new ArrayList<>();
        String currentWord = endWord;
        while (currentWord != null) {
            path.add(0, currentWord);
            currentWord = parents.getOrDefault(currentWord, null);
        }
        return path;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Input start word and end word
        System.out.print("Enter start word: ");
        String startWord = scanner.nextLine().trim().toLowerCase();

        System.out.print("Enter end word: ");
        String endWord = scanner.nextLine().trim().toLowerCase();

        // Validate input words
        if (!isValidWord(startWord) || !isValidWord(endWord)) {
            System.out.println("Invalid input words. Make sure they are in the dictionary.");
            return;
        }

        // Select algorithm
        System.out.println("Select algorithm:");
        System.out.println("1. UCS");
        System.out.println("2. Greedy Best First Search");
        System.out.println("3. A*");
        System.out.print("Enter choice: ");
        int choice = scanner.nextInt();

        List<String> ladder;
        switch (choice) {
            case 1:
                ladder = findWordLadderUCS(startWord, endWord);
                break;
            case 2:
                ladder = findWordLadderGBFS(startWord, endWord);
                break;
            case 3:
                ladder = findWordLadderAStar(startWord, endWord);
                break;
            default:
                System.out.println("Invalid choice.");
                scanner.close();
                return;
        }

        // Print the result
        if (!ladder.isEmpty()) {
            System.out.println("Word ladder found:");
            for (String word : ladder) {
                System.out.print(word + " ");
            }
            System.out.println("\nNumber of steps: " + (ladder.size() - 1));
        }

        scanner.close();
    }

    static class SearchNode implements Comparable<SearchNode> {
        String word;
        int distance;
        int totalCost;

        public SearchNode(String word, int distance, int totalCost) {
            this.word = word;
            this.distance = distance;
            this.totalCost = totalCost;
        }

        @Override
        public int compareTo(SearchNode other) {
            return Integer.compare(this.totalCost, other.totalCost);
        }
    }
}
