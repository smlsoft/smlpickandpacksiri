package Model;

public class MigrateIndexModel {
    private String indexName = "";
    private String[] indexCols = new String[]{};
    
    public MigrateIndexModel(String indexName, String[] indexCol){
        this.indexName = indexName;
        this.indexCols = indexCol;
    }
    
    public MigrateIndexModel(String indexName, String indexCol, String splitCharacter){
        this.indexName = indexName;
        this.indexCols = indexCol.split(splitCharacter);
    }

    public String getIndexName() {
        return indexName;
    }

    public void setIndexName(String indexName) {
        this.indexName = indexName;
    }

    public String[] getIndexCol() {
        return indexCols;
    }

    public void setIndexCol(String[] indexCol) {
        this.indexCols = indexCol;
    }
    
    public String getAddIndexScript(String tableName){
        StringBuilder sqlScript = new StringBuilder();

        String strCol = "";
        for(String col : indexCols ){
            if(!strCol.equals("")){ strCol += ",";}
            strCol += col;
        }
        
        sqlScript.append("CREATE INDEX IF NOT EXISTS "+this.indexName+" ON "+tableName+" ("+strCol+");");

        return sqlScript.toString();
    }
}
