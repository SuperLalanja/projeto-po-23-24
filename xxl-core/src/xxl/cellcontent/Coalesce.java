package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public class Coalesce extends IntervalFunctionStr implements Serializable{
    private String _rendered = "";

    /**
     * @param content
     */
    public Coalesce(String content){
        super(content);
    }

    /**
    * Finds the first cell whose content is a String.
    **/
    public void operation(String[] values){
        String result = values[0];
       _rendered = "" + result;
    }

    @Override
    public String toString(){
        return "|" + _rendered + super.toString();
    }
}