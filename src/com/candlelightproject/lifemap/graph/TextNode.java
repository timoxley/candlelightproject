package com.candlelightproject.lifemap.graph;

/**
 * @author Douglas Catchpole
 *
 */
public class TextNode extends Node {

	private String text;
	
	public TextNode() {
		this.text = "";
	}
	
	public void setText(String text) {
		this.text = text; 
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return text;
	}

}
