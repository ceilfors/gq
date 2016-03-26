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

import com.ceilfors.transform.gq.codeflow.CodeFlowPrinter
import com.ceilfors.transform.gq.codeflow.ExceptionInfo
import com.ceilfors.transform.gq.codeflow.ExpressionInfo
import com.ceilfors.transform.gq.codeflow.MethodInfo
import org.fusesource.jansi.Ansi.Color
/**
 * @author ceilfors
 */
class SyntaxConvertingCodeFlowPrinter implements CodeFlowPrinter {

    private PrintWriter out
    private SyntaxConverter syntaxConverter

    SyntaxConvertingCodeFlowPrinter(SyntaxConverter syntaxConverter, PrintWriter writer) {
        this.out = writer
        this.syntaxConverter = syntaxConverter
    }

    @Override
    void printMethodStart(MethodInfo methodInfo) {
        out.print(methodInfo.name.ansi(Color.GREEN))

        out.print('(')
        out.print(methodInfo.args.collect { convertExpressionValue(it) }.join(', '))
        out.println(')')
    }

    @Override
    void printMethodEnd() {
    }

    @Override
    void printMethodEnd(Object result) {
        out.print('-> '.ansi(Color.GREEN))
        out.println(convertExpressionValue(result))
    }

    @Override
    void printExpression(ExpressionInfo expressionInfo) {
        out.print("${expressionInfo.methodName}: ${expressionInfo.text.replace("\n", "")}=")
        out.print(convertExpressionValue(expressionInfo.value))
        out.println()
    }

    @Override
    void print(String string) {
        out.print(string)
    }

    @Override
    void printException(ExceptionInfo exceptionInfo) {
        Throwable exception = exceptionInfo.exception
        String decoratedMethodName = 'decorated$' + exceptionInfo.methodName
        def trace = exception.stackTrace.find { it.methodName == decoratedMethodName }

        out.print("!> ${exception.class.simpleName}('${exception.message}')".ansi(Color.RED))
        out.println(" at ${trace.fileName}:${trace.lineNumber}")
    }

    private String convertExpressionValue(Object expressionValue) {
        syntaxConverter.convertExpressionValue(expressionValue).ansi(Color.CYAN)
    }
}
