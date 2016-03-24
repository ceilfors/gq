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

import com.ceilfors.transform.gq.ExpressionInfo
import com.ceilfors.transform.gq.GqSupport
import com.ceilfors.transform.gq.SingletonCodeFlowManager
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.Janitor
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX
/**
 * @author ceilfors
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class GqSupportTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        def final transformer = new GqSupportTransformer(sourceUnit)
        for (ClassNode classNode : sourceUnit.AST.classes) {
            transformer.visitClass(classNode)
        }
    }

    private class GqSupportTransformer extends ClassCodeExpressionTransformer {

        private SourceUnit sourceUnit

        private String currentMethodName

        GqSupportTransformer(SourceUnit sourceUnit, String initialMethodName = null) {
            this.sourceUnit = sourceUnit
            this.currentMethodName = initialMethodName
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
                def originalArg = (expression.arguments as ArgumentListExpression).expressions[0]
                originalArg = originalArg.transformExpression(new GqSupportTransformer(sourceUnit, currentMethodName))
                return callExpressionProcessed(currentMethodName, newExpressionInfo(sourceUnit, currentMethodName, originalArg))
            } else {
                return super.transform(expression)
            }
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return sourceUnit
        }

        private MethodCallExpression callExpressionProcessed(String methodName, Expression expressionInfo) {
            // MethodClosure type is deliberately used for better IDE support e.g. method name refactoring, etc.
            def methodClosure = SingletonCodeFlowManager.INSTANCE.&expressionProcessed as MethodClosure

            return new MethodCallExpression(
                    propX(classX(SingletonCodeFlowManager), "INSTANCE"),
                    methodClosure.method,
                    args(constX(methodName), expressionInfo))
        }

        private ConstructorCallExpression newExpressionInfo(SourceUnit sourceUnit, String methodName, Expression x) {
            def text = lookup(sourceUnit, x)
            new ConstructorCallExpression(ClassHelper.make(ExpressionInfo), args(constX(methodName), constX(text), x))
        }
    }

    private static String lookup(SourceUnit sourceUnit, ASTNode node) {
        Janitor janitor = new Janitor()
        StringBuilder text = new StringBuilder()
        for (int i = node.lineNumber; i <= node.lastLineNumber; i++) {
            String currentLine = sourceUnit.getSample(i, 0, janitor)
            if (i == node.lineNumber && i == node.lastLineNumber) {
                text.append(currentLine.substring(node.columnNumber - 1, node.lastColumnNumber - 1))
            } else if (i == node.lineNumber) {
                text.append(currentLine.substring(node.columnNumber - 1))
                text.append('\n')
            } else if (i == node.lastLineNumber) {
                text.append(currentLine.substring(0, node.lastColumnNumber - 1))
            } else {
                text.append(currentLine)
                text.append('\n')
            }
        }
        return text.toString().trim()
    }
}
