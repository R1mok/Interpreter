import lombok.Data;

@Data
public class Program extends Construction {
    private VarFunctionsContext context = new VarFunctionsContext();
    public Program() {}
    public Program(VarFunctionsContext context){
        this.context = context;
    }
}
