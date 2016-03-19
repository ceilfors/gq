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

import java.math.RoundingMode

/**
 * @author ceilfors
 */
class LongToFileSyntaxConverter implements SyntaxConverter<String, String> {

    int limit = 50
    private String separator = ".."
    File directory
    String prefix = "gq"

    void setLimit(int limit) {
        def minimumLimit = 1 + separator.length() + 1
        if (limit < minimumLimit) {
            throw new IllegalArgumentException("Minimum limit is $minimumLimit")
        }
        this.limit = limit
    }

    LongToFileSyntaxConverter(File directory) {
        this.directory = directory
    }

    @Override
    String convertExpressionValue(String expression) {
        if (expression.length() <= limit) {
            return expression
        }
        File file = storeInFile(expression)
        return shorten(expression) + " (file://${file.absolutePath})"
    }

    private File storeInFile(String expression) {
        File file = new File(directory, "$prefix${UUID.randomUUID().toString()}.txt")
        file << expression
        return file
    }

    private String shorten(String expression) {
        int leftLimit = ((limit - separator.length()) / 2).setScale(0, RoundingMode.UP)
        int rightLimit = limit - separator.length() - leftLimit
        def left = expression.substring(0, leftLimit)
        def right = expression.substring(expression.length() - rightLimit, expression.length())
        return left + separator + right
    }
}
