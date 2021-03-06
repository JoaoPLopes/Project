package simulator;

 
/**
 * Interface of a graph traverser. A graph traverser can move through a graph an store 
 * the path taken as well as the current node;
 * 
 * @author Joao Lopes
 *
 */

public interface Traverser {
	
	
	/**
	 * move the traverser from the current node to the next node
	 *
	 */
	public void move();
	/**
	 * Returns the node where the traverser is
	 * @return current node
	 */
	/**
	 * Returns a list containing all the visited nodes by the traverser
	 * @return visited nodes
	 */
}
