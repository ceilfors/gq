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

package com.ceilfors.groovy.gq

/**
 * @author ceilfors
 */
class ToStringSyntaxConverter implements SyntaxConverter<Object, String> {

    @SuppressWarnings('Instanceof') // Can't control GString inspect as it is not implemented correctly by Groovy
    @Override
    String convertExpressionValue(Object expression) {
        if (expression instanceof CharSequence) {
            expression.toString().inspect()
        } else {
            expression.inspect()
        }
    }
}
