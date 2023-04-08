import java.text.spi.BreakIteratorProvider;
import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.FlowLayout;
import java.net.URL;

/**
 * A class modelling a tic-tac-toe (noughts and crosses, Xs and Os) game.
 * 
 * Citation for icons: I made my own icons. I didn't borrow any icons from the internet.
 * 
 * Citation for sounds:
 * Player X sound: https://mixkit.co/free-sound-effects/click/, I used the Arcade game jump coin sound.
 * Player O sound: https://mixkit.co/free-sound-effects/click/, I used the Classic click sound.
 * Game Over/ winning sound: https://mixkit.co/free-sound-effects/click/, I used the Positive interface beep sound. 
 * 
 * @author Manit Jawa 101215842
 * @version April 8, 2023
 * 
 * @author Lynn Marshall
 * @version November 8, 2012
 */

public class TicTacToe implements ActionListener
{
   public static final String PLAYER_X = "X"; // player using "X"
   public static final String PLAYER_O = "O"; // player using "O"
   public static final String EMPTY = " ";  // empty cell
   public static final String TIE = "T"; // game ended in a tie
 
   private String player;   // current player (PLAYER_X or PLAYER_O)

   private String winner;   // winner: PLAYER_X, PLAYER_O, TIE, EMPTY = in progress

   private int numFreeSquares; // number of squares still free
   
   private int row, col; // row and col made private variables outside of the function to improve working of the game
   
   private JButton board[][]; // 3x3 array of JButtons representing the board
   
   private JLabel label; // Label 
   
   private JMenuItem quitItem; // Quit menu item - used to quit the game
    
   private JMenuItem newItem; // New Game menu item - used to start a new game
   
   private JMenuItem changePlayer; // Change Player menu item - used to change starting player to O from X
   
   private JTextArea history; // history text area - text area presenting number of games won by X, O and number of ties
   
   private int xWins, oWins, numTies; // number of games won by X, O and number of ties respectively
    
   private AudioClip click; // audioclip played after clicking a button - different for player X and player O
   
   private static ImageIcon xIcon; // mark for player X 
   private static ImageIcon oIcon; // mark for player O
   private static ImageIcon blankIcon; // icon for unselected buttons
   
