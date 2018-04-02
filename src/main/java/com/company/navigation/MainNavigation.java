package com.company.navigation;


public class MainNavigation {


    private static GraphManager m_mp;
    private static GraphViewer m_gv;
    private static NavigationManager m_NavigationManager;

    public static NavigationManager getM_NavigationManager() {
        return m_NavigationManager;
    }





    public MainNavigation() {
        m_mp  = new GraphManager(GraphProperties.N_VERTEXES);
        m_gv = new GraphViewer("Graph", m_mp );
        m_NavigationManager = new NavigationManager(m_mp);

    }


    public static void main(String[] args) {
       MainNavigation gm = new MainNavigation();
        m_NavigationManager.runMockNavigator();
        m_gv.viewGraph();
    }

    public void whereToMove(){

    }



}
