package util.models;

import models.parser.Parser;
import Interfaces.IParser;
import util.TestParser;

public class ParserResults {
    IParser actualParser;
    IParser expectedParser;
    String name;

    public ParserResults(String osmFile, String jsonFile){
        actualParser = new Parser(osmFile);
        expectedParser = new TestParser(jsonFile);
        name = osmFile;

        actualParser.parse();
        expectedParser.parse();
    }

    public IParser getActualParser() {
        return actualParser;
    }
    public IParser getExpectedParser() {
        return expectedParser;
    }
    public String getName() {
        return name;
    }
}
