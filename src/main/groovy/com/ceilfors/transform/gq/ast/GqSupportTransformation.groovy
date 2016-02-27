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

import com.ceilfors.transform.gq.GqSupport
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * @author ceilfors
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class GqSupportTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        def transformer = new ClassCodeExpressionTransformer() {

            String currentMethodName

            @Override
            protected SourceUnit getSourceUnit() {
                sourceUnit
            }

            @Override
            protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
                currentMethodName = node.name
                super.visitConstructorOrMethod(node, isConstructor)
            }

            @Override
            Expression transform(Expression expression) {
                if (expression instanceof StaticMethodCallExpression && expression.ownerType.name == GqSupport.name) {
                    // Traps normal method call to GqSupport and reroute to CodeFlowListeners
                    def originalArgs = (expression.arguments as ArgumentListExpression).expressions[0]
                    return CodeFlowManagers.expressionProcessed(ExpressionInfos.ctor(getSourceUnit(), currentMethodName, originalArgs))
                }
                return super.transform(expression)
            }
        }

        for (ClassNode classNode : sourceUnit.AST.classes) {
            transformer.visitClass(classNode)
        }
    }
}
