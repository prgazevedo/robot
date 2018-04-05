package com.company.navigation;

import com.company.MainRobot;
import javafx.geometry.Point2D;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
    private GraphManager m_graphManager;
    private MainRobot m_MainRobot;
    private Orientation m_MyOrientation;

    public Orientation getM_MyOrientation() {
        return m_MyOrientation;
    }
    public Integer getM_currentPositionIteration_Key() {
        return m_currentPositionIteration_Key;
    }
    public Integer getM_currentPositionVertexID_Value() {
        return m_currentPositionVertexID_Value;
    }
    public Integer getM_retracePositionIteration_Key() { return m_retracePositionIteration_Key; }
    public Integer getRetracePositionVertexID(){
        return m_path.get(m_retracePositionIteration_Key).getM_VertexId();
    }

    private  void writeLog(org.apache.logging.log4j.Level messageLevel,String message){ m_MainRobot.writeLog(messageLevel,message); }

    public PathManager(MainRobot mainRobot) {
        m_MainRobot = mainRobot;
        m_graphManager =mainRobot.getM_GraphManager();
        m_path = new TreeMap<Integer, PathItem>();
    }


    public void initialize(){
        m_MyOrientation = new Orientation(Direction.NORTH);
        m_currentPositionIteration_Key = 0;
        m_currentPositionVertexID_Value=getStartVertexID();
        m_graphManager.setVertexVisited(m_currentPositionVertexID_Value,true);
        PathItem newPathItem = new PathItem(m_currentPositionVertexID_Value, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);

    }

    public PathItem getNewPathItem(Direction new_direction)
    {
        //Due to retrace we might have changed position
        int v0 = getM_currentPositionVertexID_Value();
        int v1= m_graphManager.getNeighborID(v0,new_direction);
        return new PathItem(v1,new_direction);
    }


    public boolean newPosition(int location){
        m_currentPositionIteration_Key++;
        m_currentPositionVertexID_Value =location;
        m_graphManager.setVertexVisited(location,true);
        if(m_currentPositionIteration_Key>GraphProperties.NAV_ITERATIONS)
        {
            writeLog(Level.INFO,"newPosition exceeded number of Navigation Iterations:" + m_currentPositionVertexID_Value);
            return false;
        }
        else return true;
    }

    private void resetRetrace(){
        m_retracePositionIteration_Key=m_currentPositionIteration_Key;
    }

    private void updateDirection(Direction direction){
        m_MyOrientation.setMy_Direction(direction);
    }

    public void newPathEdge(int v0, int v1, Direction direction){

        updateDirection(direction);
        PathItem newPathItem = new PathItem(v1, m_MyOrientation.getMy_Direction());
        m_path.put(m_currentPositionIteration_Key, newPathItem);
        //And the Edge to Jung Graph
        m_graphManager.AddEdge(String.valueOf(m_currentPositionIteration_Key), v0, v1);
        m_graphManager.setNeighborInDirectionVisited(v0,direction,true);

    }

    public void goTo(PathItem Item){
        int tempPosition = m_currentPositionVertexID_Value;
        newPosition(Item.getM_VertexId());
        resetRetrace();
        newPathEdge(tempPosition,Item.getM_VertexId(),Item.getM_Direction());
    }

    public boolean retraceToPreviousPosition(){
        if(m_path.containsKey(m_retracePositionIteration_Key)) {
            m_retracePositionIteration_Key = m_path.lowerKey(m_retracePositionIteration_Key);
            return true;
        }
        else
        {
            writeLog(Level.INFO,"retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
            return false;
        }

    }

    public Direction getRetraceDirection(){
       return m_MyOrientation.getDirectionFromOrientation(Direction.SOUTH);
    }


    public int getStartVertexID(){
        return m_graphManager.getVertexId(new Point2D(GraphProperties.START_POSITION_X,GraphProperties.START_POSITION_Y));
    }

}
