package com.github.joergpfruender;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.openqa.selenium.WebDriver;

import java.util.Optional;

public class RecordVideoOnFailedTestExtension implements AfterEachCallback {

    @Override
    public void afterEach(ExtensionContext extensionContext) {
        if (!extensionContext.getExecutionException().isPresent()) {
            return;
        }

        String testClassName = extensionContext.getTestClass()
                .flatMap(className -> Optional.of(className.getName())).orElse("Unknown_test_class_name");

        String testMethodName = extensionContext.getTestMethod()
                .flatMap(methodName -> Optional.of(methodName.getName())).orElse("Unknown_test_name");

        WebDriver webDriver = getWebDriver(extensionContext.getTestInstance());

        if (webDriver instanceof VideoRecordingWebDriver) {
            notifyTestFailed(testClassName, testMethodName, (VideoRecordingWebDriver) webDriver, extensionContext.getExecutionException().get());
        }
    }

    private WebDriver getWebDriver(Object testInstance) {
        return asHasWebDriver(testInstance).getWebDriver();
    }

    private HasWebDriver asHasWebDriver(Object testInstance) {
        if (testInstance instanceof Optional) {
            return (HasWebDriver) ((Optional) testInstance).get();
        } else {
            return (HasWebDriver) testInstance;
        }
    }

    private void notifyTestFailed(String testClassName, String testMethodName, VideoRecordingWebDriver videoRecordingWebDriver, Throwable executionException) {
        videoRecordingWebDriver.onTestFailed(new FailingTestInfo(testClassName, testMethodName, executionException));
    }

}
