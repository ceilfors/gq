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

package com.ceilfors.transform.gq

/**
 * A <tt>PrintWriter</tt> does an indentation on every new line automatically.<br />
 * <br />
 * Groovy has a built in <tt>IndentPrinter</tt> that has a <tt>autoIndent</tt> feature,
 * but it somehow is not behaving as expected. When <tt>autoIndent</tt> is enabled,
 * it actually prints the indentation every time println() is called regardless of the
 * new text being in a newline or not.
 *
 * @author ceilfors
 */
class AutoIndentingPrintWriter extends PrintWriter {

    String indent = "  "
    int indentLevel = 0
    boolean newLine = true
    private PrintWriter out

    AutoIndentingPrintWriter(PrintWriter out) {
        super(out)
        this.out = out
    }

    @Override
    void print(String s) {
        def currentIndent = indent * indentLevel
        if (newLine) {
            out.print(currentIndent)
            newLine = false
        }
        out.print(s.replaceAll(/(\r\n|\r|\n)/, '$1' + currentIndent))
    }

    @Override
    void println() {
        out.println()
        newLine = true
    }
}
