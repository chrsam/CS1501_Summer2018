public class CustomLinkedList<T extends Comparable<T>> {
	
	private CustomNode<T> listHead;			// The linked list head
	private int size;					// The linked list size
	
	
	/**
	 * Creates a new, empty linked list
	 */
	public CustomLinkedList() {
		
		setListHead(new CustomNode<T>());			// Sets the list head as a new node with a value of null
		size = 0;
	}
	
	
	/**
	 * Creates a new linked list with the list head set to an object
	 * @param obj The object to set as the linked list head
	 */
	public CustomLinkedList(T obj) {
		
		setListHead(new CustomNode<T>(obj));			// Sets the list head as a new node with a value of the object
		size = 1;
	}
	
	
	/**
	 * Gets the list head of the linked list
	 * @return
	 */
	public CustomNode<T> getListHead() {
		
		return listHead;
	}
	
	
	/**
	 * Sets the list head to a specified node
	 * @param node A CustomNode object
	 */
	protected void setListHead(CustomNode<T> node) {
		
		listHead = node;
	}
	
	
	/**
	 * Adds an object to the linked list by inserting it into the beginning
	 * @param obj The object to insert
	 */
	public void add(T obj) {
		
		CustomNode<T> oldHead = getListHead();				// Gets the current list head node
		CustomNode<T> newHead = new CustomNode<T>(obj);		// Creates a new node using the object parameter
		setListHead(newHead);							// Sets the list head as the newly created node
		newHead.setNextNode(oldHead);					// Sets the next node of the new list head as the old list head node
		oldHead.setLastNode(newHead);					// Sets the last node of the old list head as the new list head node
		size++;									// Increments the size of the list
	}
	
	
	/**
	 * Checks the linked list to see if it contains the parameter object
	 * @param obj The object to search for
	 * @return A boolean stating if the linked list contains the object
	 */
	public boolean contains(T obj) {
		
		CustomNode<T> node = getListHead();
		
		while (node.hasData()) {						// Iterates through the linked list
			if (node.getData().compareTo(obj) == 0) {		// Returns true if the parameter object is found
				return true;
			}
			
			node = node.getNextNode();
		}
		
		return false;			// Otherwise, returns false
	}
	
	
	/**
	 * Removes a node containing the object from the linked list that matches the parameter object
	 * @param obj The object to remove
	 * @return The object that was removed
	 */
	public T remove(T obj) {
		
		CustomNode<T> node = getListHead();
		
		while (node.hasData()) {							// Iterates through the linked list
			if (node.getData().compareTo(obj) == 0) {			// Conditional if the object to remove is found
				if (node.hasNextNode() && node.getLastNode() != null) {		// Conditional where the node is in the middle of the list
					node.getNextNode().setLastNode(node.getLastNode());		// Removes next and last references to the node
					node.getLastNode().setNextNode(node.getNextNode());
					size--;
					return node.getData();				// Returns the object of the removed node				
				}
				else if (node.hasNextNode() && node.getLastNode() == null) {		// Conditional where the node is the head and the list size is > 1
					node.getNextNode().setLastNode(null);
					setListHead(node.getNextNode());
					size--;
					return node.getData();
				}
				else if (!node.hasNextNode() && node.getLastNode() != null) {		// Conditional where the node is the tail
					node.getLastNode().setNextNode(null);
					size--;
					return node.getData();
				}
				else {									// Otherwise, the node is the only entry in the list
					setListHead(new CustomNode<T>());
					size--;
					return node.getData();
				}
			}
			
			node = node.getNextNode();
		}
		
		return null;			// Return null if nothing was removed
	}
	
	
	/**
	 * Gets the size of the linked list
	 * @return An int representing the size of the list
	 */
	public int size() {
		
		return size;
	}
	
	
	/**
	 * Returns a string representation of the linked list
	 */
	public String toString() {
		
		CustomNode<T> node = getListHead();				// Gets the linked list head node
		String str = "";						// Creates a new empty string
		
		while (node.hasData()) {
			str += (node.getData().toString() + "\n");		// Concatenates the string with the string representation of the node's data
			node = node.getNextNode();						// Gets the next node in the linked list
		}
		
		return str;
	}
}