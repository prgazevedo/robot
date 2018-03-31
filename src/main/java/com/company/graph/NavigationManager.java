package com.company.graph;

import javafx.geometry.Point2D;

import java.util.NavigableMap;
import java.util.TreeMap;


public class NavigationManager {


    private MapGraph m_mp;
    private PathManager m_PathManager;
    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;

    private RandomUtil m_random;



    public NavigationManager(MapGraph mg) {

        m_PathManager = new PathManager(mg);

        //m_path = new TreeMap<Integer, Integer>();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
        m_mp = mg;

    }



    public void mock_navigator_3(){

        m_PathManager.Init();
        //int last_visited=m_mp.getVertexId(new Point2D(GraphProperties.START_POSITION_X,GraphProperties.START_POSITION_Y));
        //updatePositionAndPath(0,last_visited);

        for (int i=1; i<GraphProperties.NAV_ITERATIONS; i++) {
            System.out.println("mock_navigator_3 - iteration:"+i);
            int v0 = m_PathManager.getM_currentPositionVertexID_Value();
            PathItem pathItem = navigateToNextVertex(v0);

            if(pathItem.getM_VertexId()!=-1)
            {
                System.out.println("mock_navigator_3 navigating to:"+pathItem.getM_VertexId());
                m_PathManager.goTo(pathItem);

            }
            else
            {
                System.out.println("mock_navigator_3 Stop navigation");
                break;
            }

        }
        System.out.println("mock_navigator_3 End navigation");
    }



    private void exploreSurroundings(){
        int vID=m_PathManager.getM_currentPositionVertexID_Value();
        Direction wallOrientationEAST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.EAST);
        Direction wallOrientationWEST = m_PathManager.getM_MyOrientation().getDirectionFromOrientation(Direction.WEST);
        int eastNeighborID = m_mp.getNeighborID(vID,Direction.EAST);
        int westNeighborID = m_mp.getNeighborID(vID,Direction.WEST);
        RandomUtil RU = new RandomUtil(1,4);
        int distanceOfWall = 1;//RU.getNonRepeatingRandomInt();
        //for now set left and right walls (but converted to our orientation)
        m_mp.setNeighborInDirectionAsWall(vID,wallOrientationEAST,distanceOfWall);
        m_mp.setNeighborInDirectionAsWall(vID,wallOrientationWEST,distanceOfWall);
        System.out.println("exploreSurroundings - I am at:"+ vID+"with Orientation"+m_PathManager.getM_MyOrientation()+" and found wall at: "+wallOrientationEAST+" NodeID:"+eastNeighborID+"at distance:"+distanceOfWall);
        System.out.println("exploreSurroundings - I am at:"+ vID+"with Orientation"+m_PathManager.getM_MyOrientation()+" and found wall at: "+wallOrientationWEST+" NodeID:"+westNeighborID+"at distance:"+distanceOfWall);


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
                v1=m_PathManager.retracePath();
                if(v1!=-1) {
                    bSearching = true;
                } else {
                    //no more retrace
                    bSearching = false;
                }

            }
            else
            {

                try {

                    v1 = m_mp.getNeighborID(v0,new_direction);
                    System.out.println("navigateToNextVertex - New valid node so exit the search: "+v1);
                            //New valid node so exit the search
                    bSearching=false;
                } catch (Exception e) {
                    System.out.println("navigateToNextVertex" + e.toString());
                }

            }

        }

        return new PathItem(v1,new_direction);
        //return v1;

    }

    private Direction getFreeDirection()
    {
        int vID=m_PathManager.getM_currentPositionVertexID_Value();
        boolean bSearching=true;
        while(bSearching) {
            int i_new_direction = m_random.getNonRepeatingRandomInt();
            if (i_new_direction == -1) {
                //Exhausted the directions
                System.out.println("getFreeDirection - Exhausted the directions");
                bSearching = false;
                return Direction.NONE;

            } else if (m_mp.isNeighborOutOfBounds(vID, Direction.getDirection(i_new_direction))) {
                //Out of bounds --> keep searching
                System.out.println("getFreeDirection - Out of bounds:" + Direction.getDirection(i_new_direction) + "continuing search");
                bSearching = true;
            } else if (m_mp.isNeighborDirectionWall(vID, Direction.getDirection(i_new_direction))) {
                //is a wall
                System.out.println("getFreeDirection - is a Wall:" + Direction.getDirection(i_new_direction) + "continuing search");
                bSearching = true;
            } else {
                System.out.println("getFreeDirection - valid direction:" + Direction.getDirection(i_new_direction));
                //is not a wall - go ahead
                bSearching = false;
                return Direction.getDirection(i_new_direction);

            }

        }

        System.out.println("getFreeDirection - Error:");
        return Direction.NONE;
    }


/*
    private boolean retracePath(){
        if(m_path.containsKey(m_PathManager.getM_currentPositionIteration_Key() ))
        {
            m_currentPositionIteration_Key = m_path.lowerKey(m_PathManager.getM_currentPositionIteration_Key());
            m_currentPositionVertexID_Value = m_path.get(m_PathManager.getM_currentPositionIteration_Key());
            System.out.println("retracePath go back to:" + m_PathManager.getM_currentPositionVertexID_Value());
            navigateToNextVertex(m_PathManager.getM_currentPositionVertexID_Value());
            return false;
        }
        else
        {
            System.out.println("retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
            return false;
        }

    }
*/









}
