package com.github.joergpfruender;

import java.util.Optional;

public class FailingTestInfo
{

    String testName;
    String failingTestMethodName;
    Throwable executionException;

    FailingTestInfo(String testName, String failingTestMethodName, Throwable executionException) {
        this.testName = testName;
        this.failingTestMethodName = failingTestMethodName;
        this.executionException = executionException;
    }
}
