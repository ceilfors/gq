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

/**
 * @author ceilfors
 */
enum SingletonCodeFlowManager implements CodeFlowListener {

    INSTANCE;

    /**
     * Configure the temporary directory that gq should use. It goes to /tmp by default.
     */
    public static final String GQ_TMP = "gq.tmp"

    private CodeFlowPrinter codeFlowPrinter

    private File createGqFile() {
        // By default using "/tmp" instead of using java.io.tmpdir for better user usability
        return new File(System.getProperty(GQ_TMP, "/tmp"), "gq")
    }

    def setGqFile(File file, boolean timestamp) {
        codeFlowPrinter = new IndentingCodeFlowPrinter(new SyntaxConvertingCodeFlowPrinter(
                new StringSyntaxConverter(),
                new PrintWriter(new FileCreatingWriter(file)))
        )
        if (timestamp) {
            codeFlowPrinter = new TimestampCodeFlowPrinter(codeFlowPrinter, System.&currentTimeMillis)
        }
    }

    {
        setGqFile(createGqFile(), true)
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        codeFlowPrinter.printMethodStart(methodInfo)
    }

    @Override
    void methodEnded(Object result) {
        codeFlowPrinter.printMethodEnd(result)
    }

    @Override
    void methodEnded() {
        codeFlowPrinter.printMethodEnd()
    }

    @Override
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        codeFlowPrinter.printException(exceptionInfo)
    }

    @Override
    Object expressionProcessed(String methodName, ExpressionInfo expressionInfo) {
        codeFlowPrinter.printExpression(expressionInfo)
        return expressionInfo.value
    }
}