   /** 
    * Constructs a new Tic-Tac-Toe board.
    */
   public TicTacToe()
   {
      board = new JButton[3][3]; // initialises the 3x3 array of JButtons, which will be the board
      xWins = 0; 
      oWins = 0;
      numTies = 0;
      
      // JFrame
      JFrame frame = new JFrame("Tic Tac Toe"); // create a new frame for Tic Tac Toe game
      frame.setPreferredSize(new Dimension(400,500)); // set the size of the frame - width, height
      Container contentPane = frame.getContentPane(); 
      contentPane.setLayout(new BorderLayout()); // use border layout (default)
      
      JMenuBar menubar = new JMenuBar(); // create a menu bar 
      frame.setJMenuBar(menubar); // add menu bar to our frame

      JMenu fileMenu = new JMenu("Options"); // create a menu
      menubar.add(fileMenu); // add to our menu bar
      
      quitItem = new JMenuItem("Quit"); // create a menu item called "Quit"
      fileMenu.add(quitItem); // add to our menu
      
      newItem = new JMenuItem("New"); // create a menu item called "New"
      fileMenu.add(newItem); // add to our menu
      
      changePlayer = new JMenuItem("Start game with O"); // create a menu item "Start game with O"
      fileMenu.add(changePlayer); // add to our menu
      
      final int SHORTCUT_MASK = Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx(); // to save typing
      quitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, SHORTCUT_MASK)); // CTRL-Q shortcut for "Quit" menu item
      newItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, SHORTCUT_MASK)); // CTRL-N shortcut for "New" menu item
      changePlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, SHORTCUT_MASK)); // CTRL-P shortcut for "Start game with O" menu item
      
      quitItem.addActionListener(new ActionListener() // create an anonymous inner class
        { // start of anonymous subclass of ActionListener
          // this allows us to put the code for this action here  
            public void actionPerformed(ActionEvent event)
            {
                System.exit(0); // quit
            }
        } // end of anonymous subclass
      ); // end of addActionListener parameter list and statement
      
      newItem.addActionListener(new ActionListener() // create an anonymous inner class
        { // start of anonymous subclass of ActionListener
          // this allows us to put the code for this action here  
            public void actionPerformed(ActionEvent event)
            {
                clearBoard(); // starts a new game by setting up everything for a new game 
            }
        } // end of anonymous subclass
      ); // end of addActionListener parameter list and statement

      changePlayer.addActionListener(new ActionListener() // create an anonymous inner class
        { // start of anonymous subclass of ActionListener
          // this allows us to put the code for this action here  
            public void actionPerformed(ActionEvent event)
            {
                clearBoard(); // starts a new game by setting up everything for a new game 
                player = PLAYER_O; // changes starting player to O 
                label.setText("game in progress and " + player + "'s turn"); // reflects the change in the label
            }
        } // end of anonymous subclass
      ); // end of addActionListener parameter list and statement
      
      label = new JLabel("Tic Tac Toe"); // creates a new label
      label.setHorizontalAlignment(JLabel.RIGHT); // right justified
      label.setVerticalTextPosition(JLabel.BOTTOM);
      label.setFont(new Font("Monaco", Font.PLAIN, 18)); // sets font to Monaco, size 18
      contentPane.add(label,BorderLayout.WEST); // west side 
      
      JPanel buttonPanel = new JPanel(); // creates a new button panel
      buttonPanel.setLayout(new GridLayout(3,3)); // 3x3 grid for 3x3 array of JButtons
      buttonPanel.setBackground(Color.white); // sets background color to white
      contentPane.add(buttonPanel, BorderLayout.NORTH); // north side
      
      xIcon = new ImageIcon("x_icon.jpg"); // creates a new icon for x using image found in the directory
      oIcon = new ImageIcon("o_icon.jpg"); // creates a new icon for o using image found in the directory
      blankIcon = new ImageIcon("blank_icon.jpg"); // creates a new blank icon using image found in directory
      
      // register buttons as listeners
      for (int i=0; i<3; i++){
            for (int j=0; j<3; j++){
                board[i][j] = new JButton(EMPTY); // sets text of each button empty
                board[i][j].setPreferredSize(new Dimension(50, 60)); // sets the size to 50x60 for a neat UI
                board[i][j].setFont(new Font("Arial", Font.PLAIN, 0)); // sets font to Arial, size 0 - because no text will be shown, only 
                // the icon will be shown. However text, though size 0, is present and is used to determine if a game is won by either player.
                board[i][j].addActionListener(this); 
                buttonPanel.add(board[i][j]); // adds button to the button panel
            }
        } 
        
      history = new JTextArea(10,10); // creates a new JTextArea for history
      JScrollPane pane = new JScrollPane(history); // put text area in a scroll pane
      contentPane.add(pane,BorderLayout.SOUTH); // south side
      history.setText(" "); // set text to be empty
      history.setFont(new Font("Monaco", Font.PLAIN, 18)); // sets font to Monaco, size 18
      history.setCaretPosition(history.getDocument().getLength()); // move cursor to end
      history.setEditable(false); // not editable by players
      history.setBackground(new Color(208, 247, 203)); // sets background color to specified color 
      
      // finish setting up the frame
      frame.getContentPane().setBackground(new Color(149, 240, 217) ); // sets background color of frame to specified color
      frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE); // exit when we hit the "X"
      frame.pack(); // pack everthing into our frame
      frame.setResizable(false); // we cannot resize it
      frame.setVisible(true); // it's visible
      
      playGame(); // calls playGame() to start the game as soon as the object is created
   }

   /**
    * Sets everything up for a new game. Sets text of all buttons empty, sets icons of all buttons blank,
    * enables all buttons. Sets winner to be empty, number of free squares to be 9, sets the starting player 
    * to PLAYER_X. Updates the label and history to display the current state of the game and the current information
    * about the number of wins and ties. 
    */
   private void clearBoard()
   {
      for (int i = 0; i < 3; i++) {
         for (int j = 0; j < 3; j++) {
            board[i][j].setText(EMPTY); // sets the text of buttons to be empty - buttons don't display anything
            board[i][j].setIcon(blankIcon); // put blank icons for buttons
            board[i][j].setDisabledIcon(blankIcon); // put blank icon when buttons are disabled
            board[i][j].setEnabled(true); // enable buttons
        }
      }
      winner = EMPTY; 
      numFreeSquares = 9;
      player = PLAYER_X;     // Player X always has the first turn.
      label.setText("game in progress and " + player + "'s turn"); // updates label to reflect the current state of the game
      history.setText("History \nNumber of games won by X: " + xWins + "\nNumber of games won by O: " + oWins + "\nNumber of Ties: "+numTies);
      // updates history to display number of games won by x, o and number of ties
   }


   /**
    * Plays one game of Tic Tac Toe.
    */

   public void playGame()
   {
      clearBoard(); // clear the board
      // The rest of this function was mainly moved to actionPerformed() function of buttons 
      // so that actions take place only when buttons are clicked
   } 


   /**
    * Returns true if pressing the button at the given position gives us a winner, and false
    * otherwise.
    *
    * @param int row of button just set
    * @param int col of button just set
    * 
    * @return true if we have a winner, false otherwise
    */
   private boolean haveWinner(int row, int col) 
   {
       // unless at least 5 squares have been filled, we don't need to go any further
       // (the earliest we can have a winner is after player X's 3rd move).

       if (numFreeSquares>4) return false;

       // Note: We don't need to check all rows, columns, and diagonals, only those
       // that contain the latest filled square.  We know that we have a winner 
       // if all 3 squares are the same, as they can't all be blank (as the latest
       // filled square is one of them).

       // check row "row"
       if ( board[row][0].getText().equals(board[row][1].getText()) &&
            board[row][0].getText().equals(board[row][2].getText()) ) return true;
       
       // check column "col"
       if ( board[0][col].getText().equals(board[1][col].getText()) &&
            board[0][col].getText().equals(board[2][col].getText()) ) return true;

       // if row=col check one diagonal
       if (row==col)
          if ( board[0][0].getText().equals(board[1][1].getText()) &&
               board[0][0].getText().equals(board[2][2].getText()) ) return true;

       // if row=2-col check other diagonal
       if (row==2-col)
          if ( board[0][2].getText().equals(board[1][1].getText()) &&
               board[0][2].getText().equals(board[2][0].getText()) ) return true;

       // no winner yet
       return false;
   }

   
   /** This action listener is called when the user clicks on 
    * any of the GUI's buttons. 
    */
   public void actionPerformed(ActionEvent e)
    {
        Object o = e.getSource(); // get the action 
        
        // see if it's a JButton
        if (o instanceof JButton) {
            
            if (player==PLAYER_X){ // see if it's player X
                URL urlClick = TicTacToe.class.getResource("x_clip.wav"); // get clicking sound for player X from directory
                click = Applet.newAudioClip(urlClick); // updates click variable to be player X sound
                click.play(); // just plays clip once
            } else if(player==PLAYER_O){
                URL urlClick = TicTacToe.class.getResource("o_clip.wav"); // get clicking sound for player O from directory
                click = Applet.newAudioClip(urlClick); // updates click variable to be player O sound
                click.play(); // just plays clip once
            }
            JButton button = (JButton)o;
            
            for (int i=0; i<3; i++){
                for (int j=0; j<3; j++){
                    if (button.equals(board[i][j])){
                        board[i][j].setText(player);  // fill in the button with player
                        board[i][j].setEnabled(false); // disable the button so that the other player is not able to change the mark
                        numFreeSquares--; // decrement number of free squares
                        row = i; 
                        col = j;
                        
                        if(player==PLAYER_X){
                            board[i][j].setDisabledIcon(xIcon); // set the icon for disabled button to be the mark of player X
                        } else if(player==PLAYER_O){
                            board[i][j].setDisabledIcon(oIcon); // set the icon for disabled button to be the mark of player O
                        }
                        break;
                    }
                }
        }
        
        // see if the game is over - if we either have a winner or a tie
        if (haveWinner(row,col)){ 
            URL urlClick = TicTacToe.class.getResource("win_clip.wav"); // get the sound for game over from directory
            click = Applet.newAudioClip(urlClick); // updates click variable to be game over sound
            click.play(); // just plays clip once
            winner = player; // must be the player who just went
            for (int i=0; i<3; i++){
                for (int j=0; j<3; j++){
                    board[i][j].setEnabled(false); // disable all buttons 
                }
            }
            if (winner==PLAYER_X){
                xWins += 1; // increase the number of games won by player X by 1
            } else if(winner==PLAYER_O){
                oWins += 1; // increase the number of games won by player O by 1
            }
            // update label and history to display the current state of the game and new information
            history.setText("History \nNumber of games won by X: " + xWins + "\nNumber of games won by O: " + oWins + "\nNumber of Ties: "+numTies);
            label.setText("game over " + winner + " wins");
  
        }
        else if (numFreeSquares==0) {
            URL urlClick = TicTacToe.class.getResource("win_clip.wav"); // get the sound for game over from directory
            click = Applet.newAudioClip(urlClick); // updates click variable to be game over sound
            click.play(); // just plays clip once
            winner = TIE; // board is full so it's a tie
            label.setText("game over tie"); // update label to display current state of the game
            numTies += 1; // increase number of ties by 1
            // update history to display the new information
            history.setText("History \nNumber of games won by X: " + xWins + "\nNumber of games won by O: " + oWins + "\nNumber of Ties: "+numTies);
        }
         
         // change to other player (this won't do anything if game has ended)
         if (player==PLAYER_X) {
            player=PLAYER_O; 
        }
         else{
            player=PLAYER_X;
            } 
        
        if (winner == EMPTY){
            label.setText("game in progress and " + player + "'s turn"); // update label to display the current state of the game
        }
        
        }
        
   }
   
}

