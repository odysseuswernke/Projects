/* Programmed by: Odysseus Wernke
   Date: March 2026
   Project Name: Connect Four (Game)

   Description: In Connect Four, two players ("yellow" and "red") will take turns in an attempt to connect four of their respectively colored coordinates in either a straight line or straight diagonal.
   The first player to connect four of their occupied coordinates wins. If neither player manages to connect four of their own coordinates in the 6 (rows) x 7 (columns) grid, neither player wins.
*/

#include <stdio.h>
#include <string.h>
#include <stdbool.h>

#define BOARD_WIDTH 7
#define BOARD_HEIGHT 6
#define OPEN_SPACE 0
#define YELLOW -1
#define RED 1

// Constants to print in bold red text, bold yellow text, and to reset text style
#define START_RED "\033[1m\033[31m"
#define START_YELLOW "\033[1m\033[33m"
#define RESET_COLOR "\033[0m"

/*   To print text in yellow color, for example, use code with the following syntax:
printf(START_YELLOW "This text will appear in yellow.\n" RESET_COLOR);   */

// Convention for board: red spaces occupied by 1, yellow spaces occupied by -1.

//Sets all indicies of the global board to 0.
void reset_board(int board[]) {
  for (int row = 0; row < BOARD_HEIGHT; row++) {
    for (int column = 0; column < BOARD_WIDTH; column++) {
      int pos = (row * BOARD_WIDTH) + column;
      board[pos] = 0;
    }
  }
}

//Changes player turn.
void change_turn(int* current_turn_pointer) {
  if (*current_turn_pointer == YELLOW) {
    *current_turn_pointer = RED;
  }
  else {
    *current_turn_pointer = YELLOW;
  }
}

//Prints turn status.
void print_turn_status(int current_turn) {
  if (current_turn == YELLOW) {
    printf("It's \"yellow\" player's turn.\n");
  }
  else {
    printf("It's \"red\" player's turn.\n");
  }
}

//Gets user input to fill an incidice of the board with current player's token (1 for red, -1 for yellow).
void get_user_move(int board[], int current_turn) {

  // Create an array to hold user input
  char input[100];
  bool exit_signal = false;

  while (exit_signal == false) {

    do {
      printf("Pick a column (A-G) in which to drop your piece: ");
      fgets(input, 100, stdin);
    } while (!((input[0] >= 'a' && input[0] <= 'g' && input[1] == '\n') || 
               (input[0] >= 'A' && input[0] <= 'G' && input[1] == '\n')));

    // Normalize to lowercase and map to column 0–6
    char col_char = (input[0] >= 'A' && input[0] <= 'G') ? input[0] + 32 : input[0];
    int col = col_char - 'a';

    // Bottom index of that column
    int start = (BOARD_HEIGHT - 1) * BOARD_WIDTH + col;

    //Checks column for open indice
    for (int pos = start; pos >= col; pos -= BOARD_WIDTH) {
      if (board[pos] == 0) {
        board[pos] = current_turn;
        exit_signal = true;
        break;
      }
    }
  }
}

//This prints the current board with Rs representing red tokens and Ys representing yellow tokens.
void print_board(int board[]) {
  printf(" A B C D E F G\n");
  for (int row = 0; row < BOARD_HEIGHT; row++) {
    for (int column = 0; column < BOARD_WIDTH; column++) {
      int pos = (row * BOARD_WIDTH) + column;
      if (board[pos] == YELLOW) { //Checks if global board's pos indice is non-empty
        printf("|" START_YELLOW "Y" RESET_COLOR);
      }
      else if (board[pos] == RED) {
        printf("|" START_RED "R" RESET_COLOR);
      }
      else {
        printf("| ");
      }
    }
    printf("|\n");
  }
}

//Checks win statuses of rows, columns, and diagonals.
void check_win_status(int board[], int* win_status_pointer) {
  //Checks row win statuses by checking four consecutive indices, shifting right each check and down after finishing row check.
  for (int row = 0; row < BOARD_HEIGHT; row++) {
    for (int column = 0; column <= (BOARD_WIDTH - 4); column++) {
      int pos = (row * BOARD_WIDTH) + column;
      if (board[pos] != 0 &&
          board[(pos + 1)] == board[pos] &&
          board[(pos + 2)] == board[pos] &&
          board[(pos + 3)] == board[pos]) {
        *win_status_pointer = board[pos]; 
        return;
      }
    }
  }
  //Checks column win statuses. Works by checking one column at a time, left to right; checks columns shifted a row below after.
  for (int row = 0; row <= (BOARD_HEIGHT - 4); row++) {
    for (int column = 0; column < BOARD_WIDTH; column++) {
      int pos = (row * BOARD_WIDTH) + column;
      if (board[pos] != 0 &&
          board[(pos + BOARD_WIDTH)] == board[pos] &&
          board[(pos + (BOARD_WIDTH * 2))] == board[pos] &&
          board[(pos + (BOARD_WIDTH * 3))] == board[pos]) {
        *win_status_pointer = board[pos];
        return;
      }
    }
  }
  
  //Sweeps left to right, top to bottom checking diagonals of shape '\'
  for (int i = 0; i < (BOARD_WIDTH * BOARD_HEIGHT); i++) {
    int l_to_r_down_diagonal_offset = (BOARD_WIDTH + 1);
    if ((i + (l_to_r_down_diagonal_offset * 3) < (BOARD_WIDTH * BOARD_HEIGHT)) && ((i % BOARD_WIDTH) <= (BOARD_WIDTH - 4))) {
        if ((board[i] != 0) &&
          (board[i] == board[i + l_to_r_down_diagonal_offset]) &&
          (board[i] == board[i + l_to_r_down_diagonal_offset * 2]) &&
          (board[i] == board[i + l_to_r_down_diagonal_offset * 3])) {
            *win_status_pointer = board[i];
            return;
        }
    }
  }

  //Sweeps left to right, top to bottom checking diagonals of shape '/'
  for (int i = 0; i < (BOARD_WIDTH * BOARD_HEIGHT); i++) {
    int r_to_l_down_diagonal_offset = (BOARD_WIDTH - 1);
    if ((i + (r_to_l_down_diagonal_offset * 3) < (BOARD_WIDTH * BOARD_HEIGHT)) && ((i % BOARD_WIDTH ) >= (BOARD_WIDTH - 4))) {
      if ((board[i] != 0) &&
          (board[i] == board[i + r_to_l_down_diagonal_offset]) &&
          (board[i] == board[i + r_to_l_down_diagonal_offset * 2]) &&
          (board[i] == board[i + r_to_l_down_diagonal_offset * 3])) {
        *win_status_pointer = board[i];
        return;
      }
    }
  }
}


int main() {
  int current_turn = YELLOW;
  int win_status = 0;
  int board[BOARD_WIDTH * BOARD_HEIGHT];
  reset_board(board);

  print_board(board);
  for (int turn_count = 0; turn_count < (BOARD_WIDTH * BOARD_HEIGHT); turn_count++) {
    print_turn_status(current_turn);
    get_user_move(board, current_turn);
    print_board(board);
    change_turn(&current_turn);
    check_win_status(board, &win_status);
    if (win_status == RED) {
      printf("Red wins.\n");
      break;
    }
    else if (win_status == YELLOW) {
      printf("Yellow wins.\n");
      break;
    }
    else if (turn_count == (BOARD_WIDTH * BOARD_HEIGHT - 1)) {
      printf("Nobody won.\n");
      break;
    }
  }
  return 0;
}