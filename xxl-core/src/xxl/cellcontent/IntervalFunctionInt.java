package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public abstract class IntervalFunctionInt extends IntervalFunction implements Serializable{
    private String type = "IntervalFunctionInt";
    
    /**
     * @param content
     */
    public IntervalFunctionInt(String content){
        super(content);
    }

    @Override 
    public String getType(){
        return type + "|" + super.getType();
    }

    /**
     * @param rendered
     */
    public abstract void setRendered(String rendered);

    /**
     * @param values
     */
    public abstract void operation(int[] values);
}