package org.example;

import java.util.List;

public interface MultiNodeElement<T extends Element> {
    MBR calcMBR(List<T> elements);
}
