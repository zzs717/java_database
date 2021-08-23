//This class is used to organize the data in the table
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Table {

    private String tableName;
    public ArrayList<Row> tabulation = new ArrayList<>();
    public String getTableName() {
        return tableName;
    }
    public ArrayList<Row> getTabulation() {
        return tabulation;
    }

    //The table without Attribute
    public Table(String tableName) {
        this.tableName = tableName;
    }


    //The table with Attribute
    public Table(Row inputs, String tableName) {
        this.tableName = tableName;
        this.tabulation.add(inputs);
    }

    public void addTo(Row newRow) throws IOException {
        String[] temp = new String[newRow.getSize()+1];
        temp[0] = String.valueOf(tabulation.size());
        for (int i = 1; i < temp.length; i++) {
            temp[i] = newRow.getIndividual(i-1);
        }
        Row newRowId = new Row(temp);
        tabulation.add(newRowId);
        IOFile rStore = new IOFile(tableName);
        rStore.saveOneRow(newRow);
    }

    public void addTo(Row newRow,File file) throws IOException {
        String[] temp = new String[newRow.getSize()+1];
        temp[0] = String.valueOf(tabulation.size());
        for (int i = 1; i < temp.length; i++) {
            temp[i] = newRow.getIndividual(i-1);
        }
        Row newRowId = new Row(temp);
        tabulation.add(newRowId);
        IOFile rStore = new IOFile(file);
        rStore.saveOneRow(newRowId);
    }

    public void delete(int numRow) {
        String Id = String.valueOf(numRow);
        for (int i = 0; i < tabulation.size(); i++) {
            if(tabulation.get(i).getIndividual(0).equals(Id)){
                tabulation.remove(i);
                break;
            }
        }
    }

    public void printTable()
   {
        int[] maxLength = new int[tabulation.get(0).getSize()];
        getMaxLength(maxLength);
        for (int i = 0; i < tabulation.size(); i++) {
            for (int j = 0; j < tabulation.get(i).getSize(); j++) {
                System.out.print(tabulation.get(i).getIndividual(j)+" ");
                for (int k = 0; j<maxLength.length && k < (maxLength[j] - tabulation.get(i).getIndividual(j).length()); k++) {
                    System.out.print(" ");
                }
            }
            System.out.println("");
        }
    }

    public void getMaxLength(int[] maxLength){
        int colu = tabulation.get(0).getSize();
        for (int i = 0; i < tabulation.size(); i++) {
            if(colu>tabulation.get(i).getSize()){colu = tabulation.get(i).getSize();}
        }
        for (int i = 0; i < colu; i++) {
            maxLength[i] = 0;
            for (int j = 0; j < tabulation.size(); j++) {
                if(tabulation.get(j).getIndividual(i).length()>maxLength[i]){
                    maxLength[i] = tabulation.get(j).getIndividual(i).length();
                }
            }
        }
    }
}


