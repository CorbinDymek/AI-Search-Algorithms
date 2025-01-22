package path.gui;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.Border;

import path.PathFinderApp;
import path.agent.AstarAgent;
import path.agent.BreadthFirstSearch;
import path.agent.GhostAgent;
import path.agent.GreedyBestFirstAgent;
import path.agent.PathAgent;
import path.level.Level;

/**
 * An instance of this class hosts our GUI showing the current game level and allowing the operator
 * to interactively specify the start state, goal state, as well as choosing the type of agent with
 * which we experiment.   
 * <p>
 * The special panel (pnlLevel) displays the level, start and goal states, path if available, and 
 * locations that were part of the search tree if available.
 * <p>
 * The operator should click on the level to establish the start state and click again for the goal
 * state and then choose the agent and click on the plan button. 
 * <p>
 * NOTE: We are reusing the swing Shape classes to represent our level, simplifying the rendering
 * and testing for point containment.
 * 
 */
public class MainFrame extends JFrame {
	
	
	private static final long serialVersionUID = 2884588462686235542L;

	private Level theLevel;      // the level geometry.
	
	private PathAgent theAgent;
	
	private Point start;
	private Point goal;
	private List<Point> searchStates;
	
	
	
	private Button btnQuit = new Button("Quit");
	private Button btnPlan = new Button("Plan");
	
	JComboBox<PathAgent> boxAgent = new JComboBox<>();
	JComboBox<String> direction = new JComboBox<>();
	
	
	private JPanel pnlLevel;
	private JPanel pnlButtons;
	
	//public boolean allDirection;
	
	
	
