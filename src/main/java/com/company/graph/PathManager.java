package com.company.graph;

import javafx.geometry.Point2D;

import java.util.NavigableMap;
import java.util.TreeMap;

/*
 * Stores and manages all current path choices (vertexID and Direction)
 */
public class PathManager {



    /**
     * Key is order of path ,Value is Id of vertex
     */
    private Integer m_currentPositionIteration_Key;
    private Integer m_currentPositionVertexID_Value;
    private NavigableMap<Integer, PathItem> m_path;
    private MapGraph m_mp;
    private Orientation m_MyOrientation;

    public Integer getM_currentPositionIteration_Key() {
        return m_currentPositionIteration_Key;
    }

    public void setM_currentPositionIteration_Key(Integer m_currentPositionIteration_Key) {
        this.m_currentPositionIteration_Key = m_currentPositionIteration_Key;
    }

    public Integer getM_currentPositionVertexID_Value() {
        return m_currentPositionVertexID_Value;
    }

    public void setM_currentPositionVertexID_Value(Integer m_currentPositionVertexID_Value) {
        this.m_currentPositionVertexID_Value = m_currentPositionVertexID_Value;
    }



    public PathManager(MapGraph mg) {
        m_mp=mg;
        m_path = new TreeMap<Integer, PathItem>();
        //Init();

    }

    public void Init(){
        m_MyOrientation = new Orientation(Direction.NONE);
        m_currentPositionIteration_Key = 0;
        m_currentPositionVertexID_Value=m_mp.getVertexId(new Point2D(GraphProperties.START_POSITION_X,GraphProperties.START_POSITION_Y));
        updatePosition(m_currentPositionVertexID_Value);
        PathItem newPathItem = new PathItem(m_currentPositionVertexID_Value, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);

    }


    public void updatePosition( int location){
        m_currentPositionIteration_Key++;
        m_currentPositionVertexID_Value =location;

    }

    public void updateDirection(Direction direction){
        m_MyOrientation.setMy_Direction(direction);
    }

    public void updatePath( int v1, Direction direction){
        m_currentPositionVertexID_Value=v1;
        m_MyOrientation.setMy_Direction(direction);
        PathItem newPathItem = new PathItem(m_currentPositionVertexID_Value, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);
        //And the Edge to Jung Graph
        m_mp.AddEdge(String.valueOf(m_currentPositionIteration_Key), m_currentPositionVertexID_Value, v1);
        m_mp.setVertexVisited(m_currentPositionVertexID_Value);

    }







}
