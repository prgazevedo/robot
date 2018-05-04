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


    private int m_hash = 0;
    /**
     * Indicates whether some other object is "equal to" this one.
     */
   public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj instanceof Coordinates2D) {
            Coordinates2D other = (Coordinates2D) obj;
            return getX() == other.getX() && getY() == other.getY();
        } else return false;
    }
    /**
     * Returns a hash code value for the point.
     */
    public int hashCode() {
        if (m_hash == 0) {
            long bits = 7L;
            bits = 31L * bits + Double.doubleToLongBits(getX());
            bits = 31L * bits + Double.doubleToLongBits(getY());
            m_hash = (int) (bits ^ (bits >> 32));
        }
        return m_hash;
    }

    /**
     * Returns a string representation
     */
     public String toString() {
        return "Coordinates2D [x = " + getX() + ", y = " + getY() + "]";
    }
}
