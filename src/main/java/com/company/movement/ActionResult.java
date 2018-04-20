package com.company.movement;

public class ActionResult{



    public enum ACTION_RESULT {
        NONE(0), WAITING_RESULT(1), COMPLETED(2);

        private int action_result;
        private ACTION_RESULT(int i){this.action_result = i;}
        public ACTION_RESULT getEvent(){
            if(action_result<ACTION_RESULT.values().length && action_result>0) {
                try {
                    return ACTION_RESULT.values()[action_result];
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return ACTION_RESULT.NONE;
        }
    };




    private  Action m_Action;
    private  ActionResult.ACTION_RESULT m_Result;

    public ActionResult(Action action){
        m_Action=action;
        m_Result= ActionResult.ACTION_RESULT.WAITING_RESULT;
    }

    public ActionResult(Action action, ActionResult.ACTION_RESULT ar){
        m_Action=action;
        m_Result= ar;
    }

    public Action getM_Action() {
        return m_Action;
    }

    public ActionResult.ACTION_RESULT getM_Result() {
        return m_Result;
    }

    public void setM_Result(ActionResult.ACTION_RESULT result) {
         m_Result=result;
    }

    @Override
    public String toString() {
        return  "(" + m_Action + ", " + m_Result + ")";
    }
}
