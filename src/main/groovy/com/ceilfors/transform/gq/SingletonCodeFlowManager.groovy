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

    CodeFlowListener codeFlowListener = new GqFile(createGqFile(), true)

    private File createGqFile() {
        // By default using "/tmp" instead of using java.io.tmpdir for better user usability
        return new File(System.getProperty("GQTMP", "/tmp"), "gq")
    }

    @Override
    void methodStarted(MethodInfo methodInfo) {
        codeFlowListener.methodStarted(methodInfo)
    }

    @Override
    void methodEnded(Object result) {
        codeFlowListener.methodEnded(result)
    }

    @Override
    void methodEnded() {
        codeFlowListener.methodEnded()
    }

    @Override
    void exceptionThrown(ExceptionInfo exceptionInfo) {
        codeFlowListener.exceptionThrown(exceptionInfo)
    }

    @Override
    Object expressionProcessed(String methodName, ExpressionInfo... expressionInfos) {
        codeFlowListener.expressionProcessed(methodName, expressionInfos)
    }
}
