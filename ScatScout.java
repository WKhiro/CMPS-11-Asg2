/**
 * Scat Scout (Minesweeper but with dung!)
 */
import static java.lang.System.*;
import java.util.*;

class ScatScout {
  static final int boardSize = 10;
  static final Random rand = new Random(); 
  static final boolean SCAT = true;
  static final boolean CLEAR = false;
  public static void main(String[] args) {
    Scanner input = new Scanner(in);
    boolean[][] board = new boolean[boardSize][boardSize]; // scat or no scat
    boolean[][] exposed = new boolean[boardSize][boardSize]; // showing or hidden
    int[][] counts = new int[boardSize][boardSize]; // number of neighbors with scat
    if (args.length > 0) {
      // expect the only argument if any to be a number used to seed the random number generator for testing
      rand.setSeed(Integer.parseInt(args[0]));
    }
    int seed = rand.nextInt(50); //generate and set a seed to return the same random values
    boardCreation(board, exposed, counts, seed); //creates the board
    out.println();
    out.print("Enter two integers(row and column):"); //only prints this once to prevent reprint if the game ends
    assignScatNumbers(counts,board); //assigns neighboring scat numbers to counts array
    integersOnly(input); //asks for more input if input isn't an integer
    exposeStep(input, input.nextInt(), input.nextInt(), scatCounter(board), exposedCellCounter(exposed), seed,
               board, exposed, counts); //expose user inputs on board
  }
  
  /**
   * Reads the user input. If the inputs aren't integers, continue to ask for more input. If the integers are outside
   * the array's boundaries, continue asking for more input.
   * @param input - scanner used to detect input
   */
  static void integersOnly(Scanner input){
    while(!input.hasNextInt() || input.nextInt()>9 || input.nextInt()<0){ 
      out.println("Please enter two numbers between 0 and 9");
      input.next(); //ask for more input
    }
  }
  
