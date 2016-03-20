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

package groovy.runtime.metaclass.java.util.regex

import java.util.regex.Matcher

import static com.ceilfors.groovy.spock.SpockHelper.getSpockStyleComparison
/**
 * @author ceilfors
 */
class MatcherMetaClass extends DelegatingMetaClass {

    MatcherMetaClass(MetaClass delegate) {
        super(delegate)
    }

    @Override
    Object invokeMethod(Object object, String methodName, Object[] arguments) {
        if (methodName == 'forceMatches') {
            def result = super.invokeMethod(object, 'matches', arguments)
            if (!result) {
                Matcher matcher = object as Matcher

                def regex = matcher.pattern().toString()
                def text = matcher.text.toString()
                def message =  getSpockStyleComparison('regex ==~ text', regex, text)
                throw new AssertionError("Matcher matches() returns false:\n" + message )
            }
            return result
        } else {
            return super.invokeMethod(object, methodName, arguments)
        }
    }
}
