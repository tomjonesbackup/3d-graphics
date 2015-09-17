package com.twj.gfx;

public class GeometryUtils {

    public static double pointToPlaneDistance(Vector4d point, Vector4d plane) {
        return plane.dotProduct(point);
    }

    public static Vector4d getPlaneFromPolygon(Polygon polygon) {
        double D = polygon.D();
        Vector4d normal = polygon.getNormal();
        return new Vector4d(normal.get(0), normal.get(1), normal.get(2), D);
    }
}
