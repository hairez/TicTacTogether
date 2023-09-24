import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class TTTWindow extends JFrame implements ActionListener
{
	static class Node { 
		//U - Up, M - Middle, D - Down
		//L - Left, M - Middle, R-Right
		//The first char describes the row, and the column
		Node UL;
		Node UM;
		Node UR;
		Node ML;
		Node MM;
		Node MR;
		Node DL;
		Node DM;
		Node DR;
		String value;
		public Node(String value) {
			this.value = value;
		}
	}

	/* 
	 * Node's value meaning:
	 * 
	 * 	- 		Empty
	 *  OO-XX 	Currently fighting for the spot, 
	 *  # 		Board
	 * 	X 		Cross
	 * 	O 		Circle
	 * 	* 		Star (Both circle and cross)
	 * 
	 */

	static Node rootnode = new Node("-");

	//currDimension shows the current perspective of which dimension/level we're in.
	String currDimension = "";
	String currPlayer = "O";

	JButton backButton = new JButton("Back");
	JButton continueButton = new JButton("Continue");

	JButton ULButton = new JButton("Place here");
	JButton UMButton = new JButton("Place here");
	JButton URButton = new JButton("Place here");
	JButton MLButton = new JButton("Place here");
	JButton MMButton = new JButton("Start");
	JButton MRButton = new JButton("Place here");
	JButton DLButton = new JButton("Place here");
	JButton DMButton = new JButton("Place here");
	JButton DRButton = new JButton("Place here");

	Integer maxMoves = 3; //The game initially has 3 moves. But the player can change maxMoves by using the JSpinner.

	//Moves is the current amount of moves left.
	Integer moves = maxMoves; 


	//								initial value, minimum value, maximum value, step 
	SpinnerModel value = new SpinnerNumberModel(3,2,9,1);  
	JSpinner maxMovesSpinner = new JSpinner(value);   

	Boolean placeable = true;
	Integer counter = 0;

	Node[] usedNodes = new Node[18];
	Node[] tempNode = new Node[9]; 

	char tempChar;
	String tempStr;
	Integer circles;
	Integer crosses;

	Boolean gameEnd = false;
	Boolean CircleWin = false;
	Boolean CrossWin = false;
	Boolean Tie = false;

	JLabel textLabel = new JLabel ("<html>Rules: <br/> Each player can place " + maxMoves + " pieces each turn. <br/> If you have a dominant amount of pieces on one square, you win the square. <br/> If it's tie, that square turns into another board. Win the board to win the square. <br/>The winner is the one that wins the main board.</html>");

	JPanel spinner = new JPanel(new FlowLayout(1,1,1));
	JPanel top = new JPanel(new GridLayout(2,1));
	JPanel center = new JPanel(new GridLayout(3,3));
	JPanel bottom = new JPanel (new GridLayout(1,2));

	public TTTWindow (){
		super("Tic Tac Toe Modified");
		setSize (500,400);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible (true);
		Container contentArea = getContentPane();
		contentArea.setBackground(Color.white);

		backButton.addActionListener(this);
		continueButton.addActionListener(this);

		ULButton.addActionListener(this);
		UMButton.addActionListener(this);
		URButton.addActionListener(this);
		MLButton.addActionListener(this);
		MMButton.addActionListener(this);
		MRButton.addActionListener(this);
		DLButton.addActionListener(this);
		DMButton.addActionListener(this);
		DRButton.addActionListener(this);

		top.add(textLabel);

		top.add(spinner);
		spinner.add(maxMovesSpinner);

		bottom.add(backButton);
		bottom.add(continueButton);

		center.add(ULButton);
		center.add(UMButton);
		center.add(URButton);
		center.add(MLButton);
		center.add(MMButton);
		center.add(MRButton);
		center.add(DLButton);
		center.add(DMButton);
		center.add(DRButton);

		ULButton.show(false);
		UMButton.show(false);
		URButton.show(false);
		MLButton.show(false);
		MRButton.show(false);
		DLButton.show(false);
		DMButton.show(false);
		DRButton.show(false);

		backButton.show(false);
		continueButton.show(false);

		//Makes it so that the user can't edit the spinner, and can only press on the buttons. This prevents the used to input non-integers and create errors.
		maxMovesSpinner.setEditor(new JSpinner.DefaultEditor(maxMovesSpinner));

		//Updates maxMoves depending on the value of the spinner
		maxMovesSpinner.addChangeListener(new ChangeListener() {  
			public void stateChanged(ChangeEvent e) {
				maxMoves = (Integer)((JSpinner)e.getSource()).getValue();
				textLabel.setText("<html>Rules: <br/> Each player can place " + maxMoves + " pieces each turn. <br/> If you have a dominant amount of pieces on one square, you win the square. <br/> If it's tie, that square turns into another board. Win the board to win the square. <br/>The winner is the one that wins the main board.</html>"); 
				moves = maxMoves;
			}  
		});  


		contentArea.add("North",top);
		contentArea.add("Center",center);
		contentArea.add("South",bottom);
		setContentPane(contentArea);
	}

	public void actionPerformed (ActionEvent event){	

		//Initiate the game by pressing start
		if (event.getSource() == MMButton && MMButton.getText().equals("Start")) {
			newBoard(rootnode);
			ULButton.show(true);
			UMButton.show(true);
			URButton.show(true);
			MLButton.show(true);
			MRButton.show(true);
			DLButton.show(true);
			DMButton.show(true);
			DRButton.show(true);			
			maxMovesSpinner.show(false);
			currPlayer = "O";
			moves = maxMoves;
			placeable = true;
			MMButton.setText("-");
			counter = 0;
		}

		//Preparing to replay the game when the player presses "play again". Resets all the variabels. 
		else if(continueButton.getText().equals("Play again") && event.getSource() == continueButton) {
			currDimension = "";
			ULButton.show(false);
			UMButton.show(false);
			URButton.show(false);
			MLButton.show(false);
			MRButton.show(false);
			DLButton.show(false);
			DMButton.show(false);
			DRButton.show(false);			
			maxMovesSpinner.show(true);
			rootnode.value = "-";
			MMButton.setEnabled(true);
			MMButton.setText("Start");

			//This deletes the previous board, since it also deletes all the nodes connected to the node.
			rootnode.UL = null;
			rootnode.UM = null;
			rootnode.UR = null;
			rootnode.ML = null;
			rootnode.MM = null;
			rootnode.MR = null;
			rootnode.DL = null;
			rootnode.DM = null;
			rootnode.DR = null;

			gameEnd = false;
			CircleWin = false;
			CrossWin = false;
			Tie = false;


			for (int i = 0; i< usedNodes.length;i++) {
				usedNodes[i] = null;
			}

			for (int i = 0; i< tempNode.length;i++) {
				tempNode[i] = null;
			}

			currPlayer = "-";

			continueButton.setText("Continue");
			continueButton.show(false);

			moves = maxMoves;
			textLabel.setText("<html>Rules: <br/> Each player can place " + maxMoves + " pieces each turn. <br/> If you have a dominant amount of pieces on one square, you win the square. <br/> If it's tie, that square turns into another board. Win the board to win the square. <br/>The winner is the one that wins the main board.</html>"); 

		}

		//Actions after pressing the squares during a players turn
		else if (event.getSource()== ULButton) {
			if (!currentNode(currDimension,rootnode).UL.value.equals("#") && placeable) {
				place(currentNode(currDimension,rootnode).UL);
			}
			else if (currentNode(currDimension,rootnode).UL.value.equals("#")) {
				currDimension = currDimension + "UL";
			}
		}
		else if (event.getSource()== UMButton) {
			if (!currentNode(currDimension,rootnode).UM.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).UM);
			}
			else if (currentNode(currDimension,rootnode).UM.value.equals("#")) {
				currDimension = currDimension + "UM";
			}
		}
		else if (event.getSource()== URButton) {
			if (!currentNode(currDimension,rootnode).UR.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).UR);
			}
			else if (currentNode(currDimension,rootnode).UR.value.equals("#")) {
				currDimension = currDimension + "UR";
			}
		}
		else if (event.getSource()== MLButton) {
			if (!currentNode(currDimension,rootnode).ML.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).ML);
			}
			else if (currentNode(currDimension,rootnode).ML.value.equals("#")) {
				currDimension = currDimension + "ML";
			}
		}
		else if (event.getSource()== MMButton) {
			if (!currentNode(currDimension,rootnode).MM.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).MM);
			}
			else if (currentNode(currDimension,rootnode).MM.value.equals("#")) {
				currDimension = currDimension + "MM";
			}
		}
		else if (event.getSource()== MRButton) {
			if (!currentNode(currDimension,rootnode).MR.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).MR);
			}
			else if (currentNode(currDimension,rootnode).MR.value.equals("#")) {
				currDimension = currDimension + "MR";
			}
		}
		else if (event.getSource()== DLButton) {
			if (!currentNode(currDimension,rootnode).DL.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).DL);
			}
			else if (currentNode(currDimension,rootnode).DL.value.equals("#")) {
				currDimension = currDimension + "DL";
			}
		}
		else if (event.getSource()== DMButton) {
			if (!currentNode(currDimension,rootnode).DM.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).DM);
			}
			else if (currentNode(currDimension,rootnode).DM.value.equals("#")) {
				currDimension = currDimension + "DM";
			}
		}
		else if (event.getSource()== DRButton) {
			if (!currentNode(currDimension,rootnode).DR.value.equals("#")&& placeable) {
				place(currentNode(currDimension,rootnode).DR);
			}
			else if (currentNode(currDimension,rootnode).DR.value.equals("#")) {
				currDimension = currDimension + "DR";
			}
		}


		//Backing out from the current dimension
		else if (event.getSource()== backButton) {
			currDimension = currDimension.substring(0,currDimension.length()-2);
		}

		//Next turn after both players have used their moves and no one has won
		else if (event.getSource()== continueButton && !gameEnd && moves != 0 && !MMButton.getText().equals("Start")) {
			counter = 0;

			for (int i = 0; i< usedNodes.length;i++) {
				usedNodes[i] = null;
			}

			for (int i = 0; i< tempNode.length;i++) {
				tempNode[i] = null;
			}

			placeable = true;

			currPlayer = "O";
			textLabel.setText("<html><font color='green'> Circle's </font> turn! Select a spot to place your remaining " + moves + " piece(s).</html>");

			currDimension = "";
			continueButton.show(false);

			counter = 0;
		}


		//Updating the amount of moves the player has left.
		if (moves != 0) {
			if (currPlayer.equals("O")) {
				textLabel.setText("<html><font color='green'> Circle's </font> turn! Select a spot to place your remaining " + moves + " piece(s).</html>");
			}
			else if (currPlayer.equals("X")){
				textLabel.setText("<html><font color='red'> Cross's </font> turn! Select a spot to place your remaining " + moves + " piece(s).</html>");
			}
		}

		//Moving on to the next part when a player has used all of their moves
		else {
			//When Circle is done it is Cross's turn
			if (currPlayer.equals("O") && placeable) {
				currPlayer = "X";
				moves = maxMoves;
				hideMoves();				
				currDimension = "";
				textLabel.setText("<html><font color='red'> Cross's </font> turn! Select a spot to place your remaining " + moves + " piece(s).</html>");

			}
			//When Cross is done, the game shows the results
			else if (currPlayer.equals("X") && placeable){
				showResults();
				placeable = false;
				textLabel.setText("Showing results:");
				continueButton.show(true);
				currDimension = "";
				currPlayer = "-";

			}

			//Showing the results of all moves. 
			else if (event.getSource()== continueButton && !gameEnd) {

				calculateMoves();
				checkWin(rootnode);

				tempStr = winScan(rootnode);

				if (tempStr.equals("X")) {
					CrossWin = true;
					gameEnd = true;
				}
				else if (tempStr.equals("O")) {
					CircleWin = true;
					gameEnd = true;
				}
				else if (tempStr.equals("*")) {
					Tie = true;
					gameEnd = true;
				}
				else {
					moves = maxMoves;
				}
			}

			//If anyone has won, the winner is announced
			else if (event.getSource()== continueButton && gameEnd) {

				if (Tie) {
					textLabel.setText("It's a tie! Play again?");
				}
				else if (CrossWin) {
					textLabel.setText("<html><font color='red'> Cross </font> won! Play again?</html>");
				}
				else if (CircleWin) {
					textLabel.setText("<html><font color='green'> Circle </font> won! Play again?</html>");
				}

				continueButton.setText("Play again");
			} 
		}



		//Checks if we are on the base dimension. If not, the player can go back to the previous dimension.
		if (currDimension != "") {
			backButton.show(true);
		}
		else {
			backButton.show(false);
		}

		//Always updates the buttons' texts depending on the dimension, unless the game hasn't started.
		if (!MMButton.getText().equals("Start")) {
			updateButtons(currDimension, rootnode);
		}


		//Always grabs focus to the middle button, so that the players can't predict the other player's moves.
		MMButton.requestFocusInWindow(); 

	}


	/**
	 * A recursive method that returns the current board/node that is showing. 
	 * 
	 * @param dimension		A string with the current dimension that we're in.
	 * @param rootnode		The root node.
	 * @return				The node that the current board is showing.
	 */
	public Node currentNode(String dimension, Node rootnode) {

		if (dimension.equals("")) {
			return rootnode;
		}
		else if (dimension.substring(0,2).equals("UL")) {
			return currentNode(dimension.substring(2), rootnode.UL);
		}
		else if (dimension.substring(0,2).equals("UM")){
			return currentNode(dimension.substring(2), rootnode.UM);
		}
		else if (dimension.substring(0,2).equals("UR")){
			return currentNode(dimension.substring(2), rootnode.UR);
		}
		else if (dimension.substring(0,2).equals("ML")){
			return currentNode(dimension.substring(2), rootnode.ML);
		}
		else if (dimension.substring(0,2).equals("MM")){
			return currentNode(dimension.substring(2), rootnode.MM);
		}
		else if (dimension.substring(0,2).equals("MR")){
			return currentNode(dimension.substring(2), rootnode.MR);
		}
		else if (dimension.substring(0,2).equals("DL")){
			return currentNode(dimension.substring(2), rootnode.DL);
		}
		else if (dimension.substring(0,2).equals("DM")){
			return currentNode(dimension.substring(2), rootnode.DM);
		}
		else if (dimension.substring(0,2).equals("DR")){
			return currentNode(dimension.substring(2), rootnode.DR);
		}
		return null;
	}

	/**
	 * A method that updates all the text on the buttons depending on which dimension and node that is showing.
	 * 
	 * @param dimension		A string with the current dimension that we're in.
	 * @param rootnode		The root node.
	 */
	public void updateButtons(String dimension, Node rootnode) {
		ULButton.setText(currentNode(dimension,rootnode).UL.value);
		UMButton.setText(currentNode(dimension,rootnode).UM.value);
		URButton.setText(currentNode(dimension,rootnode).UR.value);
		MLButton.setText(currentNode(dimension,rootnode).ML.value);
		MMButton.setText(currentNode(dimension,rootnode).MM.value);
		MRButton.setText(currentNode(dimension,rootnode).MR.value);
		DLButton.setText(currentNode(dimension,rootnode).DL.value);
		DMButton.setText(currentNode(dimension,rootnode).DM.value);
		DRButton.setText(currentNode(dimension,rootnode).DR.value);

		//If the square is already settled, then no one can place on that square anymore.
		if (ULButton.getText().equals("X") || ULButton.getText().equals("O") || ULButton.getText().equals("*")){
			ULButton.setEnabled(false);
		}
		else {
			ULButton.setEnabled(true);
		}

		if (UMButton.getText().equals("X") || UMButton.getText().equals("O") || UMButton.getText().equals("*")){
			UMButton.setEnabled(false);
		}
		else {
			UMButton.setEnabled(true);
		}

		if (URButton.getText().equals("X") || URButton.getText().equals("O") || URButton.getText().equals("*")){
			URButton.setEnabled(false);
		}
		else {
			URButton.setEnabled(true);
		}

		if (MLButton.getText().equals("X") || MLButton.getText().equals("O") || MLButton.getText().equals("*")){
			MLButton.setEnabled(false);
		}
		else {
			MLButton.setEnabled(true);
		}

		if (MMButton.getText().equals("X") || MMButton.getText().equals("O") || MMButton.getText().equals("*")){
			MMButton.setEnabled(false);
		}
		else {
			MMButton.setEnabled(true);
		}

		if (MRButton.getText().equals("X") || MRButton.getText().equals("O") || MRButton.getText().equals("*")){
			MRButton.setEnabled(false);
		}
		else {
			MRButton.setEnabled(true);
		}

		if (DLButton.getText().equals("X") || DLButton.getText().equals("O") || DLButton.getText().equals("*")){
			DLButton.setEnabled(false);
		}
		else {
			DLButton.setEnabled(true);
		}

		if (DMButton.getText().equals("X") || DMButton.getText().equals("O") || DMButton.getText().equals("*")){
			DMButton.setEnabled(false);
		}
		else {
			DMButton.setEnabled(true);
		}

		if (DRButton.getText().equals("X") || DRButton.getText().equals("O") || DRButton.getText().equals("*")){
			DRButton.setEnabled(false);
		}
		else {
			DRButton.setEnabled(true);
		}

	}

	/**
	 * A method used to place a certain object on the chosen square. It places a circle if it is the circle's turn, and cross if it is the cross's turn.
	 * 
	 * @param node			The node/square that the player placed on. 
	 */
	public void place(Node node) {
		node.value = node.value + currPlayer;
		moves -= 1;

		if (currPlayer.equals("O")) {
			tempNode[moves] = node;
		}

		//Storing the nodes that are used by any of the players this turn
		usedNodes[counter] = node;
		counter++;

	}

	/**
	 * A method used to hide the moved that the previous player made. 
	 */
	public void hideMoves() {
		for (int i = 0; i < maxMoves; i++) {
			tempNode[i].value = "-";
		}
	}


	/**
	 * Method that calculates the result on the squares. 
	 */
	public void calculateMoves() {
		for (int i = 0; i < 2*maxMoves; i++) {
			if (usedNodes[i] != null) {
				circles = 0;
				crosses = 0;

				//calculating the number of circles and crosses there are on nodes where they were updated this turn.
				for (int a = 0; a < usedNodes[i].value.length();a++) {
					tempChar = usedNodes[i].value.charAt(a);
					if (tempChar == 'O') {
						circles++;
					}
					else if (tempChar == 'X') {
						crosses++;
					}
				}

				if (circles>crosses) {
					usedNodes[i].value = "O";
				}
				else if (circles<crosses) {
					usedNodes[i].value = "X";
				}
				else {
					//Creates a new board on that square if it is a tie.
					newBoard(usedNodes[i]);
					usedNodes[i].value = "#";
				}

			}
		}
	}

	/**
	 * Method used to check if anyone has won on any square possible by later using winScan().
	 * 
	 * @param node		The node where it starts to check. The method only goes deeper to check, therefore to check every square, the optimal node would be rootnode.
	 */
	public void checkWin(Node node) {
		if (node.UL.value.equals("#")) {
			checkWin(node.UL);
		}
		if (node.UM.value.equals("#")) {
			checkWin(node.UM);
		}
		if (node.UR.value.equals("#")) {
			checkWin(node.UR);
		}
		if (node.ML.value.equals("#")) {
			checkWin(node.ML);
		}
		if (node.MM.value.equals("#")) {
			checkWin(node.MM);
		}
		if (node.MR.value.equals("#")) {
			checkWin(node.MR);
		}
		if (node.DL.value.equals("#")) {
			checkWin(node.DL);
		}
		if (node.DM.value.equals("#")) {
			checkWin(node.DM);
		}
		if (node.DR.value.equals("#")) {
			checkWin(node.DR);
		}

		if (node != rootnode) {
			node.value = winScan(node);
		}		
	}

	/**
	 * Method checking if anyone has won in a specific node.
	 * 
	 * @param currNode	The node where it is checked
	 * @return			A string that describes who has won that node/board. "*" if it's a tie, "O" if circle won, "X" if cross won, and "#" if no one won.
	 */
	public String winScan (Node currNode) {
		Boolean OWin = false;
		Boolean XWin = false;

		String[] XO = {"X","O"};

		Node[] firCol = {currNode.UL, currNode.ML,currNode.DL};
		Node[] secCol = {currNode.UM, currNode.MM,currNode.DM};
		Node[] thiCol = {currNode.UR, currNode.MR,currNode.DR};

		Node[] firRow = {currNode.UL, currNode.UM,currNode.UR};
		Node[] secRow = {currNode.ML, currNode.MM,currNode.MR};
		Node[] thiRow = {currNode.DL, currNode.DM,currNode.DR};

		for (int i = 0; i<=1;i++) {

			for (int a = 0; a<=2;a++) {
				//Checking if anyone has won in any column
				if (firRow[a].value.equals(XO[i]) || firRow[a].value.equals("*") ) {
					if (secRow[a].value.equals(XO[i]) || secRow[a].value.equals("*") ) {
						if (thiRow[a].value.equals(XO[i]) || thiRow[a].value.equals("*") ) {
							if (i == 0) {
								XWin = true;
							}
							else {
								OWin = true;
							}
						}
					}
				}

				//Checking if anyone has won in any row
				if (firCol[a].value.equals(XO[i]) || firCol[a].value.equals("*") ) {
					if (secCol[a].value.equals(XO[i]) || secCol[a].value.equals("*") ) {
						if (thiCol[a].value.equals(XO[i]) || thiCol[a].value.equals("*") ) {
							if (i == 0) {
								XWin = true;
							}
							else {
								OWin = true;
							}
						}
					}
				}

			}


			//Checking if anyone has won in any diagonal
			if (currNode.UL.value.equals(XO[i]) || currNode.UL.value.equals("*") ) {
				if (currNode.MM.value.equals(XO[i]) || currNode.MM.value.equals("*") ) {
					if (currNode.DR.value.equals(XO[i]) || currNode.DR.value.equals("*") ) {
						if (i == 0) {
							XWin = true;
						}
						else {
							OWin = true;
						}
					}
				}
			}
			if (currNode.UR.value.equals(XO[i]) || currNode.UR.value.equals("*") ) {
				if (currNode.MM.value.equals(XO[i]) || currNode.MM.value.equals("*") ) {
					if (currNode.DL.value.equals(XO[i]) || currNode.DL.value.equals("*") ) {
						if (i == 0) {
							XWin = true;
						}
						else {
							OWin = true;
						}
					}
				}
			}
		}

		if (XWin && OWin) {
			return "*";
		}
		else if (OWin){
			return "O";
		}
		else if (XWin){
			return "X";
		}
		else {
			return "#";
		}


	}

	/**
	 * Method showing the results of both players before calculating them.
	 */
	public void showResults() {
		for (int i = 0; i < maxMoves; i++) {
			tempNode[i].value = "O" + tempNode[i].value;
		}
	}

	/**
	 * Method creating a new board on the specific node. 
	 * 
	 * @param node		The node where the new board is created.
	 */
	public void newBoard(Node node) {
		node.UL = new Node("-");		
		node.UM = new Node("-");
		node.UR = new Node("-");
		node.ML = new Node("-");
		node.MM = new Node("-");
		node.MR = new Node("-");
		node.DL = new Node("-");
		node.DM = new Node("-");
		node.DR = new Node("-");
		node.value = "#";
	}
}


public class TicTacMain {
	public static void main(String[] args) {
		TTTWindow Win = new TTTWindow();
	}
}	


