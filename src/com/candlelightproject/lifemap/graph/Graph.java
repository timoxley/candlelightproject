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
	
	public static void connectNodes(int i, int j) {
		Node node1 = graph.get(i);
		Node node2 = graph.get(j);
		node1.connectTo(j);
		node2.connectTo(i);
	}
	
	public static void insertNode(int parent, Node node) {
		int index;
		
		if(empty.empty()) {
			index = graph.size();
			graph.add(node);
		} else {
			index = empty.pop();
			graph.add(index, node);
		}
		
		node.connectTo(parent);
		graph.get(parent).connectTo(index);
		
	}
	
	public static void deleteNode(int i) {
		for(int j: graph.get(i).getNeighbours()) {
			graph.get(j).disconnect(i);
		}
		graph.set(i, null);
		empty.push(i);
	}
	
	public static Node getNode(int i) {
		return graph.get(i);
	}
	
}
