//This class is used to store the data of each row in the table
import java.util.ArrayList;

public class Row {
    public ArrayList<String> individual = new ArrayList<>();

    Row(String[] inputs) {
        for (int i = 0; i < inputs.length; i++) {this.individual.add(inputs[i]);}
    }

    Row(){}

    public int getSize()
    {
        return individual.size();
    }

    public ArrayList<String> getoneRow() {
        return individual;
    }

    public String getIndividual(int colNum) {
        return individual.get(colNum);
    }

    public void addto(String str){
        individual.add(str);
    }

}
