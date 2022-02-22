package ch.epfl.javelo.projection;

import ch.epfl.javelo.Math2;

public record PointCh(double e, double n) {
    public PointCh {
        if (!SwissBounds.containsEN(e, n)) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * Calculate the squared distance between this and that
     * @param that
     * @return the squared distance between this and that
     */
    public double squaredDistanceTo(PointCh that){
        return Math.pow(distanceTo(that), 2);
    }

    /**
     * Calculate the distance between this and that
     * @param that
     * @return the distance between this and that
     */
    public double distanceTo(PointCh that){
        return Math2.norm((e - that.e), (n - that.n));
    }

    /**
     *
     * @return
     */
    public double lon(){
        return Ch1903.lon(e, n);
    }

    /**
     *
     * @return
     */
    public double lat(){
        return Ch1903.lat(e, n);
    }
}
