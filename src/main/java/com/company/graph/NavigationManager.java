package com.company.graph;

import javafx.geometry.Point2D;

import java.util.NavigableMap;
import java.util.TreeMap;


public class NavigationManager {


    private MapGraph m_mp;
    private PathManager m_pm;
    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;

    private RandomUtil m_random;

    private Integer m_currentPositionIteration_Key;
    private Integer m_currentPositionVertexID_Value;

    public NavigationManager(MapGraph mg) {

        PathManager m_pm = new PathManager(mg);

        //m_path = new TreeMap<Integer, Integer>();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
        m_mp = mg;
        m_currentPositionIteration_Key = 0;
        m_currentPositionVertexID_Value = 0;
    }



    public void mock_navigator_3(){

        m_pm.Init();
        //int last_visited=m_mp.getVertexId(new Point2D(GraphProperties.START_POSITION_X,GraphProperties.START_POSITION_Y));
        //updatePositionAndPath(0,last_visited);

        for (int i=1; i<GraphProperties.NAV_ITERATIONS; i++) {
            System.out.println("mock_navigator_3 - iteration:"+i);
            int v0 = m_pm.getM_currentPositionVertexID_Value();
            PathItem pathItem = navigateToNextVertex(v0);

            if(pathItem.getM_VertexId()!=-1)
            {
                System.out.println("mock_navigator_3 navigating to:"+pathItem.getM_VertexId());
                m_pm.updatePosition(pathItem.getM_VertexId());
                m_pm.updatePath(pathItem.getM_VertexId(),pathItem.getM_Direction());

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
        RandomUtil RU = new RandomUtil(1,4);
        int distanceOfWall = RU.getNonRepeatingRandomInt();
        //for now set left and right walls (but converted to our orientation)
        m_mp.setNeighborInDirectionAsWall(m_currentPositionVertexID_Value,Direction.EAST,distanceOfWall);
        m_mp.setNeighborInDirectionAsWall(m_currentPositionVertexID_Value,Direction.EAST,distanceOfWall);
        System.out.println("exploreSurroundings - I am at:"+ m_currentPositionVertexID_Value +" and found wall in EAST node: "+m_mp.getNeighborID(m_currentPositionVertexID_Value,Direction.EAST)+ "at distance:"+distanceOfWall);
        System.out.println("exploreSurroundings - I am at:"+ m_currentPositionVertexID_Value +" and found wall in WEST node: "+m_mp.getNeighborID(m_currentPositionVertexID_Value,Direction.WEST)+ "at distance:"+distanceOfWall);


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
                //dead-end -> retrace the path
                bSearching=retracePath();
            }
            else
            {

                try {

                    v1 = m_mp.getNeighborID(v0,new_direction);
                    //New valid node so exit the search
                    bSearching=false;
                } catch (Exception e) {
                    System.out.println("mock_navigator" + e.toString());
                }

            }

        }
        return new PathItem(v1,new_direction);
        //return v1;

    }

    private Direction getFreeDirection()
    {
        boolean bSearching=true;
        while(bSearching) {
            int i_new_direction = m_random.getNonRepeatingRandomInt();
            if(i_new_direction==-1)
            {
                //Exhausted the directions
                System.out.println("getFreeDirection - Exhausted the directions");
                bSearching=false;
                                                                                    //          Direction direction = Direction.getDirection(i_new_direction);

            }
            else if(m_mp.isNeighborOutOfBounds(m_currentPositionVertexID_Value,Direction.getDirection(i_new_direction)))
            {
                //Out of bounds --> keep searching
                System.out.println("getFreeDirection - Out of bounds:"+Direction.getDirection(i_new_direction));
                bSearching=true;
            }
            else
            {
                 //is a wall
                boolean isWall=m_mp.isNeighborDirectionWall(m_currentPositionVertexID_Value,Direction.getDirection(i_new_direction));

                if(!isWall) {
                    //is not a wall - go ahead
                    bSearching=false;
                    return Direction.getDirection(i_new_direction);
                }
                else
                {
                    System.out.println("getFreeDirection - is a Wall:"+Direction.getDirection(i_new_direction));

                }
            }
        }
        return Direction.NONE;
    }



    private boolean retracePath(){
        if(m_path.containsKey(m_currentPositionIteration_Key))
        {
            m_currentPositionIteration_Key = m_path.lowerKey(m_currentPositionIteration_Key);
            m_currentPositionVertexID_Value = m_path.get(m_currentPositionIteration_Key);
            System.out.println("retracePath go back to:" + m_currentPositionVertexID_Value);
            navigateToNextVertex(m_currentPositionVertexID_Value);
            return false;
        }
        else
        {
            System.out.println("retracePath Cannot go earlier than:" + m_currentPositionVertexID_Value);
            return false;
        }

    }










}
