package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public class Product extends IntervalFunctionInt implements Serializable{
    private String _rendered = "";

    /**
     * @param content
     */
    public Product(String content){
        super(content);
    }

    /**
     * Executes a multiplication with two or more factors.
     */
    public void operation(int[] values){
        int result = 1;
        for(int i = 0; i < values.length; i++)
            result *= values[i];
        _rendered = "" + result;
    }

    /**
    * Sets the String that represents the content. 
    **/
    public  void setRendered(String rendered){
        _rendered = rendered;
    }

    @Override
    public String toString(){
        return "|" + _rendered + super.toString();
    }
}