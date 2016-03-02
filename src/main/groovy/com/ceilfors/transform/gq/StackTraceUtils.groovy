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
class StackTraceUtils {

    /**
     * Sanitize generated code from stack trace.
     * @param t the throwable to be sanitized
     */
    public static sanitizeGeneratedCode(Throwable t) {
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
}