  /**
   * Randomly places scat on the board based on seed. Then calls the displayBoard method to print the
   * board based on the new SCAT values.
   * @param board - the array indicating where the scat is located
   * @param exposed - the array indicating which locations have been exposed
   * @param counts - the array of counts of neighbors with scat including the location itself if it contains scat
   * @param seed - a seed for the random number generator used to scatter the scat
   */
  static void boardCreation(boolean[][] board, boolean[][] exposed, int[][] counts, int seed){
    Random rand = new Random(seed); //seed generation
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if(rand.nextInt(7) == 0){ //rolls a 6-sided die to determine if the cell should have scat or not
          board[i][j] = SCAT;
        } else {
          board[i][j] = CLEAR;
        }
      }
    }
    displayBoard(board, exposed, counts); //display the board itself
  }
  
  /**
   * Makes the physical ScatScout board along with labels along the rows and columns. Reveals cells based on if they
   * have been exposed or not from the exposed array.
   * @param board - the array indicating where the scat is located
   * @param exposed - the array indicating which locations have been exposed
   * @param counts - the array of counts of neighbors with scat including the location itself if it contains scat
   */
  static void displayBoard(boolean[][] board, boolean[][] exposed, int[][] counts){
    out.print(" "); //space before digits at the top to center them
    for(int i = 0; i < board.length; i++) {
      if(i == 0){ //if at the top of the board
        for(int topDig = 0; topDig < board[i].length; topDig++){ //print the digits out at the top
          out.print(topDig);
        }
        out.println(); //start next line for the board
      }
      out.print(i); //print the current row number before printing the row out
      for(int j = 0; j < board[i].length; j++){
        if(board[i][j] == SCAT && exposed[i][j] == true){ //if it's exposed and a scat
          out.print('*');
        }else if(board[i][j] == CLEAR && exposed[i][j] == true){ //if it's exposed and clean
          out.print(counts[i][j]); //display the number of neighboring scats
        }else{
          out.print('.'); //default hidden cells
        }
      }
      out.print(i); //print the current row number after printing out the row out
      out.println(); //start next line for the board
    }
    out.print(" "); //space before digits at the bottom of the board to center them
    for(int botDig = 0; botDig < board.length; botDig++){
      out.print(botDig); //print the digits out at the bottom of the board
    }
  }
  
  /**
   * Reveals everything on the board. Used to show the board generation if the game is won or lost.
   * @param exposed - the array indicating which locations have been exposed
   */
  static void revealBoard(boolean[][] exposed){
    for (int i = 0; i < exposed.length; i++) {
      for (int j = 0; j < exposed[i].length; j++) {
        exposed[i][j] = true; //visit every cell and expose them all
      }
    }
  }
  
  /**
   * Victory condition. Returns true if the sum of exposed cells and generated scat is equal to the board size.
   * @param exposed - the array indicating which locations have been exposed
   * @param scatOnBoard - the amount of scat across the entire board
   */
  static boolean victory(boolean[][] exposed, int scatOnBoard){
    return (exposedCellCounter(exposed)+scatOnBoard == boardSize*boardSize);
  }
  
  /**
   * Counts the number of exposed cells and returns that value.
   * @param exposed - the array indicating which locations have been exposed
   */
  static int exposedCellCounter(boolean[][] exposed){
    int exposedCells = 0; 
    for (int i = 0; i < exposed.length; i++) {
      for (int j = 0; j < exposed[i].length; j++) {
        if(exposed[i][j] == true){
          exposedCells++;
        }
      }
    }
    return exposedCells;
  }
  
  /**
   * Counts the number of scat across the entire board and returns that value.
   * @param board - the array indicating where the scat is located
   */
  static int scatCounter(boolean[][] board){
    int amountOfScat = 0;
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        if(board[i][j] == SCAT){
          amountOfScat++;
        }
      }
    }
    return amountOfScat;
  }
  
  /**
   * Assigns the number of neighboring scats surrounding each cell in a 3x3 grid to every cell in the counts array.
   * Does so by calling the scatNeighbors method and assigning the returned number to the counts array.
   * @param counts - the array of counts of neighbors with scat including the location itself if it contains scat
   * @param board - the array indicating where the scat is located
   */
  static void assignScatNumbers(int[][] counts, boolean[][] board){
    for (int i = 0; i < board.length; i++) {
      for (int j = 0; j < board[i].length; j++) {
        counts[i][j] = scatNeighbors(board,i,j); //assigns the number of nearby scat to each and every cell in counts
      }
    }
  }
  
  /**
   * Counts the number of scat surrounding each cell in a 3x3 grid and returns that value.
   * @param board - the array indicating where the scat is located
   * @param row - indicates the current row of the array
   * @param column - indicates the current column of the array
   */
  //Counts the number of scats surrounding each cell in a 3x3 square in the board
  static int scatNeighbors(boolean[][] board, int row, int column){
    int numberOfScat = 0;
    for(int i = row-1; i<= row+1; i++){ 
      for(int j = column-1; j<=column+1; j++){ //checks everything in a 3x3 grid around the cell
        if(i >= 0 && j >= 0 && i < board.length && j < board[i].length && board[i][j] == SCAT){ 
          numberOfScat++; //only +check for scat if within the array's boundaries
          if(board[row][column] == SCAT){ //if the coordinate itself is scat
            numberOfScat--; //remove it from the numberOfScat counted
          }
        }
      }
    } 
    return numberOfScat;
  }
  
  
  
  static void exposeStep(Scanner input, int rowInput, int columnInput, int numberOfScat, int numberOfExposedCells,
                         int seed, boolean[][] board, boolean[][] exposed, int[][] counts){
    exposedCellCounter(exposed); //counts the number of exposed cells for each generation of the board
    if(board[rowInput][columnInput] == CLEAR){
      expose(rowInput, columnInput, board, exposed, counts); 
      if(victory(exposed, numberOfScat) == true){ //if victory condition is met
        out.print("WELL DONE!");
        out.println();
        out.print("YOU WIN!");
        out.println();
        revealBoard(exposed);
        boardCreation(board, exposed, counts, seed);
        return; //stop the method
      }
      boardCreation(board, exposed, counts, seed); //if the game is still going, generate the board again
      out.println();
      out.print("Enter two integers(row and column):"); //prints only if the game is still going
      exposeStep(input, input.nextInt(), input.nextInt(), numberOfScat, numberOfExposedCells, seed, board, exposed,
                 counts); //ask for more inputs for another step
    }else if(board[rowInput][columnInput] == SCAT){
      out.print("YOU STEPPED IN SCAT!");
      out.println();
      out.print("YOU LOSE!");
      out.println();
      revealBoard(exposed);
      boardCreation(board, exposed, counts, seed);
    }
  }
  
  /**
   * Expose the specified location. In addition, if the location has a count of zero (no neighbors contain scat),
   * recursively expose all of the neighbors of the specified location.
   * @param r - the row number of the location to expose
   * @param c - the column number of the location to expose
   * @param board - the array indicating where the scat is located
   * @param exposed - the array indicating which locations have been exposed
   * @param counts - the array of counts of neighbors with scat including the location itself if it contains scat
   */
  static void expose(int r, int c, boolean[][] board, boolean[][] exposed, int[][] counts) {
    if (exposed[r][c]) return; // nothing to do
    // expose any neighbors that have zero counts
    exposed[r][c] = true;
    if (counts[r][c] > 0) return;
    for (int i = -1; i <= 1; i++) {
      for (int j = -1; j <= 1; j++) {
        int x = r+i;
        int y = c+j;
        if (!(i==0 && j==0) && x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
          if (counts[x][y] == 0) {
            expose(x, y, board, exposed, counts);
          }
          else {
            exposed[x][y] = true; // just expose the boarder region - no recursive call
          }
        }
      }
    }
  }
}
