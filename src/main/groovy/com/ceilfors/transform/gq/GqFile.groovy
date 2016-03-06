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

import groovy.transform.PackageScope
import org.codehaus.groovy.runtime.StackTraceUtils

import static com.ceilfors.transform.gq.StackTraceUtils.*

/**
 * @author ceilfors
 */
class GqFile implements CodeFlowListener {

    private Stack<MethodInfo> methodCalls = [] as Set

    AutoIndentingPrintWriter writer

    @PackageScope
    GqFile(Writer writer) {
        this.writer = new AutoIndentingPrintWriter(writer, true)
    }

    GqFile(File file) {
        this(new FileCreatingWriter(file))
    }

    void print(text) {
        writer.print(text)
    }

    void println(text) {
        writer.println(text)
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        println("${methodInfo.name}(${methodInfo.args.join(", ")})")
        methodCalls.push(methodInfo)
        writer.indentLevel = methodCalls.size()
    }

    @Override
    void methodEnded(Object result) {
        methodCalls.pop()
        writer.indentLevel = methodCalls.size()
        println("-> $result")
    }

    @Override
    void methodEnded() {
        methodCalls.pop()
        writer.indentLevel = methodCalls.size()
    }

    @Override
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        MethodInfo methodInfo = methodCalls.pop()
        writer.indentLevel = methodCalls.size()

        Throwable exception = exceptionInfo.exception
        StackTraceUtils.sanitizeRootCause(exception)
        sanitizeGeneratedCode(exception)
        def trace = exception.stackTrace.find { it.methodName == methodInfo.name }
        println("!> ${exception.class.simpleName}('${exception.message}') at ${trace.fileName}:${trace.lineNumber}")
    }

    @Override
    Object expressionProcessed(String methodName, ExpressionInfo... expressionInfos) {
        println("${methodName}: " + expressionInfos.collect { "${it.text}=${it.value}" }.join(', '))
        return expressionInfos[0].value
    }
}
