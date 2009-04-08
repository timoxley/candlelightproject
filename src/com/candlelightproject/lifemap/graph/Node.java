package com.candlelightproject.lifemap.graph;

import java.util.HashSet;

import org.xml.sax.Attributes;

public abstract class Node {
	
	protected final int id;
	protected HashSet<Integer> neighbours = new HashSet<Integer>();
	
	protected Node(int parent) {
		this.id = Graph.insertNode(parent, this);
	}
	
	public void connectTo(int n) {
		neighbours.add(n);
	}
	
	public void disconnect(int n) {
		neighbours.remove(n);
	}
	
	public HashSet<Integer> getNeighbours() {
		return neighbours;
	}
	
	public int getId() {
		return id;
	}
	
	public boolean load(String name, Attributes atts) {
		try {
			if (name.equals("neighbour")) {
				Integer neigh = new Integer(atts.getValue("id"));
				if(neigh == null) {
					System.err.println("No id supplied.");
				}
				neighbours.add(neigh);
				return true;
			}
		} catch (Exception e) {
			System.err.println("Invalid neighbour id.");
			return false;
		}
		return false;
	}

	
	public abstract String toString();
	
	public abstract String saveString();

}
