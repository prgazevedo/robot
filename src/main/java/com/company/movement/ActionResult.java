package com.company.movement;

import java.util.ArrayList;
import java.util.HashMap;

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
    private  ActionResult.ACTION_RESULT m_ActionResult;
    private HashMap<String,Result> m_Result;

    public ActionResult(Action action){
        m_Action=action;
        m_ActionResult = ActionResult.ACTION_RESULT.WAITING_RESULT;
        m_Result = new HashMap<String,Result>();
    }

    public ActionResult(Action action, ActionResult.ACTION_RESULT ar){
        m_Action=action;
        m_ActionResult = ar;
    }

    public Action getM_Action() {
        return m_Action;
    }

    public ActionResult.ACTION_RESULT getM_ActionResult() {
        return m_ActionResult;
    }

    public void setM_ActionResult(ActionResult.ACTION_RESULT result) {
         m_ActionResult =result;
    }

    public void addResult(String name,Result result){
        m_Result.put(name,result);
    }

    public Result getResult(String name){
        if(m_Result.containsKey(name))return m_Result.get(name);
        else return null;
    }

    public HashMap<String,Result> getResults(){
        return m_Result;
    }


    @Override
    public String toString() {
        return  "(" + m_Action + ", " + m_ActionResult + ")";
    }


}
