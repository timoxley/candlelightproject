/**
 * 
 */
package com.candlelightproject.lifemap.graph;

import java.util.ArrayList;
import java.util.Stack;

/**
 * @author Douglas Catchpole
 *
 */
public class Graph {

	private static ArrayList<Node> graph = new ArrayList<Node>();
	private static Stack<Integer> empty = new Stack<Integer>();
	
	/**
	 * 
	 * @return
	 */
	public static String saveString() {
		String result = "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n<graph>\n";
		for(Node n: graph) {
			result += n.saveString() + "\n";
		}
		return result + "</graph>\n";
	}
	
	public static void connectNodes(int i, int j) {
		Node node1 = graph.get(i);
		Node node2 = graph.get(j);
		node1.connectTo(j);
		node2.connectTo(i);
	}
	
	public static int insertNode(int parent, Node node) {
		int index;
		
		// If there is an empty spot in the list of nodes.
		if(empty.empty()) {
			index = graph.size();
			graph.add(node);
		} else {
			index = empty.pop();
			graph.add(index, node);
		}
		
		// Perhaps it was created from another node.
		if(parent >= 0) {
			node.connectTo(parent);
			graph.get(parent).connectTo(index);
		}
		
		return index;
		
	}
	
	public static int insertNode(Node node) {
		return insertNode(-1, node);
	}
	
	public static void deleteNode(int i) {
		// Disconnect the node from all it's neighbours.
		for(int j: graph.get(i).getNeighbours()) {
			graph.get(j).disconnect(i);
		}
		// Clear the spot in the list and add the empty spot to list
		// of empties.s
		graph.set(i, null);
		empty.push(i);
	}
	
	public static Node getNode(int i) {
		return graph.get(i);
	}
	
}
