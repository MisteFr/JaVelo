    package ch.epfl.javelo.projection;
    import java.lang.Math;

    public final class Ch1903 {

        // Non-instantiable class
        private Ch1903(){}


        /**
         * Gives E coordinate of CH1903+ based on WGS84 coordinates
         * @param lon lon coordinate of WGS84
         * @param lat lat coordinate of WGS84
         * @return E coordinate of CH1903+
         */
        public static double e(double lon, double lat){

            lon = Math.toDegrees(lon);
            lat = Math.toDegrees(lat);

            double lambda1 = Math.pow(10, -4) * (3600d * lon - 26782.5);
            double phi1 = Math.pow(10, -4) * (3600d * lat - 169028.66);

            return (2600072.37 + 211455.93 * lambda1  - 10938.51 * lambda1 * phi1 - 0.36 * lambda1 * Math.pow(phi1, 2) - 44.54 * Math.pow(lambda1, 3));
        }

        /**
         * Gives N coordinate of CH1903+ based on WGS84 coordinates
         * @param lon lon coordinate of WGS84
         * @param lat lat coordinate of WGS84
         * @return N coordinate of CH1903+
         */
        public static double n(double lon, double lat){

            lon = Math.toDegrees(lon);
            lat = Math.toDegrees(lat);

            double lambda1 = Math.pow(10, -4) * (3600 * lon - 26782.5);
            double phi1 = Math.pow(10, -4) * (3600 * lat - 169028.66);

            return (1200147.07 + 308807.95 * phi1 + 3745.25 * Math.pow(lambda1, 2) + 76.63 * Math.pow(phi1, 2) - 194.56 * Math.pow(lambda1, 2) * phi1 + 119.79 * Math.pow(phi1, 3));
        }

        /**
         * Gives lon coordinate of WGS84 based on CH1903+ coordinates
         * @param e e coordinate of CH1903+
         * @param n n coordinate of CH1903+
         * @return lon coordinate of WGS84
         */
        public static double lon(double e, double n){
            double x = Math.pow(10, -6) * (e - 2600000);
            double y = Math.pow(10, -6) * (n - 1200000);

            double lambda0 = 2.6779094 + (4.728982 * x) + (0.791484 * x * y) + (0.1306 * x * Math.pow(y, 2)) - (0.0436 * Math.pow(x, 3));
            double result = lambda0 *  (100d/36);

            return Math.toRadians(result);
        }

        /**
         * Gives lat coordinate of WGS84 based on CH1903+ coordinates
         * @param e e coordinate of CH1903+
         * @param n n coordinate of CH1903+
         * @return lat coordinate of WGS84
         */
        public static double lat(double e, double n){
            double x = Math.pow(10, -6) * (e - 2600000);
            double y = Math.pow(10, -6) * (n - 1200000);

            double phi0 = 16.9023892 + 3.238272 * y - 0.270978 * Math.pow(x, 2) - 0.002528 * Math.pow(y, 2) - 0.0447 * Math.pow(x, 2) * y - 0.0140 * Math.pow(y, 3);
            double result = phi0 *  (100d/36);

            return Math.toRadians(result);
        }
    }
