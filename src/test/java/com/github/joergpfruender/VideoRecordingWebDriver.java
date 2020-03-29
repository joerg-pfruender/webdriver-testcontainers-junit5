package com.github.joergpfruender;

import org.testcontainers.containers.BrowserWebDriverContainer;
import org.testcontainers.lifecycle.TestDescription;

import java.util.Optional;

public class VideoRecordingWebDriver extends DelegatingWebDriver {

    private BrowserWebDriverContainer container;

    private Optional<FailingTestInfo> failingTestInfo = Optional.empty();

    private VideoRecordingWebDriver(BrowserWebDriverContainer container) {
        super(container.getWebDriver());
        this.container = container;
    }

    public static VideoRecordingWebDriver create(BrowserWebDriverContainer container) {
        container.start();
        return new VideoRecordingWebDriver(container);
    }

    @Override
    public void quit() {
        if (hasFailed()) {

            FailingTestInfo failingTestInfo = this.failingTestInfo.get();
            container.afterTest(new TestDescription() {
                @Override
                public String getTestId() {
                    return failingTestInfo.testName;
                }

                @Override
                public String getFilesystemFriendlyName() {
                    return failingTestInfo.testName + "_" + failingTestInfo.failingTestMethodName;
                }
            }, Optional.of(failingTestInfo.executionException));
        }
        super.quit();
        container.stop();
    }

    public boolean hasFailed() {
        return failingTestInfo.isPresent();
    }

    public void onTestFailed(FailingTestInfo failingTestInfo) {
        this.failingTestInfo = Optional.of(failingTestInfo);
    }

}
