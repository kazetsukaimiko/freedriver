package io.freedriver.jsonlink;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

class Solution {
    /*
    private static final List<String> ROWS = Arrays.asList("A", "B", "C", "D", "E", "F");
    private static final List<Integer> COLUMNS = IntStream.range(1,6).boxed().collect(Collectors.toList());

    public static void play(String playerOneShips, List<String> playerTwoGuesses) {
        Set<Coordinate> ships = getAllShips(playerOneShips);
        System.out.println("Player One entered " + ships.size() + " ships.");

        Set<Coordinate> guesses = new HashSet<>();

        for (int guessesMade = 0; guessesMade < 10 && guessesMade < playerTwoGuesses.size(); guessesMade++) {
            System.out.println("Player Two, you have " + (10 - guessesMade) + " guesses left. Board status: ");
            printBoardStatus(guesses);

            Coordinate guess = Coordinate.newGuess(playerTwoGuesses.get(guessesMade));
            if (ships.contains(guess)) {
                System.out.println("That was a hit!");
                guess.setShipPresent(true);
            } else {
                System.out.println("That was a miss!");
            }
            guesses.add(guess);
            if (guesses.containsAll(ships)) {
                System.out.println("You win!");
                return;
            }
        }

        System.out.println("You lose!");
    }

    private static Set<Coordinate> getAllShips(String playerOneShips) {
        List<Coordinate> coordinates = Stream.of(playerOneShips.split("\\s+"))
                .map(Coordinate::newShip)
                .collect(Collectors.toList());

        int shipCount = coordinates.size() / 2;
        System.out.println("Player One entered " + shipCount + " ships.");

        return IntStream.range(0, coordinates.size()/2)
                .map(idx -> idx * 2)
                .boxed()
                .flatMap(top -> allCoordinatesForShip(coordinates.get(top), coordinates.get(top+1)))
                .collect(Collectors.toSet());
    }

    private static Stream<Coordinate> allCoordinatesForShip(Coordinate one, Coordinate two) {
        if (one.getCol() != two.getCol()) {
            if (one.getCol() > two.getCol()) {

            }
        }
    }

    private static void printBoardStatus(Set<Coordinate> guesses) {
        for (String row : ROWS) {
            for (Integer col : COLUMNS) {
                Optional<Coordinate> search = guesses.stream()
                        .filter(coord -> coord.match(row, col))
                        .findFirst();
                if (search.isPresent()) {
                    Coordinate match = search.get();
                    if (match.isAttacked()) {
                        if (match.isShipPresent()) {
                            System.out.print("X");
                        } else {
                            System.out.print("O");
                        }
                    }
                } else {
                    System.out.print(".");
                }
            }
            System.out.println("");
        }
    }

    private static final class Coordinate {
        private final int row;
        private final int col;
        private boolean shipPresent;
        private boolean attacked;

        public static Coordinate newShip(String coords) {
            return new Coordinate(coords.substring(0,1), Integer.parseInt(coords.substring(1,2)), true, false);
        }

        public static Coordinate newGuess(String coords) {
            return new Coordinate(coords.substring(0, 1), Integer.parseInt(coords.substring(1,2)), false, true);
        }

        private Coordinate(String row, int col, boolean shipPresent, boolean attacked) {
            this.row = indexOf(row);
            this.col = col;
            this.shipPresent = shipPresent;
            this.attacked = attacked;
        }

        private int indexOf(String row) {
        }

        public int getRow() {
            return row;
        }

        public int getCol() {
            return col;
        }

        public boolean isShipPresent() {
            return shipPresent;
        }

        public void setShipPresent(boolean shipPresent) {
            this.shipPresent = shipPresent;
        }

        public boolean isAttacked() {
            return attacked;
        }

        public void setAttacked(boolean attacked) {
            this.attacked = attacked;
        }

        public boolean match(String row, int col) {
            return Objects.equals(this.row, row) && this.col == col;
        }

        @Override
        public String toString() {
            return row + "" +  col;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return col == that.col && Objects.equals(row, that.row);
        }

        @Override
        public int hashCode() {
            return Objects.hash(row, col);
        }
    }
*/
}
