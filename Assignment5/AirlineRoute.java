public class AirlineRoute implements Comparable<AirlineRoute> {
	
	private int origin, destination;			// Vertices of the graph
	private int distance;					// Distance of the edge
	private double cost;					// The cost to fly between vertices
	private boolean reversed;				// Whether this edge is the original from the text file or if it is reversed
	
	
	/**
	 * Creates a new airline route graph edge
	 * @param org The origin vertex
	 * @param dest The destination vertex
	 * @param dist The distance between vertices
	 * @param price The cost to travel between vertices
	 * @param dir Whether the edge is reversed or not
	 */
	public AirlineRoute(int org, int dest, int dist, double price, boolean dir) {
		
		setOrigin(org);
		setDestination(dest);
		setDistance(dist);
		setCost(price);
		setDirection(dir);
	}
	
	
	/**
	 * Gets the origin vertex
	 * @return The origin vertex
	 */
	public int getOrigin() {
		
		return origin;
	}
	
	
	/**
	 * Gets the destination vertex
	 * @return The destination vertex
	 */
	public int getDestination() {
		
		return destination;
	}
	
	
	/**
	 * Gets the distance between the origin and destination vertices
	 * @return The distance between vertices
	 */
	public int getDistance() {
		
		return distance;
	}
	
	
	/**
	 * Gets the cost to travel between the origin and destination vertices
	 * @return The cost to fly between vertices
	 */
	public double getCost() {
		
		return cost;
	}
	
	
	/**
	 * Gets whether the edge is reversed or not (in relation to the input file)
	 * @return A boolean representing reversal
	 */
	public boolean getDirection() {
		
		return reversed;
	}
	
	
	/**
	 * Compares the origin and destination vertices between two edges without regard to other
	 * fields.  If they are the same vertices, returns 0.  Otherwise, returns 1.
	 */
	public int compareTo(AirlineRoute airRt) {
		
		if (airRt == null) {
			throw new NullPointerException("The airline route being compared to is null.\n");
		}
		
		if (getOrigin() == airRt.getOrigin() && getDestination() == airRt.getDestination()) {
			return 0;				// Returns 0 if the origin and destination vertices match
		}
		else {
			return 1;			// Otherwise, returns 1
		}
	}
	
	
	/**
	 * Sets the origin vertex
	 * @param org The origin vertex
	 */
	private void setOrigin(int org) {
		
		origin = org;
	}
	
	
	/**
	 * Sets the destination vertex
	 * @param dest The destination vertex
	 */
	private void setDestination(int dest) {
		
		destination = dest;
	}
	
	
	/**
	 * Sets the distance of the edge between vertices
	 * @param dist The edge distance
	 */
	private void setDistance(int dist) {
		
		distance = dist;
	}
	
	
	/**
	 * Sets the cost to fly between vertices
	 * @param price The cost of the flight
	 */
	private void setCost(double price) {
		
		cost = price;
	}
	
	
	/**
	 * Sets the reversal orientation
	 * @param dir A boolean denoting if the route is reversed or not
	 */
	private void setDirection(boolean dir) {
		
		reversed = dir;
	}
}