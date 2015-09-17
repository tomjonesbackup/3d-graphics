package com.twj.gfx;

public class Vector4d {

    private double[] point = new double[4];

    public Vector4d(double x, double y, double z, double w) {
        point[0] = x;
        point[1] = y;
        point[2] = z;
        point[3] = w;
    }

    public double dotProduct(Vector4d other) {

        return
                (this.point[0] * other.point[0])
                        + (this.point[1] * other.point[1])
                        + (this.point[2] * other.point[2])
                        + (this.point[3] * other.point[3]);
    }

    public double magnitude() {
        return Math.sqrt(
                (this.point[0] * this.point[0])
                        + (this.point[1] * this.point[1])
                        + (this.point[2] * this.point[2])
                        + (this.point[3] * this.point[3]));
    }

    public double get(int dim) {
        return point[dim];
    }

    void set(int dim, double val) {
        point[dim] = val;
    }

    public Vector4d add(Vector4d other) {

        return new Vector4d(this.point[0] + other.point[0],
                this.point[1] + other.point[1],
                this.point[2] + other.point[2],
                this.point[3] + other.point[3]);

    }

    public Vector4d minus(Vector4d other) {

        return new Vector4d(this.point[0] - other.point[0],
                this.point[1] - other.point[1],
                this.point[2] - other.point[2],
                this.point[3] - other.point[3]);
    }

    public Vector4d scale(double unit) {

        return new Vector4d(this.point[0] * unit,
                this.point[1] * unit,
                this.point[2] * unit,
                this.point[3] * unit);
    }

    public Vector4d crossProduct(Vector4d other) {

        return new Vector4d(this.point[1] * other.point[2] - this.point[2] * other.point[1],
                this.point[2] * other.point[0] - this.point[0] * other.point[2],
                this.point[0] * other.point[1] - this.point[1] * other.point[0],
                0);
    }

    public Vector4d unitVector() {
        return this.scale(1 / this.magnitude());
    }

    public void print() {
        System.out.printf("vector: x=%s y=%s z=%s\n", this.point[0], this.point[1], this.point[2]);
    }
}
