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
import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.ClassHelper
import org.codehaus.groovy.ast.expr.ConstructorCallExpression
import org.codehaus.groovy.ast.expr.Expression
import org.codehaus.groovy.control.Janitor
import org.codehaus.groovy.control.SourceUnit

import static org.codehaus.groovy.ast.tools.GeneralUtils.args
import static org.codehaus.groovy.ast.tools.GeneralUtils.constX

/**
 * @author ceilfors
 */
class ExpressionInfos {

    static ConstructorCallExpression ctor(SourceUnit sourceUnit, Expression x) {
        def text = lookup(sourceUnit, x)
        new ConstructorCallExpression(ClassHelper.make(ExpressionInfo), args(constX(text), x))
    }

    public static String lookup(SourceUnit sourceUnit, ASTNode node) {
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
