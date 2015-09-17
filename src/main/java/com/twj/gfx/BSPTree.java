package com.twj.gfx;

import java.util.ArrayList;
import java.util.List;

public class BSPTree {

    private final BSPNode root;

    public BSPTree(List<Polygon> polygons) {
        root = buildNodeFromPolygon(polygons.get(0));
        for (Polygon polygon : polygons) {
            addPolygon(root, polygon);
        }
    }

    public void populatedDrawList(List<Polygon> polygons, Vector4d point) {
        traverse(root, point, polygons);
    }

    private BSPNode buildNodeFromPolygon(Polygon polygon) {
        return new BSPNode(GeometryUtils.getPlaneFromPolygon(polygon), polygon);
    }

    private void traverse(BSPNode node, Vector4d point, List<Polygon> polygons) {

        if (node == null) {
            return;
        }

        double distance = GeometryUtils.pointToPlaneDistance(point, node.hyperPlane);

        if (distance > 0) {
            traverse(node.behind, point, polygons);
            polygons.addAll(node.polygons);
            traverse(node.inFront, point, polygons);
        } else if (distance < 0) {
            traverse(node.inFront, point, polygons);
            polygons.addAll(node.polygons);
            traverse(node.behind, point, polygons);
        } else {
            traverse(node.inFront, point, polygons);
            traverse(node.behind, point, polygons);
        }
    }

    private void addPolygon(BSPNode node, Polygon polygon) {

        double distance = GeometryUtils.pointToPlaneDistance(polygon.getCentrePoint(), node.hyperPlane);
        if (distance < 0) {
            if (node.behind == null) {
                node.behind = buildNodeFromPolygon(polygon);
            } else {
                addPolygon(node.behind, polygon);
            }
        } else if (distance > 0) {
            if (node.inFront == null) {
                node.inFront = buildNodeFromPolygon(polygon);
            } else {
                addPolygon(node.inFront, polygon);
            }
        } else {
            node.polygons.add(polygon);
        }
    }

    private static class BSPNode {

        Vector4d hyperPlane;
        List<Polygon> polygons = new ArrayList<Polygon>();
        BSPNode behind;
        BSPNode inFront;

        public BSPNode(Vector4d hyperplane, Polygon polygon) {
            this.hyperPlane = hyperplane;
            polygons.add(polygon);
        }
    }
}
