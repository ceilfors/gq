package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.ExpressionInfo
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression

import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX

/**
 * @author ceilfors
 */
class ExpressionInfos {

    static ConstructorCallExpression ctor(Expression x) {
        new ConstructorCallExpression(ClassHelper.make(ExpressionInfo), args(constX(x.text), x))
    }
}
