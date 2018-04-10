package com.company.navigation;

import java.util.NavigableMap;

import com.company.MainRobot;
import com.company.Manager;
import com.company.events.IEvent;
import org.apache.logging.log4j.Level;

public class NavigationManager extends Manager  {

    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;
    private MainRobot m_mainRobot;
    private GraphManager m_GraphManager;
    private PathManager  m_PathManager;
    private RandomUtil m_random;



    public NavigationManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_PathManager = m_mainRobot.getM_PathManager();
        m_GraphManager = m_mainRobot.getM_GraphManager();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
    }


    public void runMockNavigator(){
        m_mainRobot.getM_MovementManager().move(10);
        while(m_PathManager.getM_currentPositionIteration_Key()<GraphProperties.NAV_ITERATIONS)
        {
        //for (int i=1; i<GraphProperties.NAV_ITERATIONS; i++) {
            writeLog(Level.INFO,"runMockNavigator - iteration:"+m_PathManager.getM_currentPositionIteration_Key());
            int v0 = m_PathManager.getM_currentPositionVertexID_Value();
            PathItem pathItem = navigateToNextVertex(v0);
            int VID = pathItem.getM_VertexId();
            if(VID!=-1)
            {
                writeLog(Level.INFO,"runMockNavigator navigating to:"+VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+" with getDirection:"+pathItem.getM_Direction());
                m_PathManager.goTo(pathItem);

            }
            else
            {
                writeLog(Level.INFO,"runMockNavigator Stop navigation");
                break;
            }

        }
        writeLog(Level.INFO,"runMockNavigator End navigation");
    }



    private void exploreSurroundings(){
        int VID=m_PathManager.getM_currentPositionVertexID_Value();
        Direction wallOrientationEAST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.EAST);
        Direction wallOrientationWEST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.WEST);
        int eastNeighborID = m_GraphManager.getNeighborID(VID,Direction.EAST);
        int westNeighborID = m_GraphManager.getNeighborID(VID,Direction.WEST);
        RandomUtil RU = new RandomUtil(1,4);
        int distanceOfWall = 1;//RU.getNonRepeatingRandomInt();
        //for now set left and right walls (but converted to our orientation)
        m_GraphManager.setNeighborInDirectionAsWall(VID,wallOrientationEAST,distanceOfWall);
        m_GraphManager.setNeighborInDirectionAsWall(VID,wallOrientationWEST,distanceOfWall);
        writeLog(Level.INFO,"exploreSurroundings - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and found wall at: "+wallOrientationEAST+" NodeID:"+eastNeighborID+"at distance:"+distanceOfWall);
        writeLog(Level.INFO,"exploreSurroundings - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and found wall at: "+wallOrientationWEST+" NodeID:"+westNeighborID+"at distance:"+distanceOfWall);


    }

    private PathItem navigateToNextVertex(int v0){
        int v1=-1;
        Direction new_direction = Direction.NONE;
        boolean bSearching=true;
        m_random.init();
        while(bSearching) {

            exploreSurroundings();
            new_direction = getFreeDirection();

            if(new_direction.equals(Direction.NONE))
            {
                //dead-end -> retrace the path -> but continue searching

                v1=retracePath();
                writeLog(Level.INFO,"navigateToNextVertex - retracePath to:"+v1);
                if(v1!=-1) {
                    m_random.init();
                    bSearching = true;
                } else {
                    //no more retrace
                    bSearching = false;
                }

            }
            else
            {
                writeLog(Level.INFO,"navigateToNextVertex - New valid node so exit the search: "+v1);
                //New valid node so exit the search
                bSearching=false;

            }

        }

        return m_PathManager.getNewPathItem(new_direction);

    }

    private Direction getFreeDirection()
    {
        int vID=m_PathManager.getM_currentPositionVertexID_Value();

        boolean bSearching=true;
        while(bSearching) {
            int randomInt = m_random.getNonRepeatingRandomInt();
            Direction testDirection = Direction.getDirection(randomInt);
            if (randomInt==-1) {
                //Exhausted the directions
                writeLog(Level.INFO,"getFreeDirection - Exhausted the directions");
                bSearching = false;
                return Direction.NONE;
            }
            else if (m_GraphManager.isNeighborOutOfBounds(vID,testDirection )) {
                //Out of bounds --> keep searching
                writeLog(Level.INFO,"getFreeDirection - Out of bounds at getDirection: " + testDirection + "continuing search");
                bSearching = true;
            }
            else if (m_GraphManager.isNeighborDirectionWall(vID, testDirection)) {
                //is a wall
                writeLog(Level.INFO,"getFreeDirection - Node is a Wall: "+ m_GraphManager.getNeighborID(vID,testDirection)+" at getDirection: " + testDirection + "continuing search");
                bSearching = true;
            }
            else if(m_GraphManager.wasVertexNeighborVisited(vID,testDirection)){
                writeLog(Level.INFO,"getFreeDirection - Node already visited: "+ m_GraphManager.getNeighborID(vID,testDirection)+" at getDirection: " + testDirection + "continuing search");
                bSearching = true;
            }
             else {
                writeLog(Level.INFO,"getFreeDirection - Node is a valid getDirection: " + m_GraphManager.getNeighborID(vID,testDirection)+" at getDirection: " + testDirection);
                //is not a wall - go ahead
                bSearching = false;
                return testDirection;

            }

        }

        writeLog(Level.INFO,"getFreeDirection - Error:");
        return Direction.NONE;
    }

    public int retracePath(){
        try {
            if(m_PathManager.retraceToPreviousPosition())
            {
                int new_position = m_PathManager.getRetracePositionVertexID();
                int previousPosition = m_PathManager.getM_currentPositionVertexID_Value();
                System.out.println("retracePath: Iteration: "+m_PathManager.getM_currentPositionIteration_Key()+" Retrace Iteration: "+m_PathManager.getM_retracePositionIteration_Key()+"I am at node: "+previousPosition +" go back to node: " + new_position);
                m_PathManager.newPosition(new_position);
                m_PathManager.newPathEdge(previousPosition, new_position, m_PathManager.getRetraceDirection());
                return m_PathManager.getM_currentPositionVertexID_Value();
            }
            else
            {
                System.out.println("retracePath: Could not retrace at current position: " + m_PathManager.getM_currentPositionVertexID_Value() + " at retracePosition: "+ m_PathManager.getM_retracePositionIteration_Key());
                return -1;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;

    }



}
