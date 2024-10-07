package xxl.cellcontent;

public class Reference extends Content {
    private String type = "Reference";
    
    /**
     * @param content
     */
    public Reference(String content){
        super(content);
    }

    @Override
    public String getType(){
        return type;
    }
    
    @Override
    public String toString(){
        return super.toString();
    }
}