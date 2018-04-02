package com.company.navigation;

public class PathItem{


    private Integer m_VertexId;
    private Direction m_Direction;

    public PathItem(Integer vertexID, Direction direction) {
        m_VertexId=vertexID;
        m_Direction=direction;
    }

    public Integer getM_VertexId() {
        return m_VertexId;
    }

    public void setM_VertexId(Integer m_VertexId) {
        this.m_VertexId = m_VertexId;
    }

    public Direction getM_Direction() {
        return m_Direction;
    }

    public void setM_Direction(Direction m_Direction) {
        this.m_Direction = m_Direction;
    }
}
