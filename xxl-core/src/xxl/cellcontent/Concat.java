package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public class Concat extends IntervalFunctionStr implements Serializable{
    private String _rendered = "";

    /**
     * @param content
     */
    public Concat(String content){
        super(content);
    }

    /**
     * Executes a concatenation.
     */
    public void operation(String[] values){
        String result = "";
        for(int i = 0; i < values.length; i++)
            result += values[i];
        result = result.replace("'", "");
        _rendered = "'" + result;
    }

    @Override
    public String toString(){
        return "|" + _rendered + super.toString();
    }
}