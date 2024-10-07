package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public class Div extends BinaryFunction implements Serializable{
    private String _rendered = "";

    /**
     * @param content
     */
    public Div(String content){
        super(content);
    }

    /**
     * Executes a division.
     */
    public void operation(int value1, int value2){
        int result = value1 / value2;
       _rendered = "" + result;
    }

    @Override
    public String toString(){
        return "|" + _rendered + super.toString();
    }
}