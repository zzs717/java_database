import java.io.*;
import java.util.ArrayList;

public class IOFile {
    File file;

    public IOFile() throws FileNotFoundException {}
    public IOFile(String fileName) throws FileNotFoundException {
        this.file = new File(fileName);
    }
    public IOFile(File file) throws FileNotFoundException {
        this.file = file;
    }

    public static void main(String[] args) throws IOException {
        new IOFile().test();
    }

    public void storeInfile(Table table) throws IOException {
        FileOutputStream fop = new FileOutputStream(file);
        OutputStreamWriter writer = new OutputStreamWriter(fop, "UTF-8");

        if(table.getTabulation().size()>0){int[] maxLength = new int[table.getTabulation().get(0).getSize()];}
        for (int i = 0; i < table.getTabulation().size(); i++) {
            for (int j = 0; j < table.getTabulation().get(i).getSize(); j++) {
                writer.append(table.getTabulation().get(i).getIndividual(j));
                if(j < table.getTabulation().get(0).getSize()-1){writer.append(",");}
            }
            writer.append("\n");
        }
        writer.close();
        fop.close();
    }

    public void saveOneRow(Row row)throws IOException{
        FileWriter writer = new FileWriter(file,true);
        for (int i = 0; i < row.getSize(); i++) {
            writer.write(row.getIndividual(i));
            writer.write(",");
        }
        writer.append("\n");
        writer.flush();
        writer.close();
    }

    public ArrayList<Row> readInfile()throws IOException{
        ArrayList<Row> result = new ArrayList<Row>();
        BufferedReader br = new BufferedReader(new FileReader(file));

        String read = "";
        while ((read = br.readLine()) != null)
        {
            String[] lineSplit = read.split(",");
            Row testRecord = new Row(lineSplit);
            result.add(testRecord);
        }
        br.close();
        return result;
    }

    public Row readOneline(int index)throws IOException{
        return readInfile().get(index);
    }

    public void test() throws IOException {
        Row testRecord1 = new Row(new String[]{"35", "Durant", "cat", "25365"});
        Row testRecord2 = new Row(new String[]{"6", "James","lion","38387"});
        Row testRecord3 = new Row(new String[]{"24", "Kobe", "mamba", "33462"});
        Row testRecord4 = new Row(new String[]{"23", "Jordan", "flyman", "32227"});

        Row Id = new Row(new String[] {"Id", "Name", "Kind", "Point"});
        Table testTable = new Table(Id,"test");
        testTable.addTo(testRecord1);
        testTable.addTo(testRecord2);
        testTable.addTo(testRecord3);
        testTable.addTo(testRecord4);


        IOFile aa = new IOFile(testTable.getTableName());
        aa.storeInfile(testTable);

        for (int i = 0; i < aa.readInfile().size(); i++) {
            for (int j = 0; j < aa.readInfile().get(i).getSize(); j++) {
                System.out.print(aa.readInfile().get(i).getIndividual(j));
            }
            System.out.println("");
        }
        Row testRecord5 = new Row(new String[]{"45", "Jordan", "flyman", "32227"});
        aa.saveOneRow(testRecord5);
        System.out.println(aa.readOneline(5).getIndividual(0));
    }
}
/*
        aa.saveInfile(testRecord1);
        aa.saveInfile(testRecord2);
        aa.saveInfile(testRecord3);
        aa.saveInfile(testRecord5);
 */