package com.ceilfors.transform.gq.ast

import org.codehaus.groovy.ast.*
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.ast.tools.GeneralUtils.*

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GqTransformation extends AbstractASTTransformation {

    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)

        AnnotatedNode annotatedNode = astNodes[1] as AnnotatedNode
        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = annotatedNode as MethodNode

            def result = varX("result")
            boolean voidReturnType = methodNode.returnType == ClassHelper.make(void)

            BlockStatement newCode = new BlockStatement([
                    stmt(CodeFlowManagers.methodStarted(MethodInfos.ctor(methodNode))),
                    callClosureAndKeepResultS(wrapInClosure(methodNode), result),
                    stmt(CodeFlowManagers.methodEnded(voidReturnType ? null : result))
            ], new VariableScope())

            if (!voidReturnType) {
                newCode.addStatement(returnS(result))
            }

            methodNode.code = newCode
        } else {
            throw new IllegalStateException("Gq annotation is only usable in methods.")
        }
    }

    private ClosureExpression wrapInClosure(MethodNode methodNode) {
        BlockStatement originalCode = methodNode.code as BlockStatement

        final def closureVariableScope = new VariableScope(originalCode.variableScope)
        for (Parameter parameter in methodNode.parameters) {
            // Allow closure to access the original code's variable
            closureVariableScope.putReferencedLocalVariable(parameter)
            parameter.setClosureSharedVariable(true) // KLUDGE: Should we use: new VariableScopeVisitor(sourceUnit, true).visitMethod(methodNode)
        }

        def closure = new ClosureExpression(Parameter.EMPTY_ARRAY, originalCode)
        closure.variableScope = closureVariableScope

        return closure
    }

    private Statement callClosureAndKeepResultS(ClosureExpression closure, VariableExpression resultVariable) {
        new AstBuilder().buildFromSpec {
            expression {
                declaration {
                    expression.add(resultVariable)
                    token "="
                    methodCall {
                        expression.add(closure)
                        constant "call"
                        argumentList {}
                    }

                }
            }
        }[0] as Statement
    }
}