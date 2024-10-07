package xxl.cellcontent;

import java.io.Serial;
import java.io.Serializable;

public abstract class BinaryFunction extends Function implements Serializable{
    private String type = "BinaryFunction";

    /**
     * @param content
     */
    public BinaryFunction(String content){
        super(content);
    }

    @Override
    public String getType(){
        return type + "|" + super.getType();
    }
    
    /**
     * @param value1
     * @param value2
     */
    public abstract void operation(int value1, int value2);
}