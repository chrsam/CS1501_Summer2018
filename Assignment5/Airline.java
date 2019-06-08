// CS 1501: Assignment 5
// @author Chris Mananghaya (cam314)

import java.util.Scanner;
import java.util.StringTokenizer;
import java.io.*;

public class Airline {
	
	private static CustomLinkedList<AirlineRoute>[] airlineList;			// Adjacency list of airline routes
	private static String[] cities;									// Array that matches vertex numbers to city names
	private static int numCities = 0;
	private static String filename;
	private static File file;
	private static boolean modified = false;							// Tracks if changes were made to the file
	private static Scanner parseFile, input = new Scanner(System.in);
	
	public static void main(String[] args) throws IOException {
		
		System.out.printf("\n0====================================0\n"
				+ "|  Skyline International Airways |\n"
				+ "0====================================0\n\n");
				
		System.out.printf("Welcome to Skyline International Airways!\n\n");
		System.out.printf("Please enter the file containing airline data: ");
		try {
			filename = input.nextLine();
			
			if (filename.length() == 0) {
				throw new IllegalArgumentException("\nNo file name was entered.  Exiting the program.\n");
			}
			
			if (!filename.endsWith(".txt")) {			// Appends the filename with ".txt" if it is not present
				filename = filename.concat(".txt");
			}
			
			file = new File(filename);
			parseFile = new Scanner(file);				// Opens the file for manipulation
		}
		catch (FileNotFoundException fnfe) {
			System.out.printf("\nThe requested file \"%s\" was not found.  Exiting the program.\n", filename);
			System.exit(1);
		}
		System.out.printf("\n");
		
		initializeGraph();				// Reads the file and generates the adjacency list for the graph
		parseFile.close();				// Closes the file stream
		displayMenu();
		
		do {					// Loops until the user enters the exit option
			System.out.printf("Enter \"M\" to show the menu.\nSelect an option: ");
			String menuOption = input.nextLine();							// Gets input from the user
			System.out.printf("\n");
			
			switch (menuOption) {
				case "1":						// If the input is "1", displays all direct routes
					getDirectFlights();
					break;
				case "2":						// If the input is "2", displays the minimum spanning tree
					getMinimumSpanningTree();
					break;
				case "3":						// If the input is "3", displays the shortest path tree by distance
					getShortestPath(1);
					break;
				case "4":						// If the input is "4", displays the shortest path tree by cost
					getShortestPath(0);
					break;
				case "5":						// If the input is "5", displays the shortest path tree by number of hops
					getShortestUnweighted();
					break;
				case "6":						// If the input is "6", displays all trips less than or equal to a maximum cost
					getFlightsOfCost();
					break;
				case "7":						// If the input is "7", adds a new route to the airline schedule
					addNewRoute();
					break;
				case "8":						// If the input is "8", removes a route from the airline schedule
					removeRoute();
					break;
				case "q":						// If the input is "q" or "Q", exits the program
				case "Q":
					exitAirline();				// Determines if a new file needs to be written
					System.exit(0);
				case "m":
				case "M":						// If the input is "m" or "M", prints the menu
					displayMenu();
					break;
				default:								// Otherwise, an invalid command was entered
					System.out.printf("The command entered could not be recognized.  Please enter a valid integer.\n\n");
					break;
			}
		} while (true);
	}
	
	
	/**
	 * Initializes and generates the adjacency list representing the graph of airline routes
	 */
	@SuppressWarnings("unchecked")
	private static void initializeGraph() {
		
		StringTokenizer tokenizer;					// Tokenizes each line in the file
		String parseLine;
		
		parseLine = parseFile.nextLine();
		numCities = Integer.parseInt(parseLine);				// Gets the number of cities visited by the airline
		cities = new String[numCities];							// Initializes the array to the number of cities
		airlineList = (CustomLinkedList<AirlineRoute>[])new CustomLinkedList[numCities];		// Initializes the adjacency list
		
		for (int i = 0; i < numCities; i++) {			// Creates the array of vertex number to city name
			cities[i] = parseFile.nextLine();
		}
		
		while (parseFile.hasNextLine()) {								// Loops while the file has edges to add
			tokenizer = new StringTokenizer(parseFile.nextLine());
			int origin = Integer.parseInt(tokenizer.nextToken());
			int destination = Integer.parseInt(tokenizer.nextToken());
			int edgeWeight = Integer.parseInt(tokenizer.nextToken());
			double flightCost = Double.parseDouble(tokenizer.nextToken());
			
			AirlineRoute flight = new AirlineRoute(origin, destination, edgeWeight, flightCost, false);
			AirlineRoute reversed = new AirlineRoute(destination, origin, edgeWeight, flightCost, true);
															// Creates a new airline route (graph edge) from the delimited line
			addFlight(origin, flight);					// Adds the route to the adjacency list
			addFlight(destination, reversed);
		}
	}
	
	
	/**
	 * Adds an airline route to the adjacency list
	 * @param vertex The index to place the AirlineRoute object in the array
	 * @param flight The AirlineRoute object
	 */
	private static void addFlight(int vertex, AirlineRoute flight) {
		
		if (airlineList[vertex - 1] == null) {				// Creates a new linked list if one does not exist in the array
			airlineList[vertex - 1] = new CustomLinkedList<AirlineRoute>();
		}
		
		airlineList[vertex - 1].add(flight);				// Adds the AirlineRoute object to the vertex's linked list
	}
	
	
	/**
	 * Gets the city name from the vertex number
	 * @param vertex The array index number + 1
	 * @return The name of the city associated with the vertex
	 */
	private static String getCity(int vertex) {
		
		return cities[vertex - 1];
	}
	
	
	/**
	 * Gets the index number from a city name.  Returns -1 if no match was found.
	 * @param city The name of the city
	 * @return The index of the city in the adjacency list
	 */
	private static int getIndex(String city) {
		
		for (int i = 0; i < cities.length; i++) {			// Simple brute force search with a runtime of Theta(n)
			if (cities[i].equalsIgnoreCase(city)) {			// String matching is case insensitive
				return i;
			}
		}
		
		return -1;				// Returns -1 if no match was found
	}
	
	
	/**
	 * Iterates through each linked list in the adjacency list to print out all direct flights
	 */
	private static void getDirectFlights() {
		
		System.out.printf("0==========================0\n"
				+ "| Listing of Direct Routes |\n"
				+ "0==========================0\n");
		
		for (int i = 0; i < airlineList.length; i++) {				// Iterate through the entire array
			CustomLinkedList<AirlineRoute> list = airlineList[i];
			
			if (list == null) {				// Continues if no linked list exists
				continue;
			}
			CustomNode<AirlineRoute> node = list.getListHead();
			
			while (node.hasData()) {					// Iterates through the entire linked list
				AirlineRoute route = node.getData();
				if (!route.getDirection()) {
					System.out.printf("%s <--> %s: %,d mi., $%,.2f\n", getCity(route.getOrigin()),
							getCity(route.getDestination()), route.getDistance(), route.getCost());
				}
				node = node.getNextNode();
			}
		}
		System.out.printf("\n");
	}
	
	
	/**
	 * Retrieves and prints the minimum spanning tree; creates one if no such tree exists
	 */
	private static void getMinimumSpanningTree() {
		
		AirlineRoute[] minSpanTree = buildMinimumSpanningTree();			// Create a minimum spanning tree if none exists
		
		System.out.printf("0===================================0\n"
				+ "| Minimum Spanning Tree by Distance |\n"
				+ "0===================================0\n");
		
		boolean disconnected = false;
		
		for (int i = 0; i < minSpanTree.length; i++) {		// Iterates through the array containing the graph edges
			AirlineRoute route = minSpanTree[i];
			if (route == null) {					// Notifies if a gap occurs (I.e., the graph is disconnected)
				disconnected = true;
				continue;
			}
			System.out.printf("%s <--> %s: %,d mi.\n", getCity(route.getOrigin()),
					getCity(route.getDestination()), route.getDistance());
		}
		if (disconnected) {							// Displays a message if the graph is disconnected
			System.out.printf("A gap exists in the minimum spanning tree.  The graph is disconnected.\n");
		}
		System.out.printf("\n");
	}
	
	
	/**
	 * Builds the minimum spanning tree from the current graph data using lazy Prim's algorithm
	 */
	private static AirlineRoute[] buildMinimumSpanningTree() {
		
		AirlineRoute[] minSpanTree = new AirlineRoute[numCities - 1];			// The minimum spanning tree has (vertices - 1) edges
		boolean[] visitedIndices = new boolean[numCities];			// Tracks which vertices have been visited
		AirlineHeap minPQ = new AirlineHeap();		// Creates a minimum priority queue for route distances
		int numVisited = 1;
		int initialIndex = 0;
		
		for (int i = 0; i < numCities; i++) {				// Sets all visited vertices to false
			visitedIndices[i] = false;
		}
		
		visitedIndices[0] = true;					// Sets the first vertex as visited
		CustomNode<AirlineRoute> node = airlineList[initialIndex].getListHead();
		
		while (node.hasData()) {					// Adds all neighbors of the vertex to the priority queue
			AirlineRoute route = node.getData();
			minPQ.add(route);
			node = node.getNextNode();
		}
		
		while (numVisited != numCities) {				// Loops while there exists an unvisited vertex
			if (minPQ.isEmpty()) {
				break;				// Early exit if the priority queue is empty before all cities are visited
			}
			
			AirlineRoute minRoute = minPQ.popMin();					// Gets the minimum edge from the priority queue
			int destVertex = minRoute.getDestination();				// Gets the destination from the route
			
			if (!visitedIndices[destVertex - 1]) {			// Conditional if the destination is unvisited
				minSpanTree[numVisited - 1] = minRoute;				// Adds the edge to the minimum spanning tree
				visitedIndices[destVertex - 1] = true;				// Sets the vertex as visited
				numVisited++;
				
				node = airlineList[destVertex - 1].getListHead();
				
				while (node.hasData()) {					// Adds all neighbors of the newly visited vertex to the priority queue
					AirlineRoute route = node.getData();
					minPQ.add(route);
					node = node.getNextNode();
				}
			}
		}
		
		return minSpanTree;
	}
	
	
	/**
	 * Builds a shortest path tree of the entered starting and destination cities by either distance
	 * or cost.  Per requirements, cities are entered using their NAME, not their vertex number.
	 * @param mode Determines if the shortest path is based on distance or cost
	 */
	private static void getShortestPath(int mode) {
		
		String startCity, endCity;
		int startIndex, endIndex;					// Index locations of the entered cities
		
		System.out.printf("Enter the name of the starting city: ");
		startCity = input.nextLine();
		startIndex = getIndex(startCity);			// Gets the index of the start city
		
		if (startIndex == -1) {				// If the index returns -1, no such city exists and the program returns to the main menu
			System.out.printf("\nThe entered starting city was not found.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the name of the destination city: ");
		endCity = input.nextLine();
		endIndex = getIndex(endCity);
		
		if (endIndex == -1) {
			System.out.printf("\nThe entered destination city was not found.  Returning to the main menu.\n\n");
			return;
		}
		
		if (mode == 1) {							// Gets the shortest path based on distance
			DijkstraNode[] spt = new DijkstraNode[numCities];				// Declares a new shortest path tree of tentative distances
			spt = buildShortestDistance(endIndex, startIndex, spt, 1);			// Builds the shortest path tree
			int currentIndex = startIndex;
			int totalDistance = 0;					// Accumulator for total distance
			
			System.out.printf("\n0======================================0\n"
					+ "| Shortest Path Tree by Route Distance |\n"
					+ "0======================================0\n");
			System.out.printf("From %s to %s:\n\n", getCity(startIndex + 1),
					getCity(endIndex + 1));
			
			if (spt == null) {						// If the shortest path tree is null, the graph is disconnected
				System.out.printf("No path exists from %s to %s.  The graph is disconnected.\n\n",
						getCity(startIndex + 1), getCity(endIndex + 1));
				return;
			}
			
			while (currentIndex != endIndex) {			// Iterates through the tree until the full path is displayed
				DijkstraNode djNode = spt[currentIndex];
				int previous = djNode.getPrevious();
				
				if (previous < 0) {								// Contingency if a previous value is less than 0
					System.out.printf("No path exists from %s to %s.  The graph is disconnected.\n\n",
							getCity(startIndex + 1), getCity(endIndex + 1));
					return;
				}
				
				int tentDist = (int)djNode.getDistance();
				int prevTentDist = (int)spt[previous].getDistance();
				
				totalDistance += (tentDist - prevTentDist);			// Gets the true edge distance rather than the tentative distance
				System.out.printf("%s --> %s: %,d mi.\n", getCity(currentIndex + 1), getCity(previous + 1),
						tentDist - prevTentDist);
				
				currentIndex = previous;
			}
			
			System.out.printf("\tTotal Distance: %d mi.\n\n", totalDistance);
		}
		else {										// Gets the shortest path based on cost
			DijkstraNode[] spt = new DijkstraNode[numCities];				// Declares a new shortest path tree of tentative costs
			spt = buildShortestDistance(endIndex, startIndex, spt, 0);				// Builds the shortest path tree
			int currentIndex = startIndex;
			double totalCost = 0.0;					// Accumulator for total cost
			
			System.out.printf("\n0==================================0\n"
					+ "| Shortest Path Tree by Route Cost |\n"
					+ "0==================================0\n");
			System.out.printf("From %s to %s:\n\n", getCity(startIndex + 1),
					getCity(endIndex + 1));
			
			if (spt == null) {						// If the shortest path tree is null, the graph is disconnected
				System.out.printf("No path exists from %s to %s.  The graph is disconnected.\n\n",
						getCity(startIndex + 1), getCity(endIndex + 1));
				return;
			}
			
			while (currentIndex != endIndex) {			// Iterates through the tree until the full path is displayed
				DijkstraNode djNode = spt[currentIndex];
				int previous = djNode.getPrevious();
				
				if (previous < 0) {								// Contingency if a previous value is less than 0
					System.out.printf("No path exists from %s to %s.  The graph is disconnected.\n\n",
							getCity(startIndex + 1), getCity(endIndex + 1));
					return;
				}
				
				double tentCost = djNode.getDistance();
				double prevTentCost = spt[previous].getDistance();
				
				totalCost += (tentCost - prevTentCost);				// Gets the true edge cost rather than the tentative cost
				System.out.printf("%s --> %s: $%,.2f\n", getCity(currentIndex + 1), getCity(previous + 1),
						tentCost - prevTentCost);
				
				currentIndex = previous;
			}
			
			System.out.printf("\tTotal Cost: $%,.2f\n\n", totalCost);
		}
	}
	
	/**
	 * Builds a shortest path tree between cities using Dijkstra's algorithm
	 * @param endIndex The index of the end city
	 * @param tentative The array of unfilled tentative distances/costs
	 * @return The array of filled tentative distances/costs
	 */
	private static DijkstraNode[] buildShortestDistance(int endIndex, int startIndex, DijkstraNode[] tentative, int mode) {
		
		DijkstraHeap heap;						// Heap for selecting the minimum unvisited vertices
		boolean[] visited = new boolean[numCities];			// Tracks unvisited vertices
		int currentIndex = endIndex;
		
		for (int i = 0; i < numCities; i++) {			// Initializes all vertices as having a maximum distance and unvisited
			tentative[i] = new DijkstraNode(i);
			visited[i] = false;
		}
		
		tentative[endIndex].setDistance(0);			// Sets the starting vertex with a distance of 0
		
		while (currentIndex != startIndex) {			// Builds tentative distances for all cities
			CustomNode<AirlineRoute> node = airlineList[currentIndex].getListHead();
			heap = new DijkstraHeap();					// Clear the heap for updated entries
			
			while (node.hasData()) {					// Gets all neighbors of the unvisited vertex
				AirlineRoute route = node.getData();
				int destVertex = route.getDestination();			// Gets the destination of the vertex's edge
				
				if (!visited[destVertex - 1]) {			// Calculates the tentative distance if the destination is unvisited
					double tentativeDist;
					if (mode == 1) {
						tentativeDist = route.getDistance() + (int)tentative[currentIndex].getDistance();
					}
					else {
						tentativeDist = route.getCost() + tentative[currentIndex].getDistance();
					}
					
					if (tentativeDist < tentative[destVertex - 1].getDistance()) {		// Updates the tentative distance if it is less than the current distance
						tentative[destVertex - 1].setDistance(tentativeDist);
						tentative[destVertex - 1].setPrevious(currentIndex);			// Updates where the previous vertex is (along the path)
					}
				}
				
				node = node.getNextNode();
			}
			visited[currentIndex] = true;				// Marks the current vertex as visited
			
			for (int i = 0; i < numCities; i++) {				// Fills the heap with unvisited edges
				if (!visited[i]) {
					heap.add(tentative[i]);
				}
			}
			
			if (heap.isEmpty()) {			// If the heap is empty before the path is completed, returns null
				return null;
			}
			
			DijkstraNode djNode = heap.popMin();				// Gets the highest priority unvisited edge
			int nextIndex = djNode.getIndex();
			
			currentIndex = nextIndex;					// Sets the next vertex to do work on
		}
		
		return tentative;
	}
	
	
	/**
	 * Builds a shortest path tree of the entered starting and destination cities regardless of
	 * distance or cost (I.e., fewest number of hops) using a breadth-first search algorithm.
	 * Per requirements, cities are entered using their NAME, not their vertex number.
	 */
	private static void getShortestUnweighted() {
		
		String startCity, endCity;
		int startIndex, endIndex;					// Index locations of the entered cities
		
		System.out.printf("Enter the name of the starting city: ");
		startCity = input.nextLine();
		startIndex = getIndex(startCity);			// Gets the index of the start city
		
		if (startIndex == -1) {				// If the index returns -1, no such city exists and the program returns to the main menu
			System.out.printf("\nThe entered starting city was not found.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the name of the destination city: ");
		endCity = input.nextLine();
		endIndex = getIndex(endCity);
		
		if (endIndex == -1) {
			System.out.printf("\nThe entered destination city was not found.  Returning to the main menu.\n\n");
			return;
		}
		
		CustomQueue<Integer> queue = new CustomQueue<Integer>(endIndex);		// Creates a new queue with the destination index (reversed order for display purposes)
		int[] neighbor = new int[numCities];					// Holds the indices where each city leads to next
		boolean[] visited = new boolean[numCities];				// Tracks which cities have been visited
		boolean disconnected = false;
		
		for (int i = 0; i < numCities; i++) {			// Initializes all visited cities to false
			visited[i] = false;
		}
		
		int currentIndex;
		visited[endIndex] = true;					// Sets the destination city as visited
		
		while (!visited[startIndex]) {				// Loops until the starting index has been visited
			if (queue.isEmpty()) {
				disconnected = true;
				break;
			}
			
			currentIndex = queue.popNextInLine();			// Pops an index off the queue
			CustomNode<AirlineRoute> node = airlineList[currentIndex].getListHead();		// Gets the linked list of the index's neighbors
			
			while (node.hasData()) {						// Iterates through the linked list
				AirlineRoute route = node.getData();
				int nextIndex = route.getDestination() - 1;
				
				if (!visited[nextIndex]) {				// Conditional if the neighbor has not been visited yet
					queue.addToBackOfLine(nextIndex);			// Adds the neighbor to the queue
					neighbor[nextIndex] = currentIndex;			// Links the index and neighbor as a path
					visited[nextIndex] = true;					// Marks the neighbor as visited
				}
				
				node = node.getNextNode();
			}
		}

		System.out.printf("\n0======================================0\n"
				+ "| Shortest Path Tree by Number of Hops |\n"
				+ "0======================================0\n");
		System.out.printf("From %s to %s:\n\n", getCity(startIndex + 1),
				getCity(endIndex + 1));
		
		if (disconnected) {
			System.out.printf("No path exists from %s to %s.  The graph is disconnected.\n\n",
					getCity(startIndex + 1), getCity(endIndex + 1));
			return;
		}
		
		System.out.printf("%s", getCity(startIndex + 1));
		
		int numHops = 0;				// Tracks the number of hops on the trip
		currentIndex = startIndex;
		
		while (currentIndex != endIndex) {
			System.out.printf(" --> %s", getCity(neighbor[currentIndex] + 1));
			currentIndex = neighbor[currentIndex];
			numHops++;
			
			if (numHops % 4 == 0 && currentIndex != endIndex) {				// Prints an indented newline for formatting only
				System.out.printf("\n  ");
			}
		}
		
		System.out.printf("\n\tTotal Hops: %d\n\n", numHops);
	}
	
	
	/**
	 * Finds all airline routes up to a maximum defined cost between all cities in the graph
	 * using a depth-first search.  Paths will be printed twice, once from each city's point
	 * of view, as the paths are treated as unidirectional in the algorithm.
	 */
	private static void getFlightsOfCost() {
		
		String costStr;
		double maxCost;						// The maximum cost allowable for any path
		
		System.out.printf("Enter the maximum cost for any flight: $");			// Gets the maximum cost as a string
		costStr = input.nextLine();
		
		try {
			maxCost = Double.parseDouble(costStr);			// Tries to parse the string as a double
		}
		catch (NumberFormatException nfe) {
			System.out.printf("\nThe entered string is not a valid number.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("\n0==================================0\n"
				+ "| All Flights up to a Maximum Cost |\n"
				+ "0==================================0\n");
		System.out.printf("All flights of cost $%,.2f or less:\n\n", maxCost);
		
		for (int i = 0; i < numCities; i++) {							// Performs a DFS pre-order traversal starting from each city
			PreOrderAirline paths = new PreOrderAirline(numCities);		// Creates a new container object used for passing data between recursions
			paths.addPath(i);											// Adds the starting city to the container's stack
			preOrderGraphTraversal(maxCost, i, paths);					// Begins the recursive pre-order traversal function
		}
	}
	
	
	/**
	 * Performs a DFS pre-order traversal by checking unvisited cities along with the current running cost
	 * @param maxCost The maximum cost allowable for any trip
	 * @param currentIndex The current city of the traversal
	 * @param paths The pre-order data container
	 */
	private static void preOrderGraphTraversal(double maxCost, int currentIndex, PreOrderAirline paths) {
		
		paths.setVisited(currentIndex);									// Marks the current city as visited
		CustomNode<AirlineRoute> node = airlineList[currentIndex].getListHead();		// Gets the linked list neighbors
		
		while (node.hasData()) {						// Iterates through the linked list
			AirlineRoute route = node.getData();
			int destination = route.getDestination();
			
			if (!paths.isVisited(destination - 1)) {			// Conditional if the city is unvisited
				double routeCost = route.getCost();
				double currentSum = paths.getRunningCost();
				
				currentSum += routeCost;					// Adds the current running cost with the edge cost
				
				if (currentSum <= maxCost) {				// Conditional if the total sum does not exceed the maximum cost
					paths.addToCost(routeCost);				// Adds the edge cost to the total cost
					paths.addPath(destination - 1);			// Adds the current city to the stack
					printPaths(paths);						// Prints out the current cities in the stack
					preOrderGraphTraversal(maxCost, destination - 1, paths);		// Recursively calls the DFS pre-order traversal function for the next city
					paths.removeFromCost(routeCost);		// Upon returning, removes this edge cost from the total cost
					paths.removeLastPath();					// Upon returning, pops this city from the stack
				}
			}
			
			node = node.getNextNode();				// Moves on to the next city in the linked list (if one exists)
		}
		
		paths.setUnvisited(currentIndex);			// Marks the current city as unvisited
	}
	
	
	/**
	 * Prints out the current cities in the stack.  While the data structure is treated as a stack,
	 * it is treated as a queue when displaying the city routes.
	 * @param paths The pre-order traversal data container
	 */
	private static void printPaths(PreOrderAirline paths) {
		
		CustomQueue<Integer> queue = paths.getPaths();			// Gets the queue from the data container
		
		if (queue.getSize() <= 1) {				// Returns if the queue has fewer than 2 cities
			return;
		}
		
		CustomNode<Integer> node = queue.getFrontOfLine();			// Gets the front of the queue (hence, treated as a queue here and not a stack)
		int numHops = 0;						// Tracks the number of cities printed for formatting purposes
		
		System.out.printf("%s", getCity(node.getData() + 1));			// Prints the starting city from the queue
		node = node.getNextNode();
		
		while (node != null) {											// Iterates through the queue from front to back
			System.out.printf(" --> %s", getCity(node.getData() + 1));
			node = node.getNextNode();
			numHops++;
			
			if (numHops % 4 == 0 && numHops != queue.getSize() - 1) {		// Prints an indented newline every 4 hops
				System.out.printf("\n  ");
			}
		}
		
		System.out.printf("\n\tTotal Cost: $%,.2f\n\n", paths.getRunningCost());
	}
	
	
	/**
	 * Adds a new route to the graph using existing cities.  Does NOT add a new city.  Per requirements,
	 * cities are entered using their VERTEX NUMBER, not their name.
	 */
	private static void addNewRoute() {
		
		String startStr, endStr, distStr, costStr;				// Strings input by the user
		int startVertex, endVertex, distance;					// Parsed vertices and distance
		double cost;											// Parsed cost
		
		System.out.printf("Enter the vertex of the starting city: ");		// Gets the starting vertex
		startStr = input.nextLine();
		
		try {											// Tries to parse the starting city string
			startVertex = Integer.parseInt(startStr);
		}
		catch (NumberFormatException nfe) {					// Returns to the main menu if the string cannot be parsed
			System.out.printf("\nThe vertex entered is not a valid number.  Returning to the main menu.\n\n");
			return;
		}
		
		if (startVertex < 1 || startVertex > numCities) {			// Returns to the main menu if the number is out of bounds
			System.out.printf("\nThe number entered is not a valid vertex.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the vertex of the destination city: ");		// Gets the end vertex
		endStr = input.nextLine();

		try {
			endVertex = Integer.parseInt(endStr);
		}
		catch (NumberFormatException nfe) {
			System.out.printf("\nThe vertex entered is not a valid number.  Returning to the main menu.\n\n");
			return;
		}
		
		if (endVertex < 1 || endVertex > numCities) {
			System.out.printf("\nThe number entered is not a valid vertex.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the distance between these cities: ");			// Gets the distance between cities
		distStr = input.nextLine();
		
		try {
			distance = Integer.parseInt(distStr);
		}
		catch (NumberFormatException nfe) {
			System.out.printf("\nThe distance entered is not a valid integer.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the cost to fly between these cities: ");		// Gets the cost of the flight
		costStr = input.nextLine();
		
		try {
			cost = Double.parseDouble(costStr);
		}
		catch (NumberFormatException nfe) {
			System.out.printf("\nThe cost entered is not a valid decimal number.  Returning to the main menu.\n\n");
			return;
		}
		
		AirlineRoute newRoute = new AirlineRoute(startVertex, endVertex, distance, cost, false);		// Creates new normal and reversed routes
		AirlineRoute newReverseRt = new AirlineRoute(endVertex, startVertex, distance, cost, true);
		
		if (airlineList[startVertex - 1].contains(newRoute)) {
			System.out.printf("\nA route between these cities already exists!  Returning to the main menu.\n\n");
			return;
		}
		
		addFlight(startVertex, newRoute);			// Adds the routes to the adjacency list
		addFlight(endVertex, newReverseRt);
		modified = true;						// Sets the modified file flag to true
		
		System.out.printf("\nYour new route was successfully added!\n\n");
	}
	
	
	/**
	 * Removes an existing route between cities.  Does NOT remove cities.  Per requirements, cities
	 * are entered using their VERTEX NUMBER, not their name.
	 */
	private static void removeRoute() {
		
		String startStr, endStr;				// Strings input by the user
		int startVertex, endVertex;				// Parsed vertex numbers
		
		System.out.printf("Enter the vertex of the starting city: ");			// Gets the starting vertex
		startStr = input.nextLine();
		
		try {
			startVertex = Integer.parseInt(startStr);				// Tries to parse the starting city string
		}
		catch (NumberFormatException nfe) {						// Returns to the main menu if the string cannot be parsed
			System.out.printf("\nThe vertex entered is not a valid number.  Returning to the main menu.\n\n");
			return;
		}
		
		if (startVertex < 1 || startVertex > numCities) {		// Returns to the main menu if the number is out of bounds
			System.out.printf("\nThe number entered is not a valid vertex.  Returning to the main menu.\n\n");
			return;
		}
		
		System.out.printf("Enter the vertex of the destination city: ");		// Gets the end vertex
		endStr = input.nextLine();

		try {
			endVertex = Integer.parseInt(endStr);
		}
		catch (NumberFormatException nfe) {
			System.out.printf("\nThe vertex entered is not a valid number.  Returning to the main menu.\n\n");
			return;
		}
		
		if (endVertex < 1 || endVertex > numCities) {
			System.out.printf("\nThe number entered is not a valid vertex.  Returning to the main menu.\n\n");
			return;
		}
		
		AirlineRoute route = new AirlineRoute(startVertex, endVertex, 0, 0.0, true);		// Creates normal and reversed routes to match for removal from the list
		AirlineRoute reverse = new AirlineRoute(endVertex, startVertex, 0, 0.0, true);
		
		AirlineRoute removed = airlineList[startVertex - 1].remove(route);		// Removes and returns the route from the list
		
		if (removed == null) {				// If the route returned after removal is null, then nothing was removed; returns to the main menu as the route did not exist
			System.out.printf("\nThe entered route does not exist!  Nothing was removed.\n\n");
			return;
		}
		
		airlineList[endVertex - 1].remove(reverse);
		modified = true;								// Sets the modified file flag to true
		
		System.out.printf("\nYour route was successfully removed!\n\n");
	}
	
	
	/**
	 * Prints the main menu
	 */
	private static void displayMenu() {
		
		System.out.printf("-------- MAIN MENU --------\n"
				+ "Please select an option by entering the number next to the option name.\n"
				+ "\t1. Display all direct routes between cities\n"
				+ "\t2. Display a minimum spanning tree of routes by distance\n"
				+ "\t3. Find the trip with the shortest distance between two cities\n"
				+ "\t4. Find the trip with the lowest cost between two cities\n"
				+ "\t5. Find the trip with the fewest number of hops between two cities\n"
				+ "\t6. Display all trips less than or equal to a maximum defined cost\n"
				+ "\t7. Add a new route between existing cities\n"
				+ "\t8. Remove a route from the airline schedule\n"
				+ "\tQ. Exit the program.\n\n");
	}
	
	
	/**
	 * Writes a new text file if changes are made to the schedule of routes (by either addition or removal)
	 * @throws IOException
	 */
	private static void exitAirline() throws IOException {
		
		if (modified) {						// Conditional if changes were made to the file
			file = new File(filename);
			PrintWriter writer = new PrintWriter(file);			// Writes to a new file of the original name
			
			writer.println(numCities);					// Writes the number of cities in the airline
			
			for (int i = 0; i < numCities; i++) {		// Writes the names of all the cities
				writer.println(getCity(i + 1));
			}
			
			for (int i = 0; i < numCities; i++) {					// Writes information about each edge in the graph
				CustomNode<AirlineRoute> node = airlineList[i].getListHead();
				
				while (node.hasData()) {
					AirlineRoute route = node.getData();
					
					if (!route.getDirection()) {
						int origin = route.getOrigin();
						int destination = route.getDestination();
						int distance = route.getDistance();
						double cost = route.getCost();
						
						writer.println(origin + " " + destination + " " + distance + " " + cost);
					}
					node = node.getNextNode();
				}
			}
			
			writer.close();
		}
		
		System.out.printf("Thank you for flying Skyline International Airways.\n"
				+ "We hope to see you on board again in the future!\n");
	}
}