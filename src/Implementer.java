import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Implementer {
    String currentDb;
    public Implementer() {}

    //USE
    public void useDb(String[] command){currentDb = command[1];}

    //CREATE
    public void creatDb(String[] command){
        File fileDir = new File("Datas" +File.separator+ command[2]);
        fileDir.mkdirs();
    }

    public void creatTable(String input, String[] command) throws IOException {
        Row Attribute;
        //Table without Attribute
        if(command.length==3){
            Table table = new Table(command[2]);
            Database database = new Database(currentDb);
            database.storeTable(table);
            return;
        }
        //Table with Attribute
        if(command.length>3){
            input = removeBraks(input);
            if(input.length()!=0){
                Attribute = addId(input);
                Table table = new Table(Attribute,command[2]);
                Database database = new Database(currentDb);
                database.storeTable(table);
            }
        }
    }

    //Remove brackets '(' ')'
    public String removeBraks(String input){
        int num1 = input.indexOf('(');
        int num2 = input.indexOf(')');
        if(num1==-1||num2==-1){
            System.out.println("[ERROR]:Missing brackets");
            return "";
        }
        input = input.substring(num1+1,num2);
        return input;
    }

    //Remove apostrophe " ' "
    public String[] removeApos(String[] command){
        for (int i = 0; i < command.length; i++) {
            if(command[i].indexOf("'")!=-1){
                int num = command[i].length()-1;
                command[i] = command[i].substring(1,num);
            }
        }
        return command;
    }

    public Row addId(String input){
        int length;
        length = input.split(","+" ").length+1;
        String[] temp = new String[length];
        temp[0] = "id";
        for (int i = 1; i < length; i++) {
            temp[i] = input.split(","+" ")[i-1];
        }
        return new Row(temp);
    }

    //INSERT
    public void insertRow(String input, String[] command) throws IOException {
        input = removeBraks(input);
        if(input.length()!=0){
            String[] message = input.split(","+" ");
            message = removeApos(message);
            Row newRow = new Row(message);
            Table table = new Table(command[2]);
            File file = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
            IOFile readTable= new IOFile(file);
            table.tabulation = readTable.readInfile();
            table.addTo(newRow,file);
        }
    }

    //SELECT
    public void selectRowWithcon(File file, String[] command, int[]attributeIndex) throws IOException {
        int[] Id = new Condition().impleCondition(command,file);
        IOFile readF = new IOFile(file);
        Table table = new Table("selected");
        for (int i = 0; i < Id.length; i++) {
            Row row = new Row();
            for (int k = 0; k < readF.readOneline(Id[i]).getSize(); k++) {
                if(contain(k,attributeIndex)){
                    row.addto(readF.readOneline(Id[i]).getIndividual(k));
                }
            }
            table.tabulation.add(row);
        }
        table.printTable();
    }

    public void selectRowWithoutcon(File file, int[]attributeIndex) throws IOException {
        IOFile readF = new IOFile(file);
        Table table = new Table("selected");
        for (int i = 0; i < readF.readInfile().size(); i++) {
            Row row = new Row();
            for (int k = 0; k < readF.readOneline(i).getSize(); k++) {
                if(contain(k,attributeIndex)){
                    row.addto(readF.readOneline(i).getIndividual(k));
                }
            }
            table.tabulation.add(row);
        }
        table.printTable();
    }

    //DELETE
    public void deleteWithCon(File file, String[] command,String[] condition) throws IOException{
        int[] Id = new Condition().impleCondition(condition,file);
        IOFile readF = new IOFile(file);
        Table table = new Table(command[2]);
        table.tabulation = readF.readInfile();
        for (int i = 0; i < Id.length; i++) {
            table.delete(Id[i]);
        }
        file.delete();
        File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
        IOFile writeF = new IOFile(file1);
        writeF.storeInfile(table);
    }

    //ALTER
    public void alertAdd(File file, String[] command) throws IOException {
        IOFile readF = new IOFile(file);
        if(contain(command[4],readF.readOneline(0).getoneRow())){return;}
        else{
            Table table = new Table(command[2]);
            table.tabulation = readF.readInfile();
            Row row = readF.readOneline(0);
            row.addto(command[4]);
            table.tabulation.set(0,row);
            for (int i = 1; i < table.tabulation.size(); i++) {
                Row row2 = readF.readOneline(i);
                row2.addto("   ");
                table.tabulation.set(i,row2);
            }
            file.delete();
            File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
            IOFile writrF = new IOFile(file1);
            writrF.storeInfile(table);
        }
    }

    public void alertDrop(File file, String[] command) throws IOException {
        IOFile readF = new IOFile(file);
        Table table = new Table(command[2]);
        table.tabulation = readF.readInfile();
        int index = 0;
        if(contain(command[4],readF.readOneline(0).getoneRow())){
            for (; index < readF.readOneline(0).getSize(); index++) {
                if(command[4].equals(readF.readOneline(0).getIndividual(index))){
                    break;
                }
            }
            for (int i = 0; i < table.tabulation.size(); i++) {
                Row row = readF.readOneline(i);
                if(index<row.individual.size()){
                    row.individual.remove(index);
                }
                table.tabulation.set(i,row);
            }
            file.delete();
            File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
            IOFile writrF = new IOFile(file1);
            writrF.storeInfile(table);
        }
    }

    //UPDATA
    public void updata(File file, String[] command, int index, String[] condition) throws IOException{
        int[] Id = new Condition().impleCondition(condition,file);
        String[] Id2 = new String[Id.length];
        for (int i = 0; i < Id.length; i++) {Id2[i] = String.valueOf(Id[i]);}
        IOFile readF = new IOFile(file);
        Table table = new Table(command[2]);
        table.tabulation = readF.readInfile();
        ArrayList<Integer> attribulist = new ArrayList<Integer>();
        for (int i = 3; i < index; i+=3) {attribulist.add(attrIndex(readF.readOneline(0),command[i]));}
        for (int i = 0; i < table.tabulation.size(); i++) {
            Row row = table.tabulation.get(i);
            if(contain(table.tabulation.get(i).getIndividual(0),Id2)){
                for (int j = 0; j < attribulist.size(); j++) {
                    row.individual.set(attribulist.get(j),command[5+3*j]);
                }
            }
            table.tabulation.set(i,row);
        }
        file.delete();
        File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[1]);
        IOFile writeF = new IOFile(file1);
        writeF.storeInfile(table);
    }

    //JOIN
    public void join(String[] command) throws IOException {
        File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[1]);
        File file2 = new File("Datas"+File.separator+currentDb+File.separator+command[3]);
        IOFile readF1 = new IOFile(file1);
        IOFile readF2 = new IOFile(file2);
        Row attri = getJoinAttri(command,readF1,readF2);
        Table table1 = new Table("table1");
        Table table2 = new Table("table2");
        table1.tabulation = readF1.readInfile();
        table2.tabulation = readF2.readInfile();
        int index1 = whichColu1(readF1,command);
        int index2 = whichColu2(readF2,command);
        Table table = getJoin(attri,table1,table2,index1,index2);
        table.printTable();
    }

    public Table getJoin(Row attri,Table table1, Table table2,int index1, int index2) throws IOException {
        Table table = new Table(attri,"Datas/"+currentDb+"/"+"join");
        for (int i = 1; i < table1.tabulation.size(); i++) {
            for (int j = 1; j < table2.tabulation.size(); j++) {
                String str1 = table1.tabulation.get(i).individual.get(index1);
                String str2 = table2.tabulation.get(j).individual.get(index2);
                if(str1.equals(str2)){
                    Row row = new Row();
                    for (int k = 1; k < table1.tabulation.get(i).individual.size(); k++) {
                        row.individual.add(table1.tabulation.get(i).individual.get(k));
                    }
                    for (int k = 1; k < table2.tabulation.get(j).individual.size(); k++) {
                        row.individual.add(table2.tabulation.get(j).individual.get(k));
                    }
                    table.addTo(row);
                }
            }
        }
        return table;
    }

    public int whichColu1(IOFile readF,String[] command) throws IOException {
        int index = 0;
        for (; index < readF.readOneline(0).individual.size(); index++) {
            if(command[5].equals(readF.readOneline(0).getIndividual(index))){
                return index;
            }
        }
        return -1;
    }

    public int whichColu2(IOFile readF,String[] command) throws IOException {
        int index = 0;
        for (; index < readF.readOneline(0).individual.size(); index++) {
            if(command[7].equals(readF.readOneline(0).getIndividual(index))){
                return index;
            }
        }
        return -1;
    }

    public Row getJoinAttri(String[] command,IOFile readF1,IOFile readF2) throws IOException {
        Row result = new Row();
        Row attri1 = readF1.readOneline(0);
        Row attri2 = readF2.readOneline(0);
        result.individual.add("id");
        for (int i = 1; i < attri1.individual.size(); i++) {
            result.individual.add(command[1]+"."+attri1.individual.get(i));
        }
        for (int i = 1; i < attri2.individual.size(); i++) {
            result.individual.add(command[3]+"."+attri2.individual.get(i));
        }
        return result;
    }

    public int attrIndex(Row row,String str){
        for (int i = 0; i < row.getSize(); i++) {
            if(str.equals(row.getIndividual(i))){
                return i;
            }
        }
        return -1;
    }
    
    public boolean contain(int num, int[]b){
        for (int i = 0; i < b.length; i++) {
            if(b[i]==num){
                return true;
            }
        }
        return false;
    }

    public boolean contain(String str, String[]Str){
        for (int i = 0; i < Str.length; i++) {
            if(Str[i].equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean contain(String str, ArrayList<String> strings){
        for (int i = 0; i < strings.size(); i++) {
            if(strings.get(i).equals(str)){
                return true;
            }
        }
        return false;
    }
}
