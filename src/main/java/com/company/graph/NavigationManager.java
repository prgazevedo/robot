package com.company.graph;

import javafx.geometry.Point2D;

import java.util.NavigableMap;
import java.util.TreeMap;


public class NavigationManager {


    private MapGraph m_mp;

    /**
     * Key is order of path ,Value is Id of vertex
     */
    private NavigableMap<Integer, Integer> m_path;

    private RandomUtil m_random;

    private Integer m_currentPositionIteration_Key;
    private Integer m_currentPositionVertexID_Value;

    public NavigationManager(MapGraph mg) {


        m_path = new TreeMap<Integer, Integer>();
        m_random = new RandomUtil(0,Direction.getNumberDirections());
        m_mp = mg;
        m_currentPositionIteration_Key = 0;
        m_currentPositionVertexID_Value = 0;
    }



    public void mock_navigator_3(){

        int last_visited=0;
        m_path.put(0,last_visited);
        for (int i=0; i<GraphProperties.NAV_ITERATIONS; i++) {
            System.out.println("mock_navigator_3 - iteration:"+i);
            int v0 = last_visited;

            int v1 = navigateToNextVertex(v0);
            if(v1!=-1)
            {
                last_visited = v1;
                System.out.println("mock_navigator_3 navigating to:"+v1);
                m_mp.AddEdge(String.valueOf(i), v0, v1);
                m_mp.setVertexVisited(v1);
                m_currentPositionIteration_Key =i+1;
                m_currentPositionVertexID_Value =v1;
                m_path.put(m_currentPositionIteration_Key, m_currentPositionVertexID_Value);
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

        //for now set left and right walls
        System.out.println("exploreSurroundings - I am at:"+ m_currentPositionVertexID_Value +" and found wall in EAST node: "+m_mp.getNeighborID(m_currentPositionVertexID_Value,Direction.EAST));
        m_mp.setNeighborDirectionWall(m_currentPositionVertexID_Value,Direction.EAST,true);
        System.out.println("exploreSurroundings - I am at:"+ m_currentPositionVertexID_Value +" and found wall in WEST node: "+m_mp.getNeighborID(m_currentPositionVertexID_Value,Direction.WEST));
        m_mp.setNeighborDirectionWall(m_currentPositionVertexID_Value,Direction.WEST,true);

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

    private  int navigateToNextVertex(int v0){
        int v1=-1;
        boolean bSearching=true;
        m_random.init();
        while(bSearching) {

            exploreSurroundings();
            Direction new_direction = getFreeDirection();

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
        return v1;

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
