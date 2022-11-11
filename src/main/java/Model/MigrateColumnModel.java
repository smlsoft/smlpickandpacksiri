package Model;

public class MigrateColumnModel {
    private String name;
    private String type;
    private int size;
    private String option;
    private boolean exists;

    public MigrateColumnModel(String name, String type) {
        this.name = name;
        this.type = type;
        this.size = 0;
        this.option = "";
        this.exists = false;
        
    }
    
    public MigrateColumnModel(String name, String type, int size) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.option = "";
        this.exists = false;  
    }
    
    public MigrateColumnModel(String name, String type, String option) {
        this.name = name;
        this.type = type;
        this.size = 0;
        this.option = option;
        this.exists = false;  
    }
    
    public MigrateColumnModel(String name, String type, int size, String option) {
        this.name = name;
        this.type = type;
        this.size = size;
        this.option = option;
        this.exists = false;  
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }
    
    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }
    
    public String getAddColumnScript(String tableName) {
        StringBuilder sqlScript = new StringBuilder();

        sqlScript.append("ALTER TABLE " + tableName + " ADD COLUMN " + this.name + " " + this.type + (this.size < 1 ? "" : "(" + this.size + ")") + " " + this.option + ";");

        return sqlScript.toString();
    }
    
    public String getAlterColumnDefault(String tableName) {
        StringBuilder sqlScript = new StringBuilder();

        // + " " + this.type + (this.size < 1 ? "" : "(" + this.size + ")") + " " + this.option + ";");
        switch (this.type) {
            case "smallint":
                sqlScript.append("ALTER TABLE ").append(tableName).append(" ALTER COLUMN " + this.name).append(" set default 0 ;");
                break;
        }
        //__createQuery.append("ALTER TABLE ").append(tableName).append(" ALTER column ").append(__getRealFieldName).append(" set default 0");
        return sqlScript.toString();
    }
}
