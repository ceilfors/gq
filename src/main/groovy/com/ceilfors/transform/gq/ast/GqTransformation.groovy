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

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.AnnotatedNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.MethodNode
import org.codehaus.groovy.ast.Parameter
import org.codehaus.groovy.ast.VariableScope
import org.codehaus.groovy.ast.builder.AstBuilder
import org.codehaus.groovy.ast.expr.ClosureExpression
import org.codehaus.groovy.ast.expr.VariableExpression
import org.codehaus.groovy.ast.stmt.BlockStatement
import org.codehaus.groovy.ast.stmt.Statement
import org.codehaus.groovy.ast.stmt.TryCatchStatement
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.AbstractASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

import static org.codehaus.groovy.ast.tools.GeneralUtils.constX
import static org.codehaus.groovy.ast.tools.GeneralUtils.declS
import static org.codehaus.groovy.ast.tools.GeneralUtils.returnS
import static org.codehaus.groovy.ast.tools.GeneralUtils.stmt
import static org.codehaus.groovy.ast.tools.GeneralUtils.varX

@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
public class GqTransformation extends AbstractASTTransformation {

    @SuppressWarnings("UnnecessaryQualifiedReference")
    public void visit(ASTNode[] astNodes, SourceUnit sourceUnit) {
        init(astNodes, sourceUnit)

        AnnotatedNode annotatedNode = astNodes[1] as AnnotatedNode
        if (annotatedNode instanceof MethodNode) {
            MethodNode methodNode = annotatedNode as MethodNode

            def result = varX("result")
            def exception = varX("e", ClassHelper.make(Throwable))
            boolean voidReturnType = methodNode.returnType == ClassHelper.make(void)

            BlockStatement newCode = new BlockStatement([
                    stmt(CodeFlowManagers.methodStarted(MethodInfos.ctor(methodNode))),
                    declS(result, constX(null)),
                    tryAndRethrow(
                            callClosureAndKeepResult(wrapInClosure(methodNode), result),
                            stmt(CodeFlowManagers.exceptionThrown(ExceptionInfos.ctor(exception))),
                            exception
                    ),
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

    private TryCatchStatement tryAndRethrow(Statement tryBlock, Statement catchBlock, VariableExpression exception) {
        new AstBuilder().buildFromSpec {
            tryCatch {
                block {
                    expression.add(tryBlock)
                }
                empty()
                catchStatement {
                    parameter "${exception.name}": Exception.class
                    block {
                        expression.add(catchBlock)
                        throwStatement {
                            expression.add(exception)
                        }
                    }
                }
            }
        }[0] as TryCatchStatement
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

    private Statement callClosureAndKeepResult(ClosureExpression closure, VariableExpression resultVariable) {
        new AstBuilder().buildFromSpec {
            expression {
                binary {
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