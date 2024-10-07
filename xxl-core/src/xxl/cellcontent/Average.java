package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public class Average extends IntervalFunctionInt implements Serializable{
    private String _rendered = "";

    /**
     * @param content
     */
    public Average(String content){
        super(content);
    }

    /** 
     * Calculates the average between the Integer content of multiple cells
     */
    public void operation(int[] values){
        int result = 0;
        for(int i = 0; i < values.length; i++)
            result += values[i];
        result /= values.length;
        _rendered = "" + result;
    }
    
    /**
     * Sets the String that represents the content.
     */
    public  void setRendered(String rendered){
        _rendered = rendered;
    }

    @Override
    public String toString(){
        return "|" + _rendered + super.toString();
    }
}