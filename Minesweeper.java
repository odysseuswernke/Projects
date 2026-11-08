/* Programmed by: Odysseus Wernke
   Date: 25 October 2026
   Project Name: Minesweeper (Game)

   Description: In Minesweeper, a player will choose to create a coordinate grid of dimension m columns by n rows. They will then choose to have x mines placed, with each mine occupying
   a random coordinate of the grid. The player's goal is to reveal all non-mine-containing coordinates. If they are successful, they win; if unsuccessful, they lose.

   Rules: 1) The player's first valid coordinate reveal choice is guaranteed to be non-mine-containing.
          2) Each non-mine-containing revealed coordinate will indicate the number of adjacent mine-containing coordinates.
          3) The player will choose a valid coordinate to reveal each turn.
          4) If the player has revealed all non-mine-containing coordinates, they win; if not, they lose.
*/

import java.util.Random;
import java.util.Scanner;

public class Minesweeper {
    final static int MINE = -1;

    public static class Matrix {
        private int rows;
        private int columns;
        //This matrix contains the raw data
        private int[][] matrix;
        //This matrix contains: 1) true values for revealed indices, 2) false values for non-revealed indices
        private boolean[][] matrix_revealed;
        private int mine_count;
        private boolean game_over;
        private boolean first_move;

        Matrix(int rows, int columns, int mine_count) {
            // Validate rows and columns
            if (rows <= 0 || columns <= 0) {
                throw new IllegalArgumentException("Rows and columns must be greater than 0.");
            }
            
            // Validate mine_count: Cannot be negative or exceed total cells
            if (mine_count < 0 || mine_count > rows * columns) {
                throw new IllegalArgumentException("Invalid mine count. Must be between 0 and " + (rows * columns) + ".");
            }
            else if (mine_count == rows * columns) {
                this.game_over = true;
                System.out.println("Because a mine occupies each of the " + (rows * columns) + " coordinates of the minefield, there are no safe coordinates to reveal. Game over.");
                System.exit(0);
            }

            this.rows = rows;
            this.columns = columns;
            this.mine_count = mine_count;
            this.game_over = false;
            this.first_move = true;
            this.matrix = new int[rows][columns];
            this.matrix_revealed = new boolean[rows][columns];
            init_matrix_revealed();
        }

        //This method assumes this.mine_count is valid and does the following: 1) places mines at random indicies, 2) for each non-mine-containing indice, assigns number of adjacent mine-containing-indices
        private void init_minesweeper_matrix(int safe_row, int safe_column) {
            Random random_int;
            int random_row;
            int random_column;
            // 1) Place mines at random indices
            for (int i = 0; i < mine_count; i++) {
                //Repeat random indice generation process until a non-mine-containing indice is found
                do {
                    //Generate a random row value
                    random_int = new Random();
                    random_row = random_int.nextInt(this.rows);
                    //Generate a random column value
                    random_int = new Random();
                    random_column = random_int.nextInt(this.columns);
                } while ((this.matrix[random_row][random_column] == MINE) || (random_row == safe_row && random_column == safe_column));

                //Place mine
                this.matrix[random_row][random_column] = MINE;
            }

            int adj_mine_count;
            // 2) For each non-mine-containing indice, assigns number of adjacent mine-containing-indices
            for (int row_curr = 0; row_curr < this.rows; row_curr++) {
                for (int column_curr = 0; column_curr < this.columns; column_curr++) {
                    //Reset adjacent mine count sum
                    adj_mine_count = 0;

                    //If a given indice contains a mine, skip that indice
                    if (this.matrix[row_curr][column_curr] == MINE) {
                        continue;
                    }
                    //Else, count all surrounding mine-containing indices by checking each for a mine
                    for (int row_adj_curr = row_curr - 1; row_adj_curr <= row_curr + 1; row_adj_curr++) {
                        for (int column_adj_curr = column_curr - 1; column_adj_curr <= column_curr + 1; column_adj_curr++) {
                            //If the current adjacent indice is out-of-bounds, skip it
                            if (row_adj_curr < 0 || row_adj_curr >= this.rows || column_adj_curr < 0 || column_adj_curr >= this.columns) {
                                continue;
                            }
                            //Else, we know that the current adjacent indice is in-bounds; check if it contains a mine
                            if (this.matrix[row_adj_curr][column_adj_curr] == MINE) {
                                adj_mine_count++;
                            }
                        }
                    }
                    
                    //Set current indice value to adjacent mine count
                    this.matrix[row_curr][column_curr] = adj_mine_count;
                }
            }
        }

        //Initializes all matrix_revealed indices to be false
        private void init_matrix_revealed() {
            for (int row_curr = 0; row_curr < this.rows; row_curr++) {
                for (int column_curr = 0; column_curr < this.columns; column_curr++) {
                    matrix_revealed[row_curr][column_curr] = false;
                }
            }
        }

        //Reveal indices of the matrix; if an adjacent indice contains value 0 (meaning adjacent indice is NOT adjacent to any mine-containing indices), reveal it too
        public void reveal_indices(int row_chosen, int column_chosen) {
            //If choice is out-of-bounds, stop the program
            if (row_chosen < 0 || row_chosen >= this.rows || column_chosen < 0 || column_chosen >= this.columns) {
                throw new IllegalArgumentException("Invalid row or column choice. Choice must be in-bounds.");
            }

            // IF THIS IS THE FIRST MOVE: Generate the board around this safe coordinate
            if (this.first_move) {
                init_minesweeper_matrix(row_chosen, column_chosen);
                this.first_move = false; // Disable first move safety
            }

            //Else, choice is valid; now, check if chosen indice contains mine
            if (this.matrix[row_chosen][column_chosen] == MINE) {
                System.out.println("Position " + "R" + row_chosen + "C" + column_chosen + " contained a mine. Game over!\n");
                //Exit program if user chose indice with mine
                this.game_over = true;
                System.exit(0);
            }
            //Chosen indice DOES NOT contain mine; do the following:
            this.matrix_revealed[row_chosen][column_chosen] = true;
            //If chosen indice is NOT adjacent to any mines
            if (this.matrix[row_chosen][column_chosen] == 0) {
                //Reveal all of the adjacent indices
                for (int row_adj_curr = row_chosen - 1; row_adj_curr <= row_chosen + 1; row_adj_curr++) {
                    for (int column_adj_curr = column_chosen - 1; column_adj_curr <= column_chosen + 1; column_adj_curr++) {
                        //If the current adjacent indice is out-of-bounds, skip it
                        if (row_adj_curr < 0 || row_adj_curr >= this.rows || column_adj_curr < 0 || column_adj_curr >= this.columns) {
                            continue;
                        }
                        //Else, we know that the current adjacent indice is in-bounds; if the C.A.I. has not yet been revealed, reveal it and recursively cascade from it
                        if (matrix_revealed[row_adj_curr][column_adj_curr] == false) {

                            this.matrix_revealed[row_adj_curr][column_adj_curr] = true;
                            if (matrix[row_adj_curr][column_adj_curr] == 0) {
                                reveal_indices(row_adj_curr, column_adj_curr);
                            }
                        }
                    }
                }
            }
        }

        //Print the matrix (with indices being in right-justified formatting)
        public void print_matrix() {
            // Print column headers with fixed spacing
            System.out.print("      ");
            for (int column_curr = 0; column_curr < this.columns; column_curr++) {
                System.out.printf("%3s ", "C" + column_curr);
            }
            System.out.print("\n\n");

            // Print the indices
            for (int row_curr = 0; row_curr < this.rows; row_curr++) {
                // Print row headers with aligned padding
                System.out.printf("R%-2d:  ", row_curr);
                
                // Print each cell right-justified (width of 3 spaces)
                for (int column_curr = 0; column_curr < this.columns; column_curr++) {
                    if (this.matrix_revealed[row_curr][column_curr] == false) {
                        System.out.printf("%3s ", "*");
                        continue;
                    }
                    // %3d automatically handles alignment for both single digits and negative mines (-1)
                    System.out.printf("%3d ", this.matrix[row_curr][column_curr]);
                }
                System.out.println();
            }
        }

        //Returns true if game is over and false if not
        public boolean game_over_status() {
            int revealed_indices_count = 0;

            for (int row_curr = 0; row_curr < this.rows; row_curr++) {
                for (int column_curr = 0; column_curr < this.columns; column_curr++) {
                    if (matrix_revealed[row_curr][column_curr] == true) {
                        revealed_indices_count++;
                    }
                }
            }
            //If all non-mine-containing indices (# of indices of the matrix - # of mines) have been revealed, the game is over and the user has won
            if (revealed_indices_count == ((this.rows * this.columns) - this.mine_count)) {
                System.out.println("\nYou have successfully revealed all non-mine-containing coordinates. Well done!");
                return true;
            }
            //Else, game is NOT over
            return false;
        }
    }

    void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int mine_count_choice;
        int row_choice;
        int column_choice;

        System.out.println("\nMinesweeper:\nRules: Reveal all non-mine-containing coordinates to win. Coordinates with a value of 0 are not adjacent to any mine; however, coordinates with a value of N (where N != 0) are adjacent to N mine-containing coordinates.\n");
        System.out.println("Choose # of rows for the Minesweeper grid: ");
        row_choice = scanner.nextInt();
        System.out.println("Choose # of columns for the Minesweeper grid: ");
        column_choice = scanner.nextInt();
        System.out.println("Choose # of mines for the Minesweeper grid: ");
        mine_count_choice = scanner.nextInt();

        Matrix minesweeper = new Matrix(row_choice, column_choice, mine_count_choice);
        minesweeper.print_matrix();

        while (minesweeper.game_over_status() == false) {
            boolean valid_input = false;

            // Inner loop to continuously prompt until valid coordinates are provided
            while (!valid_input) {
                try {
                    System.out.println("\nChoose a coordinate to reveal. Enter both a row and a column.");
                    System.out.print("Choose a row: ");
                    row_choice = scanner.nextInt();
                    System.out.print("Choose a column: ");
                    column_choice = scanner.nextInt();

                    System.out.println();
                    // This will throw IllegalArgumentException if row/col are out of bounds
                    minesweeper.reveal_indices(row_choice, column_choice);
                    
                    // If no exception was thrown, the input was valid
                    valid_input = true; 

                } catch (IllegalArgumentException e) {
                    // Catch out-of-bounds errors and prompt again
                    System.out.println("Error: " + e.getMessage() + " Please try again.");
                } catch (java.util.InputMismatchException e) {
                    // Catch non-integer inputs so the scanner doesn't loop infinitely
                    System.out.println("\nError: Please enter valid integers for row and column.");
                    scanner.next(); // Clear the invalid input token from the scanner buffer
                }
            }

            minesweeper.print_matrix();
        }
    }
}