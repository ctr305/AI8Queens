import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        ArrayList<String> chessBoard = new ArrayList<>();
        int collisions;

        for (int row = 1; row <= 8; row++) {
            for (char col = 'a'; col <= 'h'; col++) {
                String coordinate = col + Integer.toString(row);
                chessBoard.add(coordinate);
            }
        }

        ArrayList<String> queens = generateRandomQueens(chessBoard);

        collisions = countCollisions(queens);

        System.out.println("Initial board:");
        generateASCIIBoard(queens);

        System.out.println("\nCollisions: " + collisions);

        while(collisions > 0) {
            ArrayList<String> newQueens = relocateRandomQueen(queens);
            if(countCollisions(newQueens) < collisions) {
                queens = newQueens;
                collisions = countCollisions(queens);
                System.out.println("Collisions: " + collisions);
            }
        }

        generateASCIIBoard(queens);
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

    private static ArrayList<String> relocateRandomQueen(ArrayList<String> queens) {
        int rand = (int) (Math.random() * queens.size());
        String oldCoordinate = queens.get(rand);
        queens.remove(rand);
        String newCoordinate = generateRandomCoordinate();
        if(!queens.contains(newCoordinate) && !newCoordinate.equals(oldCoordinate)) {
            queens.add(newCoordinate);
            return queens;
        } else {
            queens.add(oldCoordinate);
            return relocateRandomQueen(queens);
        }
    }
}