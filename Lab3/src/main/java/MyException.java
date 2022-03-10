import lombok.Data;

@Data
public class MyException extends Exception{ // Exception в котором есть поле для возврата значения
    private Opr returnVariable;
    MyException(Opr returnVar){
        this.returnVariable = returnVar;
    }
}
