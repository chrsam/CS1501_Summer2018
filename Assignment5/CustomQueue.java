public class CustomQueue<T extends Comparable<T>> {
	
	private CustomNode<T> frontOfLine;			// The front of the queue
	private CustomNode<T> backOfLine;			// The back of the queue
	protected int size;							// The size of the queue
	
	
	/**
	 * Creates a new, empty queue
	 */
	public CustomQueue() {
		
		CustomNode<T> node = new CustomNode<T>();
		
		setFrontOfLine(node);
		setBackOfLine(node);
		size = 0;
	}
	
	
	/**
	 * Creates a new queue with an initial object
	 * @param obj The first object to place in the queue
	 */
	public CustomQueue(T obj) {
		
		CustomNode<T> node = new CustomNode<T>(obj);		// Creates a new node with the object parameter
		
		setFrontOfLine(node);
		setBackOfLine(node);
		size = 1;
	}
	
	
	/**
	 * Gets the node in the front of the queue
	 * @return The first node in the queue
	 */
	public CustomNode<T> getFrontOfLine() {
		
		return frontOfLine;
	}
	
	
	/**
	 * Gets the node in the back of the queue
	 * @return The last node in the queue
	 */
	public CustomNode<T> getBackOfLine() {
		
		return backOfLine;
	}
	
	
	/**
	 * Gets the next object in the front of the queue and removes it
	 * @return The first object in the queue
	 */
	public T popNextInLine() {
		
		if (isEmpty()) {		// Returns null if the queue is empty
			return null;
		}
		
		CustomNode<T> node = frontOfLine;
		
		setFrontOfLine(frontOfLine.getNextNode());		// Sets the first in line reference to the next node
		size--;
		
		return node.getData();			// Returns the previous first in line object
	}
	
	
	/**
	 * Gets the object in the back of the queue to enable stack functionality
	 * @return The last object in the queue
	 */
	public T popBackOfLine() {
		
		if (isEmpty()) {
			return null;
		}
		
		CustomNode<T> node = backOfLine;
		
		setBackOfLine(backOfLine.getLastNode());
		size--;
		
		return node.getData();
	}
	
	
	/**
	 * Adds a new object to the back of the queue
	 * @param obj The object to add
	 */
	public void addToBackOfLine(T obj) {
		
		CustomNode<T> node = new CustomNode<T>(obj);		// Creates a new node from the object parameter
		
		if (isEmpty()) {
			setFrontOfLine(node);
			setBackOfLine(node);
		}
		else {
			backOfLine.setNextNode(node);				// Adds the node to the back of the queue
			node.setLastNode(backOfLine);
			setBackOfLine(node);
		}
		
		size++;
	}
	
	
	/**
	 * Checks the linked list to see if it contains the parameter object
	 * @param obj The object to search for
	 * @return A boolean stating if the linked list contains the object
	 */
	public boolean contains(T obj) {
		
		CustomNode<T> node = frontOfLine;
		
		while (node.hasData()) {						// Iterates through the queue
			if (node.getData().compareTo(obj) == 0) {		// Returns true if the parameter object is found
				return true;
			}
			
			node = node.getNextNode();
		}
		
		return false;			// Otherwise, returns false
	}
	
	
	/**
	 * Checks if the queue is empty
	 * @return True if the queue is empty, false otherwise
	 */
	public boolean isEmpty() {
		
		if (size == 0) {
			return true;
		}
		else {
			return false;
		}
	}
	
	
	/**
	 * Gets the number of objects in the queue
	 * @return The size of the queue
	 */
	public int getSize() {
		
		return size;
	}
	
	
	/**
	 * Sets the front of the line
	 * @param node The node to establish as the front
	 */
	private void setFrontOfLine(CustomNode<T> node) {
		
		frontOfLine = node;
	}
	
	
	/**
	 * Sets the back of the line
	 * @param node The node to establish as the back
	 */
	protected void setBackOfLine(CustomNode<T> node) {
		
		backOfLine = node;
	}
}