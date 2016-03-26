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

package com.ceilfors.groovy.gq

import com.ceilfors.groovy.gq.codeflow.CodeFlowPrinter
import com.ceilfors.groovy.gq.codeflow.ExceptionInfo
import com.ceilfors.groovy.gq.codeflow.ExpressionInfo
import com.ceilfors.groovy.gq.codeflow.MethodInfo

/**
 * @author ceilfors
 */
class IndentingCodeFlowPrinter implements CodeFlowPrinter {

    String indent = "  "
    int indentLevel = 0
    private CodeFlowPrinter out

    IndentingCodeFlowPrinter(CodeFlowPrinter out) {
        this.out = out
    }

    @Override
    void printMethodStart(MethodInfo methodInfo) {
        indent()
        out.printMethodStart(methodInfo)
        indentLevel++
    }

    @Override
    void printMethodEnd() {
        indentLevel--
    }

    @Override
    void printMethodEnd(Object result) {
        indentLevel--
        indent()
        out.printMethodEnd(result)
    }

    @Override
    void printExpression(ExpressionInfo expressionInfo) {
        indent()
        out.printExpression(expressionInfo)
    }

    @Override
    void print(String string) {
        out.print(string)
    }

    @Override
    void printException(ExceptionInfo exceptionInfo) {
        indentLevel--
        indent()
        out.printException(exceptionInfo)
    }

    void indent() {
        out.print(indent * indentLevel)
    }
}
