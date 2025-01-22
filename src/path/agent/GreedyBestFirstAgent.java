package path.agent;

import java.awt.Point;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import path.level.Level;


public class GreedyBestFirstAgent extends PathAgent{
	
	

	public GreedyBestFirstAgent(Level lvl) {
		super(lvl);
	}
	
	
	
	PriorityQueue<Node> fringe;
    
    Set<Node> visited;
    
    /**
     * Returns a list of points from a start point to an end point while using a priority queue.
     * The priority queue is determined using the heuristic value  
     * It creates a tree of nodes that is searches
     */
	public List<Point> findPath() {
		 	assert start != null; //ensure start point is not null
		 	assert goal != null; //ensure goal point is not null
	     
		 	Node startNode = new Node(start, null, null); //Create the start node with no parent or action
		 	path = new LinkedList<Point>(); //initialize the path as an empty list
		 	//create a priority queue for the fringe, sorted by heuristic value
			fringe = new PriorityQueue<>(Comparator.comparingDouble(node -> getHeuristic(node)));
	        fringe.add(startNode); //add the start node to the fringe
	        root = startNode; //set the root to the start node

	        
	        visited = new HashSet<>(); //set to track visited nodes

	        while (!fringe.isEmpty()) { //while there are still nodes in the fringe to explore
	            Node currentNode = fringe.poll();//Get the node with the lowest heuristic value  

	            //If the current node is the goal, reconstruct the path and return it
	            if(currentNode.getState().equals(goal)) {
	            	path = pathFromNode(currentNode); //Reconstruct the path from the goal node
		    		return this.getPath();
		    	 }
	            
	            visited.add(currentNode); //Mark the current node as visited
	            Set<Action> actionList = getPossibleActions(currentNode.getState()); //get possible actions from current node
	            
	            for (Action action : actionList) { //loop through each possible action
	            	Point nextState = getNextState(currentNode.getState(),action); //get the next state after applying the action
		    		Node next = new Node(nextState,currentNode,action); //create a new node for the next state
	                
	                //if next node is neither in the fringe or visited, add it to the fringe
	                if (!fringe.contains(next) && !visited.contains(next)) {
	                    fringe.add(next); 
	                    if (currentNode.getChildren() == null) { //if current node doesn't have children initialize the list
	                        currentNode.setChildren(new LinkedList<>());
	                    }
	                    currentNode.getChildren().add(next); //add the next node as a child of the current node
	                }
	                //If the next node is already in the fringe, check if the heuristic is better
	                else if (fringe.contains(next)) {
	                	for (Node fringeNode : fringe) { //loop through all nodes in the fringe
	                        if (fringeNode.getState() == next.getState()) { //if the node's state is already in the fringe
	                            //If the heuristic of the next node is smaller, update the fringe
	                        	if (getHeuristic(next) < getHeuristic(fringeNode)) {
	                            	
	                                fringe.remove(fringeNode); //remove the old node from the fringe
	                                fringe.add(next); //add the new node with the better heuristic
	                            }
	                            break; //exit the loop once the correct fringe node is found
	                        }
	                    }
	                }
	            }

	            
	            visited.add(currentNode); //mark the current node as visited
	        }
		return null; //if no path is found return null
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
	    
	    //checking if 8-connected actions is selected in MainFrame
	    if(allDirection) {
	    	
	    
		    if (level.isValid(new Point(x-1, y - 1))) {
		        actions.add(Action.NW);//add north west action to set
		    }
		    //check if moving south is valid action
		    if (level.isValid(new Point(x+1, y + 1))) { 
		        actions.add(Action.SE);//add south east action to set
		    }
		    //check if moving west is valid action
		    if (level.isValid(new Point(x + 1, y-1))) { 
		        actions.add(Action.NE);//add north west action to set
		    }
		    //check if moving east is valid action
		    if (level.isValid(new Point(x - 1, y+1))) { 
		        actions.add(Action.SW);//add south west action to set
		    }
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
	        case NW: //if action is north west
	            y-=10; //move one unit up
	            x-=10; //move one unit left
	            break;
	        case SE: //if action is south east
	            y+=10; //move one unit down
	            x+=10; //move one unit right
	            break;
	        case NE: //if action is north east 
	            x+=10; //move one unit right
	            y-=10; //move one unit up
	            break;
	        case SW: //if action is south west
	            x-=10; //move one unit left
	            y+=10; //move on unit down
	            break;
	    }

	    return new Point(x, y); //return the new point representing the next state
	}
	
	/**
	 * This function calculates the heuristic value of a current node
	 * @param myNode
	 * @return Heuristic value of the node using manhattan method 
	 */
	public double getHeuristic(Node myNode) {
		return myNode.getState().distance(goal);
	}
	
	

	/**
	 * We supply a string label to show up on the user interface.
	 */
	public String toString() {
		return "Greedy Best First Search";
	}
}
