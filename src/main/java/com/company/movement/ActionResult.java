package com.company.movement;

public class ActionResult<ACTION,Result> {

    public static String getDEFAULT_RESULT() {
        return DEFAULT_RESULT;
    }

    private static final String DEFAULT_RESULT = "WAITING_RESULT";
    private final ACTION m_Action;
    private final Result m_Result;

    public ActionResult(ACTION action,Result result){
        m_Action=action;
        m_Result=result;
    }

    public ACTION getM_Action() {
        return m_Action;
    }

    public Result getM_Result() {
        return m_Result;
    }

    @Override
    public String toString() {
        return  "(" + m_Action + ", " + m_Result + ")";
    }
}
