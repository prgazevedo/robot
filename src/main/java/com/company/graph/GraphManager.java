package com.company.graph;




import java.awt.*;

public class GraphManager {





    public GraphManager() {


    }


    public static void main(String[] args) {
        MapGraph mp  = new MapGraph(10000);
        GridLayout g = new GridLayout(mp,100,100);
        GraphViewer gv = new GraphViewer("Graph", new Dimension(800,600), g.getM_layout());
        gv.viewGraph();
    }


}
