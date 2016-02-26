package com.ceilfors.transform.gq.ast

import com.ceilfors.transform.gq.GqSupport
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer
import org.codehaus.groovy.ast.ClassNode
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

            @Override
            protected SourceUnit getSourceUnit() {
                sourceUnit
            }

            @Override
            Expression transform(Expression expression) {
                if (expression instanceof StaticMethodCallExpression && expression.ownerType.name == GqSupport.name) {
                    // Traps normal method call to GqSupport and reroute to CodeFlowListeners
                    def originalArgs = expression.arguments as ArgumentListExpression
                    return CodeFlowManagers.expressionProcessed(ExpressionInfos.ctor(originalArgs.expressions[0]))
                }
                return super.transform(expression)
            }
        }

        for (ClassNode classNode : sourceUnit.AST.classes) {
            transformer.visitClass(classNode)
        }
    }
}
