package path.agent;

import java.awt.Point;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import path.agent.heuristic.Heuristic;
import path.level.Level;


/**
 * All search agents for this program will extend this class.  The agents will be aware (have 
 * a handle on the level) as well as the start state and goal state.  Here the states are way points
 * in the level geometry.   After fully configuring the agent, we can ask the agent to plan, which
 * results in a path and a search tree rooted at root.
 * <p>
 * After an agent plans, we will retain the tree rooted by the root handle and the path so we can
 * interrogate the agent about the results of search.
 * <p>
 * NOTE:  The programmer must clear/reset the agent to return to initial configuration/before planning.
 * 
 */
public abstract class PathAgent {

	protected Level level;   // just in case we need to ask the level for information

	protected Point start;   // starting point established by operator
	
	protected Point goal;	 // ending point established by operator

	protected List<Point> path = null;   // a path resulting from planning; null means path not available

	protected Node root;   // a handle on the resulting search tree after planning.  null means no plan yet.
	
	protected boolean allDirection; // handle on state of connected actions combo box in MainFrame


	/**
	 * All agents are born with a knowledge of the current level...for convenience.  May not
	 * be needed by some agents.
	 * 
	 * @param lvl
	 */
	public PathAgent(Level lvl) {
		this.level = lvl;
	}

	public Point getStart() {
		return start;
	}

	public void setStart(Point start) {
		this.start = start;
	}

	public Point getGoal() {
		return goal;
	}

	public void setGoal(Point goal) {
		this.goal = goal;
	}

	public List<Point> getPath() {
		return path;
	}
	

	/**
	 * Implements the search, building the resulting search tree and
	 * establishing the path (see setter method) as well as returning 
	 * the path (as a programmer's convenience).
	 * 
	 * @return
	 */
	public abstract List<Point> findPath();
	
	
	/**
	 * Walks back up the search tree from the specified search node providing the list
	 * of states (ie, way points) that will be traveled along this path.  NOTE: this is not
	 * the list of actions to take.  This is the list of states the define the path.  
	 * 
	 * @param current the current node (presumably the goal node)
	 * @return a list of points to travel to get to the goal from the start of search
	 */
	public List<Point> pathFromNode(Node current) {

		List<Point> pth = new LinkedList<>();

		while (current != null) {
			pth.add(current.getState());
			current = current.getParent();
		}

		Collections.reverse(pth);

		return pth;
	}

	
	/**
	 * Return the states of the entire search tree. 
	 * 
	 * @return collection of states/points reached or null if no tree there.
	 */
	public List<Point> searchTreeStates()  {
		
		// TODO  complete this algorithm to present a collection of points
		//       that have been reached by the search or zero if there is no 
		//       search tree.
		
		if(root!=null) { //check if root node is not null
			List<Point> states = new LinkedList<>(); //initialize a list to store the states of nodes

		    LinkedList<Node> queue = new LinkedList<>(); //initialize a queue to store nodes for BFS
		    queue.add(root); //add the root node to the queue 

		    while (!queue.isEmpty()) { //continue while there are nodes to process in the queue 
		        Node current = queue.poll(); //remove and get the front node from the queue 

		        states.add(current.getState()); //add the state of the current node to the states list
		        //if the current node has children, add them to the queue for processing 
		        if (current.getChildren() != null) {
		            queue.addAll(current.getChildren()); //add all children to the queue
		        }
		    }

		    return states; //return the list of states after all nodes have been processed

		}
		
		
		return null; 
	}

	
	/**
	 * Returns the depth of the search tree or -1 if the tree
	 * does not exist.
	 * 
	 * @return the depth of the tree
	 */
	public int searchTreeDepth() {
		
		if(root !=null) { //if root is not null run helper function
			return myhelper(root);
		}
		
		return -1; //root is null so it returns -1
	}

	/**
	 *Helper method used by searchTreeDepth, that performs a BSF to look through all the nodes
	 * @param myNode
	 * @return the depth of the search tree
	 */
	public int myhelper(Node myNode) {
		//if the node has no children return 0 (leaf node)
		if(myNode.getChildren()== null || myNode.getChildren().isEmpty()) {
			return 0;
		}
		int depth = 0; //initialize depth as 0, this stores max depth found
		
		//loop through all children of the current node
		for(Node children: myNode.getChildren()) {
			int childDepth = myhelper(children); //recursively calculate the depth of the child
			if(childDepth>depth) { //if child's depth is greater than the current max depth
				depth = childDepth; //update the depth of the child's depth
			}
		}
		return depth+1; //return the depth of the current node which is max depth of its children + 1
	}
	
	/**
	 * Calls getHeight() if the root is not null, returns -1 if root is null
	 * @return the height of the tree and -1 if root is null
	 */
	public int searchTreeHeight() {
	    if (root != null) { //if root is not null run getHeight function
	        return getHeight(root);
	    }
	    return -1; //root is null so return -1
	}

	/**
	 * Helper method that performs a BSF through the tree and returns the height of the tree starting at a certain node
	 * @param node
	 * @return the height of the tree from the current node
	 */
	public int getHeight(Node node) {
	    if (node == null) { //if node is null, return -1
	        return -1; 
	    }
	    
	    //if the node has no children, the heigh is 0 (leaf nodes)
	    if (node.getChildren() == null || node.getChildren().isEmpty()) {
	        return 0;
	    }
	    
	    int maxChildHeight = -1; //initialize the max child height to -1 since no children are processed yet
	    
	    //loop through all children of the current node to determine the max height
	    for (Node child : node.getChildren()) {
	        int childHeight = getHeight(child); //recursively get the height of the child
	        maxChildHeight = Math.max(maxChildHeight, childHeight); //update the max height if needed
	    }
	    
	    return maxChildHeight+1; //add 1 to the max child height to account for the current node's height
	}
	
	/**
	 * Returns to a pre-search state in which no path is known and
	 * no search tree exists.
	 */
	public void clearPath() {
		
		this.root = null;
		this.path = null;
		
	}

	public Level getLevel() {
		return level;
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Node getRoot() {
		return root;
	}
	
	/**
	 * Gets the state of the combo box from the main frame and sets allDirection
	 * @param myAllDirection
	 */
	public void setAllDirection(boolean myAllDirection) {
		allDirection = myAllDirection;
	}



}
