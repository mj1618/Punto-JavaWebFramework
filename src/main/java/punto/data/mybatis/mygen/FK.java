package punto.data.mybatis.mygen;

/**
 * Created by MattUpstairs on 24/11/2014.
 */
public class FK {
    String pkTable,pkCol,fkTable,fkCol;

    public FK() {
    }

    public FK(String pkTable, String pkCol, String fkTable, String fkCol) {
        this.pkTable = pkTable;
        this.pkCol = pkCol;
        this.fkTable = fkTable;
        this.fkCol = fkCol;
    }

    public String getPkTable() {
        return pkTable;
    }

    public void setPkTable(String pkTable) {
        this.pkTable = pkTable;
    }

    public String getPkCol() {
        return pkCol;
    }

    public void setPkCol(String pkCol) {
        this.pkCol = pkCol;
    }

    public String getFkTable() {
        return fkTable;
    }

    public void setFkTable(String fkTable) {
        this.fkTable = fkTable;
    }

    public String getFkCol() {
        return fkCol;
    }

    public void setFkCol(String fkCol) {
        this.fkCol = fkCol;
    }
}
