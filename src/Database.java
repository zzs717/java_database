import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Database {
    private String databasebName;
    private ArrayList<Table> tables = new ArrayList<Table>();
    public Database(String dbName) {
        this.databasebName = dbName;
    }

    // Store one table to the file
    public void storeTable(Table table) throws IOException {
        File fileDir = new File("Datas"+File.separator + databasebName);
        fileDir.mkdirs();
        File file = new File("Datas"+File.separator + databasebName + File.separator +table.getTableName());
        IOFile fStore = new IOFile(file);
        fStore.storeInfile(table);
    }

}
