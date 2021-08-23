import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

public class Condition {

    public Condition() {}

    public boolean parseCondition(String[] command,File file) throws IOException {
        int index = 0;
        int num1= 0;
        int num2= 0;
        IOFile fileR = new IOFile(file);
        for (; index < command.length; index++) {
            for (int n = 0; n < command[index].length(); n++) {
                if(command[index].charAt(n)=='('){num1+=1;}
            }
            for (int n = 0; n < command[index].length(); n++) {
                if(command[index].charAt(n)==')'){num2+=1;}
            }
            if(num1==num2&&("AND".equals(command[index])|| "OR".equals(command[index]))){
                break;
            }
        }
        if(index == command.length){
            if(!contain(command[0],fileR.readOneline(0))){
                System.out.println("[ERROR]: Attribute does not exist");
                return false;
            }
            for (int i = 0; i < command.length; i++) {
                for (int j = 0; j < command[i].length(); j++) {
                    if(command[i].charAt(j)=='('||command[i].charAt(j)==')'){
                        System.out.println("[ERROR]: Invalid query");
                        return false;
                    }
                }
            }
            return true;
        }
        String[] commandL = new String[index];
        String[] commandR = new String[command.length-index-1];
        for (int j = 0; j < commandL.length; j++) {
            commandL[j] = command[j];
        }
        for (int k = index+1; k < command.length; k++) {
            commandR[k-index-1] = command[k];
        }
        removeBrkt(commandL);
        removeBrkt(commandR);
        return (parseCondition(commandL,file)&&parseCondition(commandR,file));
    }

    public int[] impleCondition(String[] command,File file) throws IOException {
        int index = 0;
        int num1= 0;
        int num2= 0;
        for (; index < command.length; index++) {
            for (int n = 0; n < command[index].length(); n++) {
                if(command[index].charAt(n)=='('){num1+=1;}
            }
            for (int n = 0; n < command[index].length(); n++) {
                if(command[index].charAt(n)==')'){num2+=1;}
            }
            if(num1==num2&&("AND".equals(command[index])|| "OR".equals(command[index]))){
                break;
            }
        }
        if(index == command.length){return implementSingleCon(command,file);}
        String[] commandL = new String[index];
        String[] commandR = new String[command.length-index-1];
        for (int j = 0; j < commandL.length; j++) {commandL[j] = command[j];}
        for (int k = index+1; k < command.length; k++) {commandR[k-index-1] = command[k];}
        removeBrkt(commandL);
        removeBrkt(commandR);
        if("AND".equals(command[index])){return And(impleCondition(commandL,file),impleCondition(commandR,file));}
        if("OR".equals(command[index])){return Or(impleCondition(commandL,file),impleCondition(commandR,file));}
        return new int[]{-3};
    }


    public int[] implementSingleCon(String[] command, File file) throws IOException {
        Table table = new Table("Selected");
        IOFile fileR = new IOFile(file);
        Row row = fileR.readOneline(0);
        table.tabulation = fileR.readInfile();
        int index = 0;
        for (;index < row.getoneRow().size(); index++) {
            if(row.getIndividual(index).equals(command[0])){break;}
        }
        return operate(command,table,index);
    }

