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

package com.ceilfors.groovy.gq.ast

import com.ceilfors.groovy.gq.SingletonCodeFlowManager
import com.ceilfors.groovy.gq.codeflow.ExpressionInfo
import gq.Gq
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.ClassNode
import org.codehaus.groovy.ast.CodeVisitorSupport
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.expr.ArgumentListExpression
import org.codehaus.groovy.ast.expr.BinaryExpression
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.ast.expr.MethodCallExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.runtime.MethodClosure
import org.codehaus.groovy.syntax.Types
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static SourceUnitUtils.lookupBinary
import static SourceUnitUtils.lookupText
import static com.ceilfors.groovy.gq.ast.SourceUnitUtils.getImportAlias
import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.classX
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.ctorX
import static org.codehaus.groovy.ast.tools.GeneralUtils.propX

/**
 * A global AST transformation that transforms <tt>q()</tt>, <tt>q/</tt>, <tt>q|</tt> to fire events to
 * <tt>CodeFlowManager</tt>
 *
 * @author ceilfors
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class GqASTTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        CodeVisitorSupport transformer = new GqTransformer(sourceUnit)
        for (ClassNode classNode : sourceUnit.AST.classes) {
            transformer.visitClass(classNode)
        }
    }

    class GqTransformer extends ClassCodeExpressionTransformer {

        private final SourceUnit sourceUnit

        private String currentMethodName

        GqTransformer(SourceUnit sourceUnit) {
            this.sourceUnit = sourceUnit
        }

        @Override
        protected SourceUnit getSourceUnit() {
            sourceUnit
        }

        @Override
        protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
            currentMethodName = node.name
            super.visitConstructorOrMethod(node, isConstructor)
        }

        @SuppressWarnings('Instanceof')
        @Override
        Expression transform(Expression expression) {
            String alias = getImportAlias(sourceUnit, Gq)
            if (!alias) {
                // Ignoring the class as it doesn't try to use Gq.
                // Note that import without alias in Groovy gives back alias too
                return super.transform(expression)
            }

            if (expression instanceof MethodCallExpression && expression.methodAsString == alias) {
                return transformCall(expression as MethodCallExpression)
            }

            if (expression instanceof BinaryExpression
                    && expression.leftExpression.text == classX(Gq).text
                    && expression.operation.type in [Types.PIPE, Types.DIVIDE]) {
                return transformOperator(expression)
            }

            if (expression instanceof ClosureExpression) {
                // Workaround for GROOVY-10713: ClassCodeExpressionTransformer ignoring Expressions within
                // ClosureExpression.code
                expression.visit(this)

                return expression
            }

            super.transform(expression)
        }

        Expression transformCall(MethodCallExpression expression) {
            Expression originalArg = (expression.arguments as ArgumentListExpression).expressions[0]
            if (!originalArg) {
                addError('gq must have at least 1 argument!', expression)
                return null
            }

            String text = lookupText(sourceUnit, originalArg)
            callExpressionProcessed(newExpressionInfo(originalArg.transformExpression(this), text))
        }

        Expression transformOperator(BinaryExpression expression) {
            Expression actualOperation = expression.rightExpression
            String text = lookupBinary(sourceUnit, actualOperation)
            callExpressionProcessed(newExpressionInfo(actualOperation.transformExpression(this), text))
        }

        private MethodCallExpression callExpressionProcessed(Expression expressionInfo) {
            // MethodClosure type is deliberately used for better IDE support e.g. method name refactoring, etc.
            MethodClosure methodClosure = SingletonCodeFlowManager.INSTANCE.&expressionProcessed as MethodClosure

            new MethodCallExpression(
                    propX(classX(SingletonCodeFlowManager), 'INSTANCE'),
                    methodClosure.method,
                    args(expressionInfo))
        }

        private ConstructorCallExpression newExpressionInfo(Expression x, String text) {
            ctorX(ClassHelper.make(ExpressionInfo), args(constX(currentMethodName), constX(text), x))
        }
    }
}
