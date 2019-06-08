

public class PreOrderAirline {
	
	private CustomQueue<Integer> stackPaths;			// A queue that acts as a stack for storing paths
	private boolean[] visited;							// Tracks which vertices have been visited
	private double runningCost;						// Stores the current sum of costs
	
	
	/**
	 * Creates a new data container with the visited array all set to false
	 * @param numCities
	 */
	public PreOrderAirline(int numCities) {
		
		stackPaths = new CustomQueue<Integer>();
		visited = new boolean[numCities];
		runningCost = 0.0;
		
		for (int i = 0; i < numCities; i++) {			// Sets all indices in the array to false
			visited[i] = false;
		}
	}
	
	
	/**
	 * Gets the stack of paths from the data container
	 * @return The stack of paths
	 */
	public CustomQueue<Integer> getPaths() {
		
		return stackPaths;
	}
	
	
	/**
	 * Checks whether the specified index has been visited
	 * @param index The index to check
	 * @return A boolean stating whether the index has been visited
	 */
	public boolean isVisited(int index) {
		
		return visited[index];
	}
	
	
	/**
	 * Gets the current sum of costs
	 * @return The sum of costs so far
	 */
	public double getRunningCost() {
		
		return runningCost;
	}
	
	
	/**
	 * Adds a vertex to the top of the stack
	 * @param index The vertex to add
	 */
	public void addPath(int index) {
		
		stackPaths.addToBackOfLine(index);
	}
	
	
	/**
	 * Pops a vertex off the top of the stack
	 * @return The vertex that was popped
	 */
	public int removeLastPath() {
		
		return stackPaths.popBackOfLine();
	}
	
	
	/**
	 * Checks if the stack is empty
	 * @return A boolean denoting if the stack is empty
	 */
	public boolean emptyPaths() {
		
		return stackPaths.isEmpty();
	}
	
	
	/**
	 * Sets the vertex as visited
	 * @param index The vertex to set
	 */
	public void setVisited(int index) {
		
		visited[index] = true;
	}
	
	
	/**
	 * Sets the vertex as unvisited
	 * @param index The vertex to set
	 */
	public void setUnvisited(int index) {
		
		visited[index] = false;
	}
	
	
	/**
	 * Adds to the current sum of costs
	 * @param cost The cost to add
	 */
	public void addToCost(double cost) {
		
		runningCost += cost;
	}
	
	
	/**
	 * Subtracts from the current sum of costs
	 * @param cost The cost to subtract
	 */
	public void removeFromCost(double cost) {
		
		runningCost -= cost;
	}
}