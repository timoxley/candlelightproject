package com.candlelightproject.lifemap.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import com.candlelightproject.lifemap.graph.Graph;
import com.candlelightproject.lifemap.graph.GraphLoader;

public class TestLoadGraph {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File inFile = new File("test/test.xml");
		GraphLoader gl = new GraphLoader();
		try {
			FileInputStream fis = new FileInputStream(inFile);
			gl.load(fis);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(Graph.saveString());
		
	}

}
