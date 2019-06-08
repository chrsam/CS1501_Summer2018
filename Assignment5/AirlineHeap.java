public class AirlineHeap {
	
	private AirlineRoute[] heap;					// AirlineRoute objects are stored in an array
	private int nextLeaf;				// Determines where the next entry is added to the heap
	
	
	/**
	 * Creates a new non-indexable AirlineRoute heap
	 */
	public AirlineHeap() {
		
		heap = new AirlineRoute[32];
		nextLeaf = 0;
	}
	
	
	/**
	 * Gets the route of highest priority
	 * @return The route of the highest priority (lowest distance)
	 */
	public AirlineRoute getMin() {
		
		return heap[0];
	}
	
	
	/**
	 * Adds a route to the heap; floats the route if necessary
	 * @param route The AirlineRoute object to add to the heap
	 */
	public void add(AirlineRoute route) {
		
		if (nextLeaf == heap.length) {			// Resizes the array if there is no room available
			heap = resizeHeap(heap);
		}
		
		heap[nextLeaf] = route;				// Adds the route to the next available leaf position
		floatRoute();							// Floats the route (if necessary) in the heap
		nextLeaf++;							// Increments the index of the next available leaf position
	}
	
	
	/**
	 * Removes the minimum route from the heap
	 * @return The minimum route that was removed
	 */
	public AirlineRoute popMin() {
		
		if (nextLeaf == 0) {
			return null;
		}
		
		nextLeaf--;							// Decrements the index of the next available leaf position
		
		AirlineRoute minRoute = heap[0];
		heap[0] = heap[nextLeaf];			// Overwrites the route with the highest priority
		heap[nextLeaf] = null;				// Sets the last leaf to null
			
		sinkRoute(0);					// Sinks the route (if necessary)
		
		return minRoute;
	}
	
	
	/**
	 * Checks to see if the heap is empty
	 * @return A boolean representing the state of the heap
	 */
	public boolean isEmpty() {
		
		return heap[0] == null;
	}
	
	
	/**
	 * Calculates the parentIndex before calling the recursive float function
	 */
	private void floatRoute() {
		
		int parentIndex = (int)Math.floor((nextLeaf - 1) / 2.0);			// Calculates the parent index using the leaf index
		
		floatRecurse(parentIndex, nextLeaf);			// Calls the recursive float function
	}
	
	
	/**
	 * Floats a route up the heap
	 * @param parentIndex The index of the AirlineRoute object's parent
	 * @param insertedIndex The index of the current AirlineRoute object
	 */
	private void floatRecurse(int parentIndex, int insertedIndex) {
		
		if (parentIndex < 0) {					// If the parent index is less than zero, then the current route is located at the root
			return;
		}
		
		AirlineRoute parentRoute = heap[parentIndex];
		AirlineRoute insertedRoute = heap[insertedIndex];
		
		if (parentRoute.getDistance() > insertedRoute.getDistance()) {			// Swaps the parent and current routes if the parent's distance is greater than the child's
			AirlineRoute tempRoute = heap[parentIndex];
			heap[parentIndex] = heap[insertedIndex];			
			heap[insertedIndex] = tempRoute;
			
			insertedIndex = parentIndex;								// Sets the current route's index as the parent index
			parentIndex = (int)Math.floor((parentIndex - 1) / 2.0);		// Calculates a new parent index from the current route's new index
			
			floatRecurse(parentIndex, insertedIndex);				// Recursively calls the float method
		}
	}
	
	
	/**
	 * Sinks a route down the heap
	 * @param parentIndex The parent AirlineRoute object's index
	 */
	private void sinkRoute(int parentIndex) {
		
		int leftIndex = 2 * parentIndex + 1;		// Calculates the left child's index
		int rightIndex = 2 * parentIndex + 2;		// Calculates the right child's index
		int comparedIndex;						// The index to eventually compare to the parent
		AirlineRoute leftRoute, rightRoute;					// Routes of the left and right children
		
		if (leftIndex > heap.length - 1) {		// Sets the left child to null if the index exceeds the heap size
			leftRoute = null;
		}
		else {
			leftRoute = heap[leftIndex];			// Otherwise, retrieves the left child route
		}
		
		if (rightIndex > heap.length - 1) {		// Sets the right child to null if the index exceeds the heap size
			rightRoute = null;
		}
		else {
			rightRoute = heap[rightIndex];		// Otherwise, retrieves the right child route
		}
		
		if (leftRoute == null && rightRoute == null) {			// Conditional if both children are null
			return;
		}
		else if (leftRoute != null && rightRoute == null) {		// Conditional if only the right child is null
			comparedIndex = leftIndex;				// Sets the compared index to the left child index
		}
		else if (leftRoute == null && rightRoute != null) {		// Conditional if only the left child is null (contingency; shouldn't happen normally)
			comparedIndex = rightIndex;				// Sets the compared index to the right child index
		}
		else if (leftRoute.getDistance() <= rightRoute.getDistance()) {		// Sets the compared index to the left child if its distance is less than or equal to the right child's
			comparedIndex = leftIndex;
		}
		else {
				comparedIndex = rightIndex;			// Otherwise, sets the compared index to the right child index
		}
		
		AirlineRoute parentRoute = heap[parentIndex];			// Gets the parent and compared child routes
		AirlineRoute childRoute = heap[comparedIndex];
		
		if (parentRoute.getDistance() > childRoute.getDistance()) {			// Swaps routes if the parent's distance is greater than the child's
			AirlineRoute tempRoute = heap[comparedIndex];
			heap[comparedIndex] = heap[parentIndex];
			heap[parentIndex] = tempRoute;
			
			sinkRoute(comparedIndex);						// Recursively calls the sink method
		}
	}
	
	
	/**
	 * Resizes the heap's array its maximum size has been exceeded
	 * @param oldHeap The current array that has not been resized
	 * @return The new array of doubled size
	 */
	private AirlineRoute[] resizeHeap(AirlineRoute[] oldHeap) {
		
		int doubledSize = oldHeap.length * 2;
		AirlineRoute[] newHeap = new AirlineRoute[doubledSize];		// Creates a new array by doubling the size of the old array
		
		for (int i = 0; i < oldHeap.length; i++) {			// Copies all objects from the old array to the new array
			newHeap[i] = oldHeap[i];
		}
		
		return newHeap;
	}
}