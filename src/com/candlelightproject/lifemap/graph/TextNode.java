package com.candlelightproject.lifemap.graph;

import org.xml.sax.Attributes;

/**
 * @author Douglas Catchpole
 *
 */
public class TextNode extends Node {

	private String text;
	
	public TextNode() {
		super(-1);
		this.text = "";
	}
	
	public TextNode(int parent) {
		super(parent);
		this.text = "";
	}
	
	public void setText(String text) {
		this.text = text; 
	}
	
	public boolean load(String name, Attributes atts) {
		if(name.equals("TextNode")) {
			String text = atts.getValue("text");
			if(text == null) {
				System.err.println("No text supplied.");
			}
			this.text = text;
			return true;
		}
		return super.load(name, atts);
	}
	
	@Override
	public String toString() {
		return text;
	}
	
	public Graph.NodeType getType() {
		return Graph.NodeType.TEXT_NODE;
	}
	
	public String saveString() {
		String result = "<TextNode id=\""+id+"\" text=\""+text.replace("\"", "&quot;")+"\">\n";
		for(int i: neighbours) {
			result += "<neighbour id=\""+i+"\" />\n";
		}
		result += "</TextNode>";
		return result;
	}

}
