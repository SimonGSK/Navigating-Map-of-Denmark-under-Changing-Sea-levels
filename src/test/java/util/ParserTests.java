package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import Interfaces.IParser;
import models.osm.Member;
import models.osm.Element;
import models.osm.Node;
import models.osm.Relation;
import models.osm.Way;
import util.extensions.DependsOn;
import util.extensions.DependsOnExtension;
import util.models.ParserResults;

@ExtendWith(DependsOnExtension.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

public class ParserTests {
    static final List<ParserResults> parserResults = new ArrayList<>();

    static {
        try {
            parserResults.add(new ParserResults("bornholm.osm", "bornholm.json"));
        } catch (Exception e) {
            throw new  ExceptionInInitializerError("An exception occured while creating parser");

        }
    }
}
