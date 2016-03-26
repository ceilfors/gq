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
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static SourceUnitUtils.lookupBinary
import static SourceUnitUtils.lookupText
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX
/**
 * @author ceilfors
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class GqSupportTransformation implements ASTTransformation {

    private static final GQ_SUPPORT = ClassHelper.make(GqSupport)

    @Override
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        def final transformer = new GqSupportTransformer(sourceUnit)
        for (ClassNode classNode : sourceUnit.AST.classes) {
            transformer.visitClass(classNode)
        }
    }

    /**
     * This <tt>ExpressionTransformer</tt> traps interactions to GqSupport object and
     * reroute the interaction to CodeFlowListeners.
     *
     * @author ceilfors
     */
    class GqSupportTransformer extends ClassCodeExpressionTransformer {

        private SourceUnit sourceUnit

        private String currentMethodName

        GqSupportTransformer(SourceUnit sourceUnit) {
            this.sourceUnit = sourceUnit
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return sourceUnit
        }

        @Override
        protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
            currentMethodName = node.name
            super.visitConstructorOrMethod(node, isConstructor)
        }

        @Override
        Expression transform(Expression expression) {
            if (isGqSupportExpression(expression) && expression.method == getGqSupportAlias()) {
                return transformCall(expression as StaticMethodCallExpression)
            }

            if (expression instanceof BinaryExpression
                    && isGqSupportExpression(expression.leftExpression)
                    && expression.operation.type in [Types.PIPE, Types.DIVIDE]) {
                return transformOperator(expression)
            }
            return super.transform(expression)
        }

        Expression transformCall(StaticMethodCallExpression expression) {
            def originalArg = (expression.arguments as ArgumentListExpression).expressions[0]
            String text = lookupText(sourceUnit, originalArg)
            return callExpressionProcessed(currentMethodName, newExpressionInfo(originalArg.transformExpression(this), text))
        }

        Expression transformOperator(BinaryExpression expression) {
            Expression actualOperation = expression.rightExpression
            String text = lookupBinary(sourceUnit, actualOperation)
            return callExpressionProcessed(currentMethodName, newExpressionInfo(actualOperation.transformExpression(this), text))
        }

        private boolean isGqSupportExpression(Expression expression) {
            return expression instanceof StaticMethodCallExpression && expression.ownerType.name == GqSupport.name
        }

        private MethodCallExpression callExpressionProcessed(String methodName, Expression expressionInfo) {
            // MethodClosure type is deliberately used for better IDE support e.g. method name refactoring, etc.
            def methodClosure = SingletonCodeFlowManager.INSTANCE.&expressionProcessed as MethodClosure

            return new MethodCallExpression(
                    propX(classX(SingletonCodeFlowManager), "INSTANCE"),
                    methodClosure.method,
                    args(constX(methodName), expressionInfo))
        }

        private ConstructorCallExpression newExpressionInfo(Expression x, String text) {
            new ConstructorCallExpression(ClassHelper.make(ExpressionInfo), args(constX(currentMethodName), constX(text), x))
        }

        private getGqSupportAlias() {
            sourceUnit.AST.staticImports.values().find { it.fieldName == 'gq' && it.type == GQ_SUPPORT }.alias
        }
    }
}