	/**
	 * Our primary constructor configures the frame and prepares our user interactions as well
	 * as populating the user's combobox with a variety of searching agents.
	 * @param lvl
	 */
	public MainFrame(Level lvl) {
		
		super("Path Finder");
		
		this.theLevel = lvl;

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setSize(PathFinderApp.DEFAULT_FRAME_WIDTH,PathFinderApp.DEFAULT_FRAME_HEIGHT);
		this.setLayout(new BorderLayout());
		
		
		/*
		 * Populate the combo box with our variations of agents given the current level
		 */
		boxAgent.addItem(new GhostAgent(theLevel));
		boxAgent.addItem(new BreadthFirstSearch(theLevel));
		boxAgent.addItem(new AstarAgent(theLevel));
		boxAgent.addItem(new GreedyBestFirstAgent(theLevel));
		
		direction.addItem("4 Connected Actions");
		direction.addItem("8 Connected Actions");
		// ...
		//
		
		/*
		 * 
		 * Configure the quit button to exit when clicked.
		 */
		
		
		
		
		btnQuit.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		

		/*
		 * Configure the plan button to incite the agent to plan when 
		 * the button is clicked. 
		 */
		btnPlan.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				doPlanning();
			}
		});

		
		
		
		/*
		 * Now establish frame layout... we need buttons and control widgets in the bottom
		 * panel.
		 */
		pnlButtons = new JPanel();
		
		pnlButtons.add(boxAgent); // combo box for selecting agents
		
		pnlButtons.add(direction); // combo box for selecting number of actions

		pnlButtons.add(btnPlan);	// button to initiate the path finding search
		
		pnlButtons.add(btnQuit);	// button to quit
		
		
		/*
		 * And we need display the world...so we use a panel that
		 * uses our custom paint method.   See the helper method below for details.
		 */
		pnlLevel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				paintTheLevel(g);
			}
		};

		/*
		 * Rig the mouse click on panel to invoke the call back method to set
		 * the start and goal states.
		 */
		pnlLevel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseReleased(MouseEvent e) {
				super.mouseReleased(e);
				
				setStartOrGoal(e.getPoint());
				
			}
			
		});
		
		
		/*
		 * Set style of level panel.
		 */
		pnlLevel.setBackground(Color.LIGHT_GRAY);
		Border bdrLevel = BorderFactory.createStrokeBorder(new BasicStroke(10.0f));
		pnlLevel.setBorder(bdrLevel);
		
		
		/*
		 * Add the panels to the frame.
		 */
		this.add(pnlButtons, BorderLayout.SOUTH);
		this.add(pnlLevel, BorderLayout.CENTER);
		
		
		// TODO Wouldn't it be better to provide information to the user in a small
		// panel at the top of the GUI?
	}

	
	
	
	/**
	 * This call back method is called when the user clicks on the level panel.  We
	 * calculate the nearest point exactly on multiples of the STRIDE constant. 
	 * @param point
	 */
	protected void setStartOrGoal(Point point) {

		/*
		 * Calculate the stride point (closet to stride grid).
		 */
		int sx = (point.x / PathFinderApp.STRIDE) * PathFinderApp.STRIDE;
		int sy = (point.y / PathFinderApp.STRIDE) * PathFinderApp.STRIDE;
		
		point = new Point(sx,sy);
		
		/*
		 * Sometimes we are clicking for the start state.
		 */
		if (start == null || goal != null) {
			start = point;
			goal = null;
		} 
		
		/*
		 * if the start state is already established, we must be clicking for the 
		 * goal state.
		 */
		else 
		{
			goal = point;
		}

		/*
		 * either way, we should clear the agent's path and search tree.
		 */
		if (theAgent != null) {
			this.theAgent.clearPath();
		}
		
		this.repaint();
	}




	/**
	 * The callback method use to render the various elements of the level.
	 * @param g
	 */
	protected void paintTheLevel(Graphics g) {

		Graphics2D gfx = (Graphics2D) g;

		drawObstacles(gfx);
		
		drawStartShapeIfPossible(gfx);
		
		drawGoalShapeIfPossible(gfx);

		drawStateLocationsIfPossible(gfx);
		
		drawAgentPathIfPossible(gfx);
		
		
	}



	/**
	 * Draws the agent's path or nothing if no path is established.
	 * 
	 * @param gfx
	 */
	private void drawAgentPathIfPossible(Graphics2D gfx) {
		
		
		if (theAgent != null && theAgent.getPath() != null) {
			
			
			
			
			Path2D pth = new Path2D.Float();
			pth.moveTo(start.x, start.y);
			
			for (Point p : theAgent.getPath()) {
				pth.lineTo(p.getX(), p.getY());
			}
			
			gfx.setColor(Color.red);
			gfx.setStroke(new BasicStroke(5.0F));
			gfx.draw(pth);
			
		}
		
		
	}



	/**
	 * Draws the goal state shape centered on the goal point
	 * in blue.
	 * Only draws if the goal state exists, otherwise draws nothing.
	 * @param gfx
	 */
	private void drawGoalShapeIfPossible(Graphics2D gfx) {
		/*
		 * display goal point of agent
		 */
		if (goal != null) {
			gfx.setColor(Color.blue);
			gfx.fillOval(goal.x-8, goal.y-8, 16, 16);
		}
	}


	/**
	 * Draws the start shape centered on the start point in green.
	 * Only draws if the start state exists, otherwise draws nothing.
	 * @param gfx
	 */
	private void drawStartShapeIfPossible(Graphics2D gfx) {
		/*
		 * display start point of agent
		 */
		if (start != null) {
			gfx.setColor(Color.green);
			gfx.fillOval(start.x-8, start.y-8, 16, 16);
		}
	}




	/**
	 * If searchStates is available, this method will draw the list
	 * of states that were reached by the search.   This should help
	 * us understand the complexity of the search performed by the 
	 * agent.
	 * <p>
	 * Draws nothing if the searchStates are not available.
	 * 
	 * @param gfx
	 */
	private void drawStateLocationsIfPossible(Graphics2D gfx) {
		
		if (this.searchStates == null) return;
		
		gfx.setColor(Color.yellow);
		for (Point p : this.searchStates) 
				gfx.fillOval(p.x-1, p.y-1, 3, 3);

	}



	/**
	 * A private helper method to draw the obstacle zones in the level.
	 * 
	 * @param gfx
	 */
	private void drawObstacles(Graphics2D gfx) {
		/*
		 * display each obstacle zone
		 */
		gfx.setColor(Color.black);
		for (Shape s : theLevel.obstacles()) {
			gfx.fill(s);
		}
	}




	/**
	 * Callback method called when the user clicks on the plan button.  We
	 * assert that all dependencies are satisfied (start and goal are established)
	 * and then configure the selected agent from the user's combobox with all
	 * the information needed to perform the search.    
	 */
	protected void doPlanning() {

		if (start == null) return;
		if (goal == null) return;
		
		
		theAgent = (PathAgent) this.boxAgent.getSelectedItem();
		
		Object numberOfActions = this.direction.getSelectedItem();
		
		//gets state of direction combo box and sets allDirection to true if 8-connected actions is selected
		//sets it to false otherwise (4 conneced actions is selected)
		if(numberOfActions.equals("8 Connected Actions")) {
			theAgent.setAllDirection(true);
		}
		else {
			theAgent.setAllDirection(false);
		}
		
		theAgent.clearPath();
		theAgent.setStart(start);
		theAgent.setGoal(goal);

		System.err.println("start planning...");
		
		theAgent.findPath();
		
		
		System.err.println("...back from planning");
		
		
		

		/*
		 * Now output search metrics
		 */
		this.searchStates = theAgent.searchTreeStates();

		if (this.searchStates != null) { 
			System.out.println("Tree Size: "+ this.searchStates.size());
		}

		// TODO the student will add more (to console or to GUI)
		System.out.println("Length of Path: " + theAgent.findPath().size());
		System.out.println("Height of Search Tree: "+theAgent.searchTreeHeight());
		
		this.repaint();
		
		
	}





	
	
	

	public Level getTheLevel() {
		return theLevel;
	}

	public void setTheLevel(Level theLevel) {
		this.theLevel = theLevel;
	}
	
	
	
	
}
