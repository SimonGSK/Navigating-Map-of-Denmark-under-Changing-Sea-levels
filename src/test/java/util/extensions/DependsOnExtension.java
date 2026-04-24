package util.extensions;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DependsOnExtension implements BeforeTestExecutionCallback, AfterTestExecutionCallback {
    private static final Map<String, Boolean> testResults = new HashMap<>();

    @Override
    public void beforeTestExecution(ExtensionContext context) throws Exception {
        DependsOn dependsOn = context.getTestMethod()
        .flatMap(m -> Optional.ofNullable(m.getAnnotation(DependsOn.class)))
        .orElse(null);

        if (dependsOn != null) {
            boolean dependenciesPassed = testResults.getOrDefault(dependsOn.value(), false);
            Assumptions.assumeTrue(dependenciesPassed, () -> "Skipping" + context.getDisplayName() + "because" + dependsOn.value() + "failed");
        }
    }
    @Override
    public void afterTestExecution(ExtensionContext context) throws Exception {
        boolean testPassed = context.getExecutionException().isEmpty();
        testResults.put(context.getTestMethod().get().getName(), testPassed);
    }

}
