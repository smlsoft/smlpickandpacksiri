package Model;

import java.util.Vector;

public class MigrateTableModel {

    private String tableName;
    private boolean exists;
    private Vector<MigrateColumnModel> columns;
    private Vector<String> constraints;
    private Vector<String> beforeScript;
    private Vector<String> afterScript;
    private Vector<MigrateIndexModel> indexs;

    public MigrateTableModel(String tableName) {
        this.tableName = tableName;
        
        columns = new Vector<MigrateColumnModel>();
        constraints = new Vector<String>();
        beforeScript = new Vector<String>();
        afterScript = new Vector<String>();
        indexs = new Vector<MigrateIndexModel>();
    }
    
    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public boolean isExists() {
        return exists;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public Vector<String> getConstraints() {
        return constraints;
    }

    public void addConstraint(String constraint) {
        this.constraints.add(constraint);
    }

    public Vector<MigrateColumnModel> getColumns() {
        return columns;
    }

    public void addColumns(MigrateColumnModel column) {
        this.columns.add(column);
    }

    public Vector<MigrateIndexModel> getIndexs() {
        return indexs;
    }
    
    public void addIndex(MigrateIndexModel index) {
        this.indexs.add(index);
    }

    public String getCreateTableScript() {
        StringBuilder sqlScript = new StringBuilder();

        StringBuilder sqlColumns = new StringBuilder();
        for (MigrateColumnModel column : this.columns) {
            if (sqlColumns.length() > 0) {
                sqlColumns.append(", ");
            }
            sqlColumns.append(column.getName() + " " + column.getType() + (column.getSize() < 1 ? "" : "(" + column.getSize() + ")") + " " + column.getOption());
        }

        StringBuilder sqlConstraints = new StringBuilder();

        if (sqlColumns.length() > 0) {
            for (String constraint : this.constraints) {
                sqlConstraints.append(", ");
                sqlConstraints.append(" CONSTRAINT " + constraint);
            }
        }

        sqlScript.append("CREATE TABLE IF NOT EXISTS " + this.tableName + " ( ");
        sqlScript.append(sqlColumns.toString());
        sqlScript.append(sqlConstraints.toString());
        sqlScript.append(" ) ");
        return sqlScript.toString();
    }

    public String getAddColumnScript() {
        StringBuilder sqlScript = new StringBuilder();
        for (MigrateColumnModel column : this.columns) {
            if (!column.isExists()) {
                sqlScript.append("ALTER TABLE " + this.tableName + " ADD COLUMN " + column.getName() + " " + column.getType() + (column.getSize() < 1 ? "" : "(" + column.getSize() + ")") + " " + column.getOption() + ";");
            }
        }
        return sqlScript.toString();
    }
    
    public String getAddIndexScript(){
        StringBuilder sqlScript = new StringBuilder();
        for (MigrateIndexModel index : this.indexs) {
            sqlScript.append(index.getAddIndexScript(this.tableName));
        }
        return sqlScript.toString();
    }
    
    public void addAfterScript(String data) {
        this.afterScript.add(data);
    }

    public void addBeforScript(String data) {
        this.beforeScript.add(data);
    }
    
    public String getBeforeScript() {
        StringBuilder sqlScript = new StringBuilder();
        if (!this.exists) {
            for (String data : beforeScript) {
                sqlScript.append(data + ";");
            }
        }
        return sqlScript.toString();
    }

    public String getAfterScript() {
        StringBuilder sqlScript = new StringBuilder();
        if (!this.exists) {
            for (String data : afterScript) {
                sqlScript.append(data + ";");
            }
        }
        return sqlScript.toString();
    }
}
