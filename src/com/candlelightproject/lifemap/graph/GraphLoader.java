package com.candlelightproject.lifemap.graph;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class GraphLoader extends DefaultHandler {
	
	private Node currentNode = null;
	
	public void startElement(String uri, String name, String qName, Attributes atts) {
		if(qName.equals("TextNode")) {
			currentNode = new TextNode();
			currentNode.load(qName, atts);
		}
		// All other node types
		else if(currentNode != null){
			currentNode.load(qName, atts);
		}
	}
	
	public void endElement(String uri, String name, String qName) throws SAXException {
		
	}
	
	public void characters(char ch[], int start, int length) {
		
	}
	
	public void load(InputStream fis) {
		try {
			SAXParserFactory spf = SAXParserFactory.newInstance();
			SAXParser sp = spf.newSAXParser();
			XMLReader xr = sp.getXMLReader();
			xr.setContentHandler(this);
			xr.parse(new InputSource(fis));
		} catch (IOException e) {
			System.err.println("LifeMap: " + e.toString());
		} catch (SAXException e) {
			System.err.println("LifeMap: " + e.toString());
		} catch (ParserConfigurationException e) {
			System.err.println("LifeMap: " + e.toString());
		} 
	}

}
