import java.util.ArrayList;

public class Main {

    private static final double MUTATION_CHANCE = 1.0;
    private static final double COMBINATION_CHANCE = 1.0;
    private static final int MAX_POPULATION_SIZE = 50;
    private static final double PARENT_PERCENTAGE = 0.4;
    private static final double DECREMENT = 1.0 / (MAX_POPULATION_SIZE + 1);
    private static final int MAX_GENERATIONS = 10000;

    public static void main(String[] args) {

        ArrayList<String> chessBoard = new ArrayList<>();

        for (int row = 1; row <= 8; row++) {
            for (char col = 'a'; col <= 'h'; col++) {
                String coordinate = col + Integer.toString(row);
                chessBoard.add(coordinate);
            }
        }

        ArrayList<ArrayList<String>> population = new ArrayList<>();
        for (int i = 0; i < MAX_POPULATION_SIZE; i++) {
            population.add(generateRandomQueens(chessBoard));
        }

        population = sortByAptitude(population);
        int generation = 0;

        while (countCollisions(population.get(0)) > 0 && generation < MAX_GENERATIONS) {
            ArrayList<ArrayList<String>> newPopulation = new ArrayList<>();
            ArrayList<ArrayList<String>> parents = new ArrayList<>();
            ArrayList<Double> parentChance = new ArrayList<>();

            for (int j = 0; j < MAX_POPULATION_SIZE * PARENT_PERCENTAGE; j++) {
                parents.add(population.get(j));
            }

            for (int j = 0; j < parents.size(); j++) {
                parentChance.add(1.0 - (j * DECREMENT));
            }

            for (int i = 0; i < MAX_POPULATION_SIZE; i++) {
                if (Math.random() < COMBINATION_CHANCE) {
                                        int parentIndex1 = -1;
                    int parentIndex2 = -1;

                    do {
                        for (int j = 0; j < parentChance.size(); j++) {
                            if (Math.random() < parentChance.get(j)) {
                                parentIndex1 = j;
                                break;
                            }
                        }
                        for (int j = 0; j < parentChance.size(); j++) {
                            if (Math.random() < parentChance.get(j)) {
                                if (j != parentIndex1) {
                                    parentIndex2 = j;
                                    break;
                                }
                            }
                        }
                    } while (parentIndex1 == -1 || parentIndex2 == -1);

                    ArrayList<String> parent1 = parents.get(parentIndex1);
                    ArrayList<String> parent2 = parents.get(parentIndex2);
                    ArrayList<String> child = combineParents(parent1, parent2);
                    newPopulation.add(child);
                    if (newPopulation.size() < MAX_POPULATION_SIZE) {
                        newPopulation.add(mutate(child, MUTATION_CHANCE));
                    }
                } else {
                    ArrayList<String> randomParent = parents.get((int) (Math.random() * parents.size()));
                    newPopulation.add(randomParent);
                }
            }
            population = sortByAptitude(newPopulation);
            System.out.println("Generation: " + generation + "\t | Collisions: " + countCollisions(population.get(0)));
            generation++;
        }

        System.out.println("\nFinal board:");
        generateASCIIBoard(population.get(0));
    }

    private static ArrayList<ArrayList<String>> sortByAptitude(ArrayList<ArrayList<String>> population) {
        for (int i = 0; i < population.size(); i++) {
            for (int j = i + 1; j < population.size(); j++) {
                if (countCollisions(population.get(i)) > countCollisions(population.get(j))) {
                    ArrayList<String> temp = population.get(i);
                    population.set(i, population.get(j));
                    population.set(j, temp);
                }
            }
        }
        return population;
    }

    private static ArrayList<String> generateRandomQueens(ArrayList<String> board) {
        ArrayList<String> queens = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            int rand = (int) (Math.random() * board.size());
            if(!queens.contains(board.get(rand))) {
                queens.add(board.get(rand));
            } else {
                i--;
            }
        }
        return queens;
    }

    private static String generateRandomCoordinate() {
        char col = (char) ((int) (Math.random() * 8) + 97);
        int row = (int) (Math.random() * 8) + 1;
        if(isInsideBoard(col, row)) {
            return col + Integer.toString(row);
        } else {
            return generateRandomCoordinate();
        }
    }

    private static boolean isInsideBoard(char col, int row) {
        return col >= 'a' && col <= 'h' && row >= 1 && row <= 8;
    }

    private static int scanDirection(String queen, ArrayList<String> queens, int xDirection, int yDirection) {
        int queensFound = 0;
        char col = queen.charAt(0);
        int row = Integer.parseInt(queen.substring(1));
        while(isInsideBoard(col, row)) {
            col += xDirection;
            row += yDirection;
            if(queens.contains(col + Integer.toString(row))) {
                queensFound++;
            }
        }
        return queensFound;
    }

    private static int countCollisions(ArrayList<String> queens) {
        int collisions = 0;
        for (String queenScan : queens) {
            for(int i = -1; i <= 1; i++) {
                for(int j = -1; j <= 1; j++) {
                    if(i == 0 && j == 0) {
                        continue;
                    }
                    collisions += scanDirection(queenScan, queens, i, j);
                }
            }
        }

        return collisions;
    }

    private static void generateASCIIBoard(ArrayList<String> queens) {
        System.out.println("\n \tA\t\tB\t\tC\t\tD\t\tE\t\tF\t\tG\t\tH");
        for (int row = 1; row <= 8; row++) {
            System.out.print(row);
            for (char col = 'a'; col <= 'h'; col++) {
                if(queens.contains(col + Integer.toString(row))) {
                    System.out.print("\t" + "Q" + "\t");
                } else {
                    System.out.print("\t. \t");
                }
            }
            System.out.println("\n");
        }
    }

    private static ArrayList<String> mutate(ArrayList<String> queens, double relocationChance) {
        if (Math.random() < relocationChance) {
            int rand = (int) (Math.random() * queens.size());
            String oldCoordinate = queens.get(rand);
            queens.remove(rand);
            String newCoordinate = generateRandomCoordinate();
            if(!queens.contains(newCoordinate) && !newCoordinate.equals(oldCoordinate)) {
                queens.add(newCoordinate);
            } else {
                queens.add(oldCoordinate);
                queens = mutate(queens, relocationChance);
            }
        }
        return queens;
    }

    private static ArrayList<String> combineParents(ArrayList<String> parent1, ArrayList<String> parent2) {
        ArrayList<String> child = new ArrayList<>();
        for (int i = 0; i < parent1.size(); i++) {
            if(Math.random() < 0.5) {
                if (!child.contains(parent1.get(i))) {
                    child.add(parent1.get(i));
                } else if (!child.contains(parent2.get(i))) {
                    child.add(parent2.get(i));
                }
            } else {
                if (!child.contains(parent2.get(i))) {
                    child.add(parent2.get(i));
                } else if (!child.contains(parent1.get(i))) {
                    child.add(parent1.get(i));
                }
            }
        }

        child = deduplicate(child);

        if (child.size() != 8) {
            child = fixSolution(child);
        }

        return child;
    }

    private static ArrayList<String> fixSolution(ArrayList<String> child) {
        while (child.size() != 8) {
            if (child.size() > 8) {
                child.remove((int) (Math.random() * child.size()));
            } else {
                child.add(generateRandomCoordinate());
            }
            child = deduplicate(child);
        }

        return child;
    }

    private static ArrayList<String> deduplicate(ArrayList<String> child) {
        for (int i = 0; i < child.size(); i++) {
            for (int j = i + 1; j < child.size(); j++) {
                if (child.get(i).equals(child.get(j))) {
                    child.remove(j);
                }
            }
        }
        return child;
    }
}