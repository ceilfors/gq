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
import org.fusesource.jansi.Ansi.Color

/**
 * @author ceilfors
 */
class TimestampCodeFlowPrinter implements CodeFlowPrinter {

    private final CodeFlowPrinter out
    private final long startMilli
    private final Clock clock

    static interface Clock {

        long getCurrentMilli()
    }

    private long currentElapsedMilli() {
        clock.currentMilli - startMilli
    }

    TimestampCodeFlowPrinter(CodeFlowPrinter out, Clock clock) {
        this.out = out
        this.clock = clock
        this.startMilli = clock.currentMilli
    }

    @Override
    void printMethodStart(MethodInfo methodInfo) {
        writeTimestamp()
        out.printMethodStart(methodInfo)
    }

    @Override
    void printMethodEnd() {
        out.printMethodEnd()
    }

    @Override
    void printMethodEnd(Object result) {
        writeTimestamp()
        out.printMethodEnd(result)
    }

    @Override
    void printExpression(ExpressionInfo expressionInfo) {
        writeTimestamp()
        out.printExpression(expressionInfo)
    }

    @Override
    void print(String string) {
        out.print(string)
    }

    @Override
    void printException(ExceptionInfo exceptionInfo) {
        writeTimestamp()
        out.printException(exceptionInfo)
    }

    void writeTimestamp() {
        out.print(sprintf('%4.1fs ', currentElapsedMilli() / 1000).ansi(Color.YELLOW))
    }
}
