/*
 * Copyright 2016 Wisen Tanasa
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ceilfors.transform.gq

import org.codehaus.groovy.runtime.StackTraceUtils

/**
 * @author ceilfors
 */
class GqFile implements CodeFlowListener {

    public static String TEMP_DIR = "GQTMP"

    private Stack<MethodInfo> methodCalls = [] as Set
    private String directory

    GqFile(String directory) {
        this.directory = directory
    }

    GqFile() {
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        file.append(" " * (methodCalls.size() * 2))
        file.append("${methodInfo.name}(${methodInfo.args.join(", ")})")
        file.append("\n")

        methodCalls.push(methodInfo)
    }

    @Override
    void methodEnded(Object result) {
        methodCalls.pop()

        file.append(" " * (methodCalls.size() * 2))
        file.append("-> $result")
        file.append("\n")
    }

    @Override
    void methodEnded() {
        methodCalls.pop()
    }

    @Override
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        MethodInfo methodInfo = methodCalls.pop()

        file.append(" " * (methodCalls.size() * 2))
        Throwable exception = exceptionInfo.exception
        StackTraceUtils.sanitizeRootCause(exception)
        sanitizeGeneratedCode(exception)
        def trace = exception.stackTrace.find { it.methodName == methodInfo.name }
        file.append("!> ${exception.class.simpleName}('${exception.message}') at ${trace.fileName}:${trace.lineNumber}")
        file.append("\n")
    }

    @Override
    Object expressionProcessed(ExpressionInfo expressionInfo) {
        file.append(" " * (methodCalls.size() * 2))
        file.append("${expressionInfo.methodName}: ${expressionInfo.text}=${expressionInfo.value}")
        file.append("\n")

        return expressionInfo.value
    }

    /**
     * Sanitize generated code from stack trace.
     * @param t the throwable to be sanitized
     */
    private static sanitizeGeneratedCode(Throwable t) {
        StackTraceElement[] trace = t.stackTrace
        List<StackTraceElement> newTrace = [] as LinkedList
        StackTraceElement traceWithoutLineNumber = null
        for (int i = trace.size() - 1; i >= 0; i--) {
            def currentTrace = trace[i]
            if (currentTrace.lineNumber != -1) {
                if (traceWithoutLineNumber) {
                    newTrace.add(0, new StackTraceElement(traceWithoutLineNumber.className, traceWithoutLineNumber.methodName, traceWithoutLineNumber.fileName, currentTrace.lineNumber))
                    traceWithoutLineNumber = null
                } else {
                    newTrace.add(0, currentTrace)
                }

            } else if (currentTrace.fileName != null // Generated code from Groovy?
                    && traceWithoutLineNumber == null) { // Ignore trace without line number in between
                traceWithoutLineNumber = currentTrace
            }
        }

        StackTraceElement[] clean = new StackTraceElement[newTrace.size()]
        newTrace.toArray(clean)
        t.setStackTrace(clean)
        return t
    }

    File getFile() {
        String gqDir = directory
        if (!gqDir) {
            // By default not using java.io.tmpdir for better user usability
            gqDir = System.getProperty(TEMP_DIR) ? System.getProperty(TEMP_DIR) : "/tmp"
        }

        def file = new File(gqDir, "gq")
        if (!file.exists()) {
            file.createNewFile()
        }
        return file
    }
}
