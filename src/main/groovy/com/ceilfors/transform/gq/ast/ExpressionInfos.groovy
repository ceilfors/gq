package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.ExpressionInfo
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.SourceUnit

import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX

/**
 * @author ceilfors
 */
class ExpressionInfos {

    static ConstructorCallExpression ctor(SourceUnit sourceUnit, String methodName, Expression x) {
        def text = sourceUnit.getSample(x.lineNumber, 0, null).substring(x.columnNumber - 1, x.lastColumnNumber - 1)
        new ConstructorCallExpression(ClassHelper.make(ExpressionInfo), args(constX(methodName), constX(text), x))
    }
}
