import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Robot {
    private List<String> fileMaze = new ArrayList<>();
    private int x;
    private int y;
    private String[][] matrix; // space = " ", wall = "#", robot = "R", exit = "X" portal = "O"
    Pair<Integer, Integer> coordinates;
    Pair<Integer, Integer> exitCoord;
    protected boolean inExit(){
        return coordinates.equals(exitCoord);
    }
    public void printMaze(){
        for (int i = 0; i < this.y; ++i){
            for (int j = 0; j < this.x; ++j){
                System.out.print(matrix[i][j]);
            }
            System.out.println("");
        }
        System.out.println("---------------");
    }
    public int toTOP(){
        if (coordinates.getValue() <= this.y || matrix[coordinates.getValue() - 1][coordinates.getKey()].equals("#")) {
            return 0;
        } else {
            coordinates = new Pair<>(coordinates.getKey(), coordinates.getValue() - 1);
            matrix[coordinates.getValue()][coordinates.getKey()] = "R";
            matrix[coordinates.getValue() + 1][coordinates.getKey()] = " ";
        }
        printMaze();
        if (inExit()){
            System.out.println("Robot out");
        }
        return 1;
    }
    public int toBOTTOM(){
        if (coordinates.getValue() >= this.y || matrix[coordinates.getValue() + 1][coordinates.getKey()].equals("#")){
            return 0;
        } else {
            coordinates = new Pair<>(coordinates.getKey(), coordinates.getValue() + 1);
            matrix[coordinates.getValue()][coordinates.getKey()] = "R";
            matrix[coordinates.getValue() - 1][coordinates.getKey()] = " ";
        }
        printMaze();
        if (inExit()){
            System.out.println("Robot out");
        }
        return 1;
    }
    public int toRIGHT(){
        if (coordinates.getKey() >= this.x || matrix[coordinates.getValue()][coordinates.getKey() + 1].equals("#")){
            return 0;
        } else {
            coordinates = new Pair<>(coordinates.getKey() + 1, coordinates.getValue());
            matrix[coordinates.getValue()][coordinates.getKey()] = "R";
            matrix[coordinates.getValue()][coordinates.getKey() - 1] = " ";
        }
        printMaze();
        if (inExit()){
            System.out.println("Robot out");
        }
        return 1;
    }
    public int toLEFT(){
        if (coordinates.getKey() <= this.x || matrix[coordinates.getValue()][coordinates.getKey() - 1].equals("#")){
            return 0;
        } else {
            coordinates = new Pair<>(coordinates.getKey() - 1, coordinates.getValue());
            matrix[coordinates.getValue()][coordinates.getKey()] = "R";
            matrix[coordinates.getValue()][coordinates.getKey() + 1] = " ";
        }
        printMaze();
        if (inExit()){
            System.out.println("Robot out");
        }
        return 1;
    }
    public Robot(String fileName){
        try {
            File file = new File(fileName);
            FileReader fr = new FileReader(file);
            BufferedReader reader = new BufferedReader(fr);
            String line = reader.readLine();
            while (line != null){
                fileMaze.add(line);
                line = reader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.y = fileMaze.size();
        this.x = fileMaze.get(0).length();
        this.matrix = new String[this.y][this.x];
        for (int i = 0; i < this.y; ++i){
            String str = fileMaze.get(i);
            for (int j = 0; j < this.x; ++j){
                this.matrix[i][j] = String.valueOf(str.charAt(j));
                if (this.matrix[i][j].equals("R")){
                    this.coordinates = new Pair<>(j, i);
                }
                if (this.matrix[i][j].equals("X")){
                    this.exitCoord = new Pair<>(j, i);
                }
            }
        }

    }
}
