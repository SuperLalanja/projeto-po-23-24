package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public abstract class IntervalFunctionStr extends IntervalFunction implements Serializable{
    private String type = "IntervalFunctionStr";

    /**
     * @param content
     */
    public IntervalFunctionStr(String content){
        super(content);
    }

    @Override
    public String getType(){
        return type + "|" + super.getType();
    }

    /**
     * @param values
     */
    public abstract void operation(String[] values);
}