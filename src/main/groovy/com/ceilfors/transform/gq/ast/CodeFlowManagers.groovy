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

package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.SingletonCodeFlowManager
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.runtime.MethodClosure

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

/**
 * @author ceilfors
 */
class CodeFlowManagers {

    static MethodCallExpression expressionProcessed(String methodName, Expression... expressionInfos) {
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), "expressionProcessed", args(constX(methodName), *expressionInfos))
    }

    static MethodCallExpression methodStarted(Expression methodInfo) {
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), "methodStarted", args(methodInfo))
    }

    static MethodCallExpression methodEnded(Expression result) {
        Expression args = result == null ? args(Parameter.EMPTY_ARRAY) : args(result)
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), "methodEnded", args)
    }

    static MethodCallExpression exceptionThrown(Expression exceptionInfo) {
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), "exceptionThrown", args(exceptionInfo))
    }
}
