package com.company.navigation;

import java.nio.file.Path;
import java.util.NavigableMap;

import com.company.MainRobot;
import com.company.graph.GraphManager;
import com.company.graph.GraphProperties;
import com.company.manager.Manager;
import com.company.movement.ActionManager;
import com.company.movement.ActionProperties;
import com.company.movement.Obstacles;
import org.apache.logging.log4j.Level;

public class NavigationManager extends Manager  {

    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;
    private GraphManager m_GraphManager;
    private PathManager  m_PathManager;
    private ActionManager m_ActionManager;
    private RandomUtil m_random;

    @Override
    public void initialize() {
        super.initialize();
    }

    public NavigationManager(MainRobot mainRobot) {
        m_mainRobot = mainRobot;
        m_PathManager = m_mainRobot.getM_PathManager();
        m_GraphManager = m_mainRobot.getM_GraphManager();
        m_ActionManager = m_mainRobot.getM_ActionManager();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
    }


    public void runNavigator(){

        while(m_PathManager.getM_currentPositionIteration_Key()< GraphProperties.NAV_ITERATIONS)
        {

            writeLog(Level.INFO,"runMockNavigator - iteration:"+m_PathManager.getM_currentPositionIteration_Key());
            int v0 = m_PathManager.getM_currentPositionVertexID_Value();
            PathItem pathItem = exploreNextDestination(v0);
            int VID = pathItem.getM_VertexId();
            if(VID!=-1)
            {
                writeLog(Level.INFO,"runMockNavigator navigating to:"+VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+" with getDirection:"+pathItem.getM_Direction());
                m_PathManager.goTo(pathItem);
                m_ActionManager.move(pathItem.getM_Direction(), m_ActionManager.defaultDistance());

            }
            else
            {
                writeLog(Level.INFO,"runMockNavigator Stop navigation");
                break;
            }

        }
        writeLog(Level.INFO,"runMockNavigator End navigation");
    }


    private PathItem exploreNextDestination(int v0){
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

                v1=m_PathManager.retracePath();
                writeLog(Level.INFO,"navigateToNextVertex - retracePath to:"+v1);
                if(v1!=-1) {
                    m_random.init();
                    m_ActionManager.move(m_PathManager.getRetraceDirection(),m_ActionManager.defaultDistance());
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


    private void exploreSurroundings() {
        for(Direction direction: Obstacles.OBSTACLE_LIST){
            exploreForWall(direction);
            int hops = m_ActionManager.getLookResult();
            Direction wallOrientation = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(direction);
            setMockWall(wallOrientation,hops);
        }

    }



    private  void setMockWall(Direction direction,int hops){
        Direction wallOrientation = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(direction);
        int VID=m_PathManager.getM_currentPositionVertexID_Value();
        int NeighborID = m_GraphManager.getNeighborIDInHops(VID,wallOrientation,hops);
        m_GraphManager.setNeighborInDirectionAsWall(VID,wallOrientation,hops);
        writeLog(Level.INFO,"setMockWall - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and found wall at: "+wallOrientation+" NodeID:"+NeighborID+"at distance of :"+hops);
    }


    private void exploreForWall(Direction direction){
        int VID=m_PathManager.getM_currentPositionVertexID_Value();
        Direction wallOrientation = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(direction);
        m_ActionManager.look(direction.getM_properties().getDegrees());
        writeLog(Level.INFO,"exploreForWall - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and searching for wall at: "+wallOrientation);

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

    /*
    * Old Navigation Code
     */

    public boolean runStepwiseMockNavigator() {

        if(m_PathManager.getM_currentPositionIteration_Key()< GraphProperties.NAV_ITERATIONS)
        {

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
                return false;
            }
            return true;

        }
        writeLog(Level.INFO,"runMockNavigator End navigation");
        return false;
    }

    public void runMockNavigator(){
        m_mainRobot.getM_ActionManager().move(10);
        while(m_PathManager.getM_currentPositionIteration_Key()< GraphProperties.NAV_ITERATIONS)
        {

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


    private PathItem navigateToNextVertex(int v0){
        int v1=-1;
        Direction new_direction = Direction.NONE;
        boolean bSearching=true;
        m_random.init();
        while(bSearching) {

            OldexploreSurroundings();
            new_direction = getFreeDirection();

            if(new_direction.equals(Direction.NONE))
            {
                //dead-end -> retrace the path -> but continue searching

                v1=m_PathManager.retracePath();
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


    private void OldexploreSurroundings(){
        int VID=m_PathManager.getM_currentPositionVertexID_Value();
        Direction wallOrientationEAST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.EAST);
        Direction wallOrientationWEST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.WEST);
        int eastNeighborID = m_GraphManager.getNeighborID(VID,wallOrientationEAST);
        int westNeighborID = m_GraphManager.getNeighborID(VID,wallOrientationWEST);
        RandomUtil RU = new RandomUtil(1,4);
        int hops = 1;//RU.getNonRepeatingRandomInt();
        //for now set left and right walls (but converted to our orientation)
        m_GraphManager.setNeighborInDirectionAsWall(VID,wallOrientationEAST,hops);
        m_GraphManager.setNeighborInDirectionAsWall(VID,wallOrientationWEST,hops);
        writeLog(Level.INFO,"exploreSurroundings - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and found wall at: "+wallOrientationEAST+" NodeID:"+eastNeighborID+"at distance:"+hops);
        writeLog(Level.INFO,"exploreSurroundings - I am at:"+ VID+" at position:"+ m_GraphManager.getVertexCoordinates(VID)+"with Orientation"+m_PathManager.getM_MyOrientation().getMy_Direction()+" and found wall at: "+wallOrientationWEST+" NodeID:"+westNeighborID+"at distance:"+hops);

    }





}
