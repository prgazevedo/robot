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
    private Integer m_retracePositionIteration_Key;
    private Integer m_currentPositionIteration_Key;
    private Integer m_currentPositionVertexID_Value;
    private NavigableMap<Integer, PathItem> m_path;
    private MapGraph m_mp;

    public Orientation getM_MyOrientation() {
        return m_MyOrientation;
    }

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


    }

    public void Init(){
        m_MyOrientation = new Orientation(Direction.NORTH);
        m_currentPositionIteration_Key = 0;
        m_currentPositionVertexID_Value=m_mp.getVertexId(new Point2D(GraphProperties.START_POSITION_X,GraphProperties.START_POSITION_Y));
        m_currentPositionVertexID_Value =m_currentPositionVertexID_Value;
        m_mp.setVertexVisited(m_currentPositionVertexID_Value,true);
        PathItem newPathItem = new PathItem(m_currentPositionVertexID_Value, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);

    }




    private void updatePosition(int location){
        m_currentPositionIteration_Key++;
        m_currentPositionVertexID_Value =location;
        m_mp.setVertexVisited(location,true);
    }

    private void resetRetrace(){
        m_retracePositionIteration_Key=m_currentPositionIteration_Key;
    }

    private void updateDirection(Direction direction){
        m_MyOrientation.setMy_Direction(direction);
    }

    private void updatePath(int v0, int v1, Direction direction){

        updateDirection(direction);
        PathItem newPathItem = new PathItem(v1, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);
        //And the Edge to Jung Graph
        m_mp.AddEdge(String.valueOf(m_currentPositionIteration_Key), v0, v1);
        m_mp.setNeighborInDirectionVisited(v0,direction,true);

    }

    public void goTo(PathItem Item){
        int tempPosition = m_currentPositionVertexID_Value;
        updatePosition(Item.getM_VertexId());
        resetRetrace();
        updatePath(tempPosition,Item.getM_VertexId(),Item.getM_Direction());
    }

    public int retracePath(){
        try {
            if(m_path.containsKey(m_retracePositionIteration_Key))
            {
                m_retracePositionIteration_Key = m_path.lowerKey(m_retracePositionIteration_Key);
                if(m_retracePositionIteration_Key==null){
                    System.out.println("retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
                    return -1;
                }
                else if(m_currentPositionIteration_Key>GraphProperties.NAV_ITERATIONS)
                {
                    System.out.println("retracePath exceeded number of Navigation Iterations:" + m_currentPositionVertexID_Value);
                    return -1;
                }
                else if(m_path.containsKey(m_retracePositionIteration_Key)) {
                    m_currentPositionVertexID_Value = m_path.get(m_retracePositionIteration_Key).getM_VertexId();
                    System.out.println("retracePath: Iteration:"+m_currentPositionIteration_Key+"Retrace Iteration"+m_retracePositionIteration_Key+" go back to node:" + m_currentPositionVertexID_Value);
                    int tempPosition = m_currentPositionVertexID_Value;
                    updatePosition(m_currentPositionVertexID_Value);
                    updatePath(tempPosition, m_currentPositionVertexID_Value, m_MyOrientation.getDirectionFromOrientation(Direction.SOUTH));
                    return m_currentPositionVertexID_Value;
                }
                else
                {
                    System.out.println("retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
                    return -1;
                }

            }
            else
            {
                System.out.println("retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }







}
