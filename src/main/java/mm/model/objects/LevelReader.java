package mm.model.objects;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;

public class LevelReader {
    private ObjectMapper mapper;
    private File inputFile;

    public LevelReader(String filePath, String fileName) {
        mapper = new ObjectMapper();
        inputFile = new File(filePath+fileName);
    }

    public ObjectMapper getMapper() {return this.mapper;}
    //no setMapper necessary for now

    public File getFile() {return this.inputFile;}
    public void setFile(String filePath, String fileName) {this.inputFile = new File(filePath+fileName);}

    public Level readFile() {
        try {
            Level level = mapper.readValue(inputFile, Level.class);
            return level;
        } catch (Exception e) {
            System.err.println("Error: " + e + " occured while trying to read File. Returning null.");
            return null;
        }
    }
}
