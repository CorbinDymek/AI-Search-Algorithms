package path.agent;

import java.awt.Point;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import path.level.Level;


public class BreadthFirstSearch extends PathAgent{
	
	public int height = 0;

	public BreadthFirstSearch(Level lvl) {
		super(lvl);
	}
	
	
	/**
	 * Uses a queue to find the path from a start node to a goal node. It performs a BFS and expands until
	 * all nodes have been searched or goal node is found. Returns a list of points consisting of the path, or null otherwise.
	 * Creates a tree of all nodes explored. 
	 */
	public List<Point> findPath() {
		
		Queue<Node> fringe = new LinkedList<>(); //Fringe to store the nodes to be explored
	    Set<Node> visited = new HashSet<>(); //Set to track visited nodes to avoid revisiting them
	    path = new LinkedList<Point>(); //List to stroe the final path
		
		
		 assert start != null; //Ensure start point is not null
	     assert goal != null; //Ensure goal point is not null
	     
	     Node startNode = new Node(start, null, null); //Create the start node
	     fringe.add(startNode); //add start node to fringe for exploration
	     visited.add(startNode); //mark the start node as visited
	     root = startNode; //Set the root of the path to the start node
	     
	     while(!fringe.isEmpty()) { //while there are still nodes to explore
	    	 Node currentNode = fringe.poll(); //remove and get the next node from the fringe
	    	 if(currentNode.getState().equals(goal)) { //Check if the current node is the goal
	    		 path = pathFromNode(currentNode); //Get path from the goal node to the start node
	    		 return this.getPath();
	    	 }
	    	 
	    	 Set<Action> actionList = getPossibleActions(currentNode.getState()); //get possible actions from the current node
	    	 
	    	 for(Action currentAction: actionList) { //loop through all possible actions
	    		 Point nextState = getNextState(currentNode.getState(),currentAction); //get the next state after applying the action
	    		 Node nextNode = new Node(nextState,currentNode,currentAction); //create node with next state
	    		 
	    		 if(!visited.contains(nextNode) && !fringe.contains(nextNode)) { //if the next node is not visited or not in the fringe
	    			 fringe.add(nextNode); //add next node to fringe for exploration
	    			 visited.add(nextNode); //mark the next node as visited
	    			 
	    			 if (currentNode.getChildren() == null) { //if current node doesn't have children initialize the list
	                        currentNode.setChildren(new LinkedList<>());
	                 }
	                 currentNode.getChildren().add(nextNode); //add the next node as a child of the current node
	    		 }
	    	 }
	    	 
	     }
	     
	
	     return null; //if path is not found return null
		
	}
	
	/**
	 * Looks N,W,S,E through points and checks if they are valid states on the GUI
	 * @param current point
	 * @return a set of possible actions that can be taken from a given point
	 */
	public Set<Action> getPossibleActions(Point current) {
	    Set<Action> actions = new HashSet<>(); //create set to store possible actions

	    int x = current.x; //get the x-coordinate of the current point
	    int y = current.y; //get the y-coordinate of the current point
	    //check if moving north is valid action
	    if (level.isValid(new Point(x, y - 1))) {
	        actions.add(Action.N);//add north action to set
	    }
	    //check if moving south is valid action
	    if (level.isValid(new Point(x, y + 1))) { 
	        actions.add(Action.S);//add south action to set
	    }
	    //check if moving west is valid action
	    if (level.isValid(new Point(x - 1, y))) { 
	        actions.add(Action.W);//add west action to set
	    }
	    //check if moving east is valid action
	    if (level.isValid(new Point(x + 1, y))) { 
	        actions.add(Action.E);//add east action to set
	    }

	    return actions; //return the set of valid actions
	}
	
	/**
	 * Takes in a given point and action and adds or subtracts in order to get the next point 
	 * @param currentPoint
	 * @param action
	 * @return the next point based on the action that it is given (N,S,W,E)
	 */
	public Point getNextState(Point currentPoint, Action action) {
	    int x = currentPoint.x; //get the current x-coordinate of the point
	    int y = currentPoint.y; //get the current y-coordinate of the point

	    //switch statement to determine the next state based on the action
	    switch (action) {
	        case N: //if action is north
	            y-=10; //move one unit up
	            break;
	        case S: //if action is south
	            y+=10; //move one unit down
	            break;
	        case E: //if action is east
	            x+=10; //move one unit right
	            break;
	        case W: //if action is west
	            x-=10; //move one unit left
	            break;
	    }

	    return new Point(x, y); //return the new point representing the next state
	}
	
	
	
	
	/**
	 * We supply a string label to show up on the user interface.
	 */
	public String toString() {
		return "Breadth First Search";
	}
}
