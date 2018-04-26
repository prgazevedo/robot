package com.company.graph;

public class Coordinates2D {


        /**
         * The x coordinate.
         *
         * @defaultValue 0.0
         */
        private double x;


        public final double getX() {
            return x;
        }

        /**
         * The y coordinate.
         */
        private double y;


        public final double getY() {
            return y;
        }


        /**
         * Creates a new instance
         * @param x the x coordinate of the point
         * @param y the y coordinate of the point
         */
        public Coordinates2D(double x,  double y) {
            this.x  = x;
            this.y = y;
        }
}
