package xxl.cellcontent;

public abstract class Function extends Content {
    private String type = "Function";

    /**
     * @param content
     */
    public Function(String content) {
        super(content); 
    }

    @Override
    public String getType(){
        return type;
    }
}