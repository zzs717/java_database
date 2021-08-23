import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Parser {
    File file;
    String input;
    String[] command;
    String[] condition;
    int[] attributeIndex;
    String currentDb;
    Implementer implementer;

    public Parser() {implementer = new Implementer();}

    public boolean parse(String input) throws IOException {
        this.input = input;
        if(!preJudge()){
            return false;
        }
        if("CREATE".equals(command[0])){return parseCreate();}
        if("USE".equals(command[0])){return parseUse();}
        if("DROP".equals(command[0])){return parseDrop();}
        if("ALTER".equals(command[0])){return parseAlter();}
        if("SELECT".equals(command[0])){return parseSelect();}
        if("UPDATE".equals(command[0])){return parseUpdate();}
        if("DELETE".equals(command[0])){return parseDelete();}
        if("JOIN".equals(command[0])){return parseJoin();}
        if("INSERT".equals(command[0])){return parseInsert();}
        else {
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
    }

    public void implement() throws IOException {
        if("CREATE".equals(command[0])){impleCreate();}
        if("USE".equals(command[0])){impleUse();}
        if("DROP".equals(command[0])){impleDrop();}
        if("ALTER".equals(command[0])){impleAlert();}
        if("SELECT".equals(command[0])){impleSelect();}
        if("UPDATE".equals(command[0])){impleUpdata();}
        if("DELETE".equals(command[0])){impleDelete();}
        if("JOIN".equals(command[0])){impleJoin();}
        if("INSERT".equals(command[0])){impleInsert();}
    }

    //Remove the semicolon at the end of command
    public boolean preJudge(){
        for (int i = 0; i < input.length(); i++) {
            if(input.charAt(i)!=' '){
                input=input.substring(i);
                break;
            }
        }
        if(input.charAt(input.length() - 1)==';'){
            input = input.substring(0, input.length()-1);
            command = input.split(" ");
        }
        else {
            System.out.println("[ERROR]: Semi colon missing at end of line");
            return false;
        }
        int num1= 0;int num2= 0;int num3= 0;
        for (int i = 0; i < input.length(); i++) {
            if(input.charAt(i)=='('){num1++;}
            if(input.charAt(i)==')'){num2++;}
            if(input.codePointAt(i)==39){num3++;}
        }
        if(num1!=num2||num3%2!=0){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        for (int i = 0; i < command.length; i++) {
            if(command[i].charAt(0)==','){command[i] = command[i].substring(1,command[i].length());}
            if(command[i].charAt(command[i].length()-1)==','){
                command[i] = command[i].substring(0,command[i].length()-1);
            }
        }
        return true;
    }

    public boolean parseCreate() {
        if ("DATABASE".equals(command[1])) {
            if (command.length != 3) {
                System.out.println("[ERROR]:Did not specify the name of the database");
                return false;
            }
        }
        if ("TABLE".equals(command[1])) {
            if(command.length==3){return true;}
            if(command[3].charAt(0)!='('){
                System.out.println("[ERROR]:Inviald query");
                return false;
            }
        }
        return true;
    }

    public void impleCreate() throws IOException {
        if("DATABASE".equals(command[1])){implementer.creatDb(command);}
        if("TABLE".equals(command[1])){implementer.creatTable(input,command);}
    }

    public boolean parseUse(){
        if(command.length!=2){
            System.out.println("[ERROR]:Did not specify the name of the database");
            return false;
        }
        File file = new File("Datas/"+command[1]);
        if(!file.exists()){
            System.out.println("[ERROR]:Database does not exist");
            return false;
        }
        else {return true;}
    }

    public void impleUse() {
        currentDb = command[1];
        implementer.useDb(command);
    }

    public boolean parseInsert() {
        if (!"INTO".equals(command[1])) {
            System.out.println("[ERROR]: Invalid query");
        } else if (!"VALUES".equals(command[3])) {
            System.out.println("[ERROR]: Invalid query");
        } else {
            File file = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
            if (!file.exists()) {
                System.out.println("[ERROR]:Table does not exist");
                return false;
            }
            return true;
        }
        return false;
    }

    public void impleInsert() throws IOException {
        implementer.insertRow(input,command);
    }

    public boolean parseSelect() throws IOException {
        for (int i = 0; i < command.length; i++) {
            if("WHERE".equals(command[i])){return parseSelectWithCon(i+1);}
        }
        for (int i = 0; i < command.length; i++) {
            if("FROM".equals(command[i])){
                if(i+2<command.length){
                    System.out.println("[ERROR]: Invalid query");
                    return false;
                }
            }
        }
        return parseSelectUncon();
    }

    public boolean parseSelectWithCon(int Index) throws IOException {
        file = new File("Datas"+File.separator+currentDb+File.separator+command[Index-2]);
        if(!file.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        int from = 0;
        for (; from < command.length; from++) {
            if("FROM".equals(command[from])){break;}
        }
        attributeIndex = getAttriIndex(from);
        if(attributeIndex[0]==-1){
            System.out.println("[ERROR]: Attribute does not exist");
            return false;
        }
        condition = new String[command.length-Index];
        int index = 0;
        for (; index < command.length; index++) {if("FROM".equals(command[index])){break;}}
        if("FROM".equals(command[index])&& "WHERE".equals(command[index+2])){
            for (int i = 0; i < condition.length; i++) {condition[i] = command[i+Index];}
            if(!new Condition().parseCondition(condition,file)){
                return false;
            }
        }
        else {
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        int[] Id = new Condition().impleCondition(condition,file);
        return parseCondition(Id);
    }

    public boolean parseCondition(int[] Id){
        if(Id[0]==-1){
            System.out.println("[ERROR]: Attribute cannot be converted to number");
            return false;
        }
        if(Id[0]==-2){
            System.out.println("[ERROR]: String expected");
            return false;
        }
        return true;
    }

    public boolean parseSelectUncon() throws IOException {
        file = new File("Datas"+File.separator+currentDb+File.separator+command[command.length-1]);
        if(!file.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        int from = 0;
        for (; from < command.length; from++) {
            if("FROM".equals(command[from])){break;}
        }
        attributeIndex = getAttriIndex(from);
        if(attributeIndex[0]==-1){
            System.out.println("[ERROR]: Attribute does not exist");
            return false;
        }
        return true;
    }

    public void impleSelect() throws IOException {
        int from = 0;
        for (; from < command.length; from++) {
            if("FROM".equals(command[from])){break;}
        }
        attributeIndex = getAttriIndex(from);
        boolean haveCon = false;
        for (int i = 0; i < command.length; i++) {
            if("WHERE".equals(command[i])){
                haveCon = true;
                break;
            }
        }
        if(haveCon){implementer.selectRowWithcon(file,condition,attributeIndex);}
        else{implementer.selectRowWithoutcon(file,attributeIndex);}
    }

    public boolean parseDrop(){
        if(command.length ==3){
            return true;
        }
        System.out.println("[ERROR]: Invalid query");
        return false;
    }

    public void impleDrop() {
        if ("TABLE".equals(command[1])) {
            file = new File("Datas" +File.separator+ currentDb + File.separator + command[2]);
            file.delete();
        }
        else {
            file = new File("Datas"+ File.separator + command[2]);
            File[] files = file.listFiles();
            if (files.length == 0) {
                file.delete();
                return;
            }
            else {
                for (File file1 : files) {
                    file1.delete();
                }
            }
            file.delete();
        }
    }

    public boolean parseAlter() throws IOException {
        if(!"TABLE".equals(command[1])){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        file = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
        if(!file.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        if(!"DROP".equals(command[3])&&!"ADD".equals(command[3])){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        return true;
    }

    public void impleAlert() throws IOException {
        if("ADD".equals(command[3])){
            implementer.alertAdd(file,command);
        }
        if("DROP".equals(command[3])){
            implementer.alertDrop(file,command);
        }
    }

    public boolean parseUpdate() throws IOException {
        file = new File("Datas"+File.separator+currentDb+File.separator+command[1]);
        if(!file.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        if(!"SET".equals(command[2])){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        IOFile readF = new IOFile(file);
        int index = 0;
        for (; index < command.length; index++) {if("WHERE".equals(command[index])){break;}}
        if(index%3!=0){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        for (int i = 3; i < index; i+=3) {
            if(!contain(command[i],readF.readOneline(0).getoneRow())){
                System.out.println("[ERROR]: Attribute does not exist");
                return false;
            }
        }
        for (int i = 4; i < index; i+=3) {
            if(!"=".equals(command[i])){
                System.out.println("[ERROR]: Invalid query");
                return false;
            }
        }
        condition = new String[command.length-index-1];
        for (int i = 0; i < condition.length; i++) {condition[i] = command[i+index+1];}
        if(!new Condition().parseCondition(condition,file)){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        int[] Id = new Condition().impleCondition(condition,file);
        return parseCondition(Id);
    }

    public void impleUpdata() throws IOException {
        int index = 0;
        for (; index < command.length; index++) {if("WHERE".equals(command[index])){break;}}
        implementer.updata(file,command,index,condition);
    }

    public boolean parseDelete() throws IOException {
        if(!"FROM".equals(command[1])||!"WHERE".equals(command[3])){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        file = new File("Datas"+File.separator+currentDb+File.separator+command[2]);
        if(!file.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        condition = new String[command.length-4];
        for (int i = 0; i < condition.length; i++) {condition[i] = command[i+4];}
        if(!new Condition().parseCondition(condition,file)){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        int[] Id = new Condition().impleCondition(condition,file);
        return parseCondition(Id);
    }

    public void impleDelete() throws IOException {
        implementer.deleteWithCon(file,command,condition);
    }

    public boolean parseJoin() throws IOException {
        if(!"AND".equals(command[2])||!"ON".equals(command[4])||!"AND".equals(command[6])){
            System.out.println("[ERROR]: Invalid query");
            return false;
        }
        File file1 = new File("Datas"+File.separator+currentDb+File.separator+command[1]);
        File file2 = new File("Datas"+File.separator+currentDb+File.separator+command[3]);
        IOFile readF1 = new IOFile(file1);
        IOFile readF2 = new IOFile(file2);
        if(!file1.exists()||!file2.exists()){
            System.out.println("[ERROR]: Table does not exist");
            return false;
        }
        if(!contain(command[5],readF1.readOneline(0).individual)||
        !contain(command[7],readF2.readOneline(0).getoneRow())){
            System.out.println("[ERROR]: Attribute does not exist");
            return false;
        }
        return true;
    }

    public void impleJoin() throws IOException{
        implementer.join(command);
    }

    public int[] getAttriIndex(int index) throws IOException {
        IOFile fileR = new IOFile(file);
        Row attribute = fileR.readOneline(0);
        int[] result;
        if("*".equals(command[1])){
            result = new int[attribute.getSize()];
            for (int i = 0; i < attribute.getSize(); i++) {
                result[i] = i;
            }
        }
        else {
            result = new int[index-1];
            for (int i = 1; i < index; i++) {
                if(command[i].charAt(command[i].length()-1)==','){command[i]=command[i].substring(0,command[i].length()-1);}
                if(!contain(command[i],attribute.getoneRow())){return new int[]{-1};}
                else {
                    for (int j = 0; j < attribute.getoneRow().size(); j++) {
                        if(command[i].equals(attribute.getIndividual(j))){result[i-1]=j;}
                    }
                }
            }
        }
        return result;
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
