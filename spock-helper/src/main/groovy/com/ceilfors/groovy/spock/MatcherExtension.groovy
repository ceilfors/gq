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

package com.ceilfors.groovy.spock

import java.util.regex.Matcher

import static com.ceilfors.groovy.spock.SpockHelper.getSpockStyleComparison

/**
 * @author ceilfors
 */
class MatcherExtension {

    public static boolean forceMatches(final Matcher self) {
        def result = self.matches()
        if (!result) {
            def regex = self.pattern().toString()
            def text = self.text.toString()
            def message = getSpockStyleComparison('regex ==~ text', regex, text)
            throw new AssertionError("Matcher matches() returns false:\n" + message)
        }
        return result
    }
}
