import lombok.Data;

@Data
public class MyException extends Exception{
    private Opr returnVariable;
    MyException(Opr returnVar){
        this.returnVariable = returnVar;
    }
}
