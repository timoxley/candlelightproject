package com.candlelightproject.lifemap.graph;

import java.util.HashSet;

public abstract class Node {
	
	private HashSet<Integer> neighbours = new HashSet<Integer>();
	
	public void connectTo(int n) {
		neighbours.add(n);
	}
	
	public void disconnect(int n) {
		neighbours.remove(n);
	}
	
	public HashSet<Integer> getNeighbours() {
		return neighbours;
	}
	
	public abstract String toString(); 

}