    public int[] operate(String[] command, Table table, int index){
        ArrayList<String> result = new ArrayList<String>();
        result.add("0");
        StringBuffer object = new StringBuffer();
        for (int i = 2; i < command.length; i++) {
            object.append(" ");
            object.append(command[i]);
        }
        object.delete(0,1);
        if(object.indexOf("'")!=-1){
            object.delete(0,1);
            object.delete(object.length()-1,object.length());
        }

        if("==".equals(command[1])){
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(table.tabulation.get(i).getIndividual(index).contentEquals(object)){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }

        if("!=".equals(command[1])){
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!table.tabulation.get(i).getIndividual(index).contentEquals(object)){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }

        if(">=".equals(command[1])){
            if(!isNumber(command[2])){return new int[]{-1};}
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!isNumber(table.tabulation.get(i).getIndividual(index))){return new int[]{-1};}
                float num1 = Float.parseFloat(table.tabulation.get(i).getIndividual(index));
                float num2 = Float.parseFloat(command[2]);
                if(num1>=num2){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }
        if("<=".equals(command[1])){
            if(!isNumber(command[2])){return new int[]{-1};}
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!isNumber(table.tabulation.get(i).getIndividual(index))){return new int[]{-1};}
                float num1 = Float.parseFloat(table.tabulation.get(i).getIndividual(index));
                float num2 = Float.parseFloat(command[2]);
                if(num1<=num2){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }
        if("<".equals(command[1])){
            if(!isNumber(command[2])){return new int[]{-1};}
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!isNumber(table.tabulation.get(i).getIndividual(index))){return new int[]{-1};}
                float num1 = Float.parseFloat(table.tabulation.get(i).getIndividual(index));
                float num2 = Float.parseFloat(command[2]);
                if(num1<num2){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }
        if(">".equals(command[1])){
            if(!isNumber(command[2])){return new int[]{-1};}
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!isNumber(table.tabulation.get(i).getIndividual(index))){return new int[]{-1};}
                float num1 = Float.parseFloat(table.tabulation.get(i).getIndividual(index));
                float num2 = Float.parseFloat(command[2]);
                if(num1>num2){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }
        if("LIKE".equals(command[1])){
            if(command[2].codePointAt(0)==39){command[2]=command[2].substring(1,command[2].length()-1);}
            if(!isString(command[2])){return new int[]{-2};}
            for (int i = 1; i < table.tabulation.size(); i++) {
                if(!isString(table.tabulation.get(i).getIndividual(index))){return new int[]{-2};}
                if(isLike(command[2],table.tabulation.get(i).getIndividual(index))){
                    result.add(table.tabulation.get(i).getIndividual(0));
                }
            }
            return ArrayconvertInt(result);
        }
        return new int[]{0};
    }

    public int[] ArrayconvertInt(ArrayList<String> result){
        int[] Result = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            Result[i] = Integer.parseInt(result.get(i));
        }
        return Result;
    }

    public boolean isNumber(String str){
        for (int i = 0; i < str.length(); i++) {
            if(!Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public boolean isString(String str){
        for (int i = 0; i < str.length(); i++) {
            if(Character.isDigit(str.charAt(i))){
                return false;
            }
        }
        return true;
    }

    public void removeBrkt(String[] command){
        command [0] = command[0].substring(1,command[0].length());
        int index = command.length-1;
        command[index] = command[index].substring(0,command[index].length()-1);
    }

    public int[] And(int[]a,int[] b){
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < a.length; i++) {
            if(a[i]==-1){return new int[]{-1};}
            if(a[i]==-2){return new int[]{-2};}
            if(contain(a[i],b)){
                result.add(a[i]);
            }
        }
        Collections.sort(result);
        int[] Result = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            Result[i] = result.get(i);
        }
        return Result;
    }

    public int[] Or(int[]a,int[] b){
        ArrayList<Integer> result = new ArrayList<>();
        for (int i = 0; i < b.length; i++) {
            if(b[i]==-1){return new int[]{-1};}
            if(b[i]==-2){return new int[]{-2};}
            result.add(b[i]);
        }
        for (int i = 0; i < a.length; i++) {
            if(a[i]==-1){return new int[]{-1};}
            if(a[i]==-2){return new int[]{-2};}
            if(!contain(a[i],b)){
                result.add(a[i]);
            }
        }
        Collections.sort(result);
        int[] Result = new int[result.size()];
        for (int i = 0; i < result.size(); i++) {
            Result[i] = result.get(i);
        }
        return Result;
    }

    public boolean contain(int num, int[]b){
        for (int i = 0; i < b.length; i++) {
            if(b[i]==num){
                return true;
            }
        }
        return false;
    }

    public boolean contain(String str, Row row){
        ArrayList<String> strings= row.getoneRow();
        for (int i = 0; i < strings.size(); i++) {
            if(strings.get(i).equals(str)){
                return true;
            }
        }
        return false;
    }

    public boolean isLike(String str, String Str){
        if (str.equals(Str)){return true;}
        if(str.length()>=Str.length()){return false;}
        for (int i = 0; i <= Str.length()-str.length(); i++) {
            if(str.equals(Str.substring(i,i+str.length()))){
                return true;
            }
        }
        return false;
    }
}
