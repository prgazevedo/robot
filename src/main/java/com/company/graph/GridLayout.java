package com.company.graph;

import edu.uci.ics.jung.algorithms.layout.StaticLayout;

public class GridLayout {



    public StaticLayout getM_layout() {
        return m_layout;
    }

    private StaticLayout m_layout;




    public GridLayout(MapGraph graph, int numRows, int numColumns) {

        m_layout = new StaticLayout(graph.getM_graph());
        Layout();


    }

    private void Layout(){
        //distance between the nodes
        int distX=GraphProperties.NODE_X_DISTANCE;
        int distY=GraphProperties.NODE_Y_DISTANCE;



        int operatingNode = 0;

        for (int i = 0; i<GraphProperties.N_NODES_IN_COLUMNS; i++) {
            for (int j = 0; j<GraphProperties.N_NODES_IN_ROWS; j++) {
                m_layout.setLocation((operatingNode++), i*distX, j*distY);
            }
        }
    }


}

