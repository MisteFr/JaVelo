
import ch.epfl.javelo.Functions;
import ch.epfl.javelo.projection.PointCh;
import ch.epfl.javelo.routing.Edge;
import ch.epfl.javelo.routing.ElevationProfileComputer;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.SingleRoute;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ElevationProfileComputerTestAutre {

    @Test
    void elevationProfile() {
    }





            @Test
            void worksOnConstantRoad(){

                List<Edge> edges= new ArrayList<>();
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(10)));
                SingleRoute route = new SingleRoute(edges);
                assertEquals(10, ElevationProfileComputer.elevationProfile(route,50).elevationAt(10));
                assertEquals(10,ElevationProfileComputer.elevationProfile(route,50).elevationAt(150));
                assertEquals(10,ElevationProfileComputer.elevationProfile(route,50).elevationAt(-50));
            }

            @Test
            void worksOnRouteWithSingleEdge(){

                List<Edge> edges= new ArrayList<>();
                float samples[] = {360f,370f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples,100)));
                SingleRoute route = new SingleRoute(edges);
                for (int i = 0; i <= 100; i++) {
                    assertEquals(360+i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
                }
            }
            @Test
            void worksOnRouteWithTwoEdges(){

                List<Edge> edges= new ArrayList<>();
                float samples[] = {360f,370f};
                float samples2[] = {370f,360f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples2,50)));

                SingleRoute route = new SingleRoute(edges);
                for (int i = 0; i <= 50; i++) {
                    assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
                }
                for (int i = 0; i <= 50; i++) {
                    assertEquals(370-2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
                }
            }

            @Test
            void worksOnRouteWithThreeEdges(){

                List<Edge> edges= new ArrayList<>();
                float samples[] = {360f,370f};
                float samples2[] = {370f,360f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples2,50)));
                float samples3[] = {360f,390f};

                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples3,100)));
                SingleRoute route = new SingleRoute(edges);
                for (int i = 0; i <= 50; i++) {
                    assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
                }
                for (int i = 0; i <= 50; i++) {
                    assertEquals(370-2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
                }
                for (int i = 0; i <= 100; i++) {
                    assertEquals(360+3*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(100+i),1e-4);
                }
            }
            @Test
            public void worksWithNoProfileRoad(){
                List<Edge> edges= new ArrayList<>();
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
                Route route = new SingleRoute(edges);
                for (int i = -11; i <= 100; i++) {
                    assertEquals(0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
                }
            }
            @Test
            public void firstRoadNoProfile(){
                List<Edge> edges= new ArrayList<>();
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
                float samples[] = {360f,370f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));

                Route route = new SingleRoute(edges);
                for (int i = -11; i <= 50; i++) {
                    assertEquals(360,ElevationProfileComputer.elevationProfile(route,2).elevationAt(i),1e-4);
                }
                for (int i = 0; i <= 50; i++) {
                    assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,2).elevationAt(50+i),1e-4);
                }

            }
            @Test
            public void secondRoadNoProfile(){
                List<Edge> edges= new ArrayList<>();
                float samples[] = {360f,370f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.sampled(samples,50)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));

                Route route = new SingleRoute(edges);

                for (int i = 0; i < 50; i++) {
                    assertEquals(360+2*i/10.0,ElevationProfileComputer.elevationProfile(route,1).elevationAt(i),1e-4);
                }
                for (int i = 0; i <= 100; i++) {
                    assertEquals(370,ElevationProfileComputer.elevationProfile(route,0.0001).elevationAt(50+i),1e-4);
                }

            }


            @Test
            public void roadInTheMiddleNoProfile(){
                List<Edge> edges= new ArrayList<>();
                float samples[] = {360f,370f};
                float samples2[] = {380,390f};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples,100)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(Float.NaN)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples2,100)));


                Route route = new SingleRoute(edges);

                for (int i = 0; i < 300; i++) {
                    assertEquals(360+i/10.0,ElevationProfileComputer.elevationProfile(route,1).elevationAt(i),1e-4);
                }


            }

            @Test
            public void ULTIMATE(){
                List<Edge> edges= new ArrayList<>();
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),50, Functions.constant(Float.NaN)));
                float[] samples = {2000,5000};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),150, Functions.sampled(samples,150)));
                float[] samples2 = {5000,4000};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),40, Functions.sampled(samples2,40)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),160, Functions.constant(Float.NaN)));
                float[] samples3 = {8200,7000};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.sampled(samples3,100)));
                float[] samples4 = {7000,10000};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),80, Functions.sampled(samples4,80)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),150, Functions.constant(Float.NaN)));
                float[] samples5 = {4000,6100};
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),70, Functions.sampled(samples5,70)));
                edges.add(new Edge(0,1,new PointCh(2500000,1200000),new PointCh(2500000,1200000),100, Functions.constant(Float.NaN)));

                //Data taken from this beautiful piazza post
                //https://piazza.com/class/kzifjghz6po4se?cid=560


                Route route = new SingleRoute(edges);

                assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(0));
                assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(20));
                assertEquals(2500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(50));
                assertEquals(3000,ElevationProfileComputer.elevationProfile(route,76).elevationAt(100));
                assertEquals(3500,ElevationProfileComputer.elevationProfile(route,76).elevationAt(125));
                assertEquals(4000,ElevationProfileComputer.elevationProfile(route,76).elevationAt(150));
                assertEquals(4200,ElevationProfileComputer.elevationProfile(route,76).elevationAt(190));
                assertEquals(4375,ElevationProfileComputer.elevationProfile(route,76).elevationAt(225));
                assertEquals(5450,ElevationProfileComputer.elevationProfile(route,76).elevationAt(300));
                assertEquals(6525,ElevationProfileComputer.elevationProfile(route,76).elevationAt(375));
                assertEquals(7600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(450));
                assertEquals(7937.5,ElevationProfileComputer.elevationProfile(route,76).elevationAt(525));
                assertEquals(6825,ElevationProfileComputer.elevationProfile(route,76).elevationAt(600));
                assertEquals(5712.5,ElevationProfileComputer.elevationProfile(route,76).elevationAt(675));
                assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(750));
                assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(800));
                assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(900));
                assertEquals(4600,ElevationProfileComputer.elevationProfile(route,76).elevationAt(1000));
            }


        }
