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

    static MethodCallExpression expressionProcessed(Expression expressionInfo) {
        String method = (SingletonCodeFlowManager.INSTANCE.&expressionProcessed as MethodClosure).method
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), method, args(expressionInfo))
    }

    static MethodCallExpression methodStarted(Expression methodInfo) {
        String method = (SingletonCodeFlowManager.INSTANCE.&methodStarted as MethodClosure).method
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), method, args(methodInfo))
    }

    static MethodCallExpression methodEnded(Expression result) {
        String method = (SingletonCodeFlowManager.INSTANCE.&methodEnded as MethodClosure).method
        Expression args = result == null ? args(Parameter.EMPTY_ARRAY) : args(result)
        new MethodCallExpression(propX(classX(SingletonCodeFlowManager), "INSTANCE"), method, args)
    }
}
