package com.company.graph;

import java.awt.*;

public class GraphProperties {
    public static final int N_VERTEXES = 100;
    public static final int N_NODES_IN_COLUMNS = 10;
    public static final int N_NODES_IN_ROWS = 10;
    public static final int WINDOW_HEIGHT = 800;
    public static final int WINDOW_WIDTH = 600;

    public static final int NODE_X = -5;
    public static final int NODE_Y = -5;
    public static final int NODE_W = 5;
    public static final int NODE_H = 5;
    public static final Color NODE_COLOR = Color.GREEN;
    public static final Color VISITED_NODE_COLOR = Color.RED;
    public static final Color WALL_COLOR = Color.BLACK;

    public static final int NODE_X_DISTANCE = 5;
    public static final int NODE_Y_DISTANCE = 5;
    public static final int DELTA_Y = NODE_Y_DISTANCE*N_NODES_IN_COLUMNS;
    public static final int DELTA_X = NODE_X_DISTANCE*N_NODES_IN_ROWS;

    public static final int NAV_ITERATIONS = 100;
}
