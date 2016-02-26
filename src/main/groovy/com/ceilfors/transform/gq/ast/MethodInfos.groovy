package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.MethodInfo
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ConstructorCallExpression

/**
 * @author ceilfors
 */
class MethodInfos {

    static ConstructorCallExpression ctor(MethodNode methodNode) {
        new AstBuilder().buildFromSpec {
            constructorCall(MethodInfo) {
                argumentList {
                    constant methodNode.name
                    list {
                        for (Parameter parameter in methodNode.parameters) {
                            constant parameter.name
                        }
                    }
                    list {
                        for (Parameter parameter in methodNode.parameters) {
                            variable parameter.name
                        }
                    }
                }
            }
        }[0] as ConstructorCallExpression
    }
}
