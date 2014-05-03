package com.twj.gfx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class World {
	private List<Polygon> polygons = new ArrayList<Polygon>();
	private BSPTree tree;
	private Vector4d lightPosition;
	
	public void addPolygon(Polygon polygon) {
		polygons.add(polygon);
	}
	
	public void buildTree() {
		tree = new BSPTree();
		tree.build(polygons);
	}
	
	public List<Polygon> getPolygons() {
		return this.polygons;
	}
	
	public List<Polygon> getBackToFrontPolygonList(Vector4d point) {
		
		List<Polygon> orderedPolygons = new ArrayList<Polygon>();
		tree.populatedDrawList(orderedPolygons, point);
		return orderedPolygons;
	}
	
	public List<Polygon> getFrontToBackPolygonList(Vector4d point) {
		
		List<Polygon> orderedPolygons = new ArrayList<Polygon>();
		tree.populatedDrawList(orderedPolygons, point);
		Collections.reverse(orderedPolygons);
		return orderedPolygons;
	}
	

	public Vector4d getLightPosition() {
		return lightPosition;
	}

	public void setLightPosition(Vector4d lightPosition) {
		this.lightPosition = lightPosition;
	}
}
