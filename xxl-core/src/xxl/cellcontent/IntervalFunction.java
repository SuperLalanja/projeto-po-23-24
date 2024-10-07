package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public abstract class IntervalFunction extends Function implements Serializable{
    private String type = "IntervalFunction";
    
    /**
     * @param content
     */
    public IntervalFunction(String content){
        super(content);
    }

    @Override
    public String getType(){
        return type + "|" + super.getType();
    }
}