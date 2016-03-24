```
This file must be moved to github issue tracker once we hit 0.1.0
```

# Technical debt
- Rename package to com.ceilfors.groovy instead of transform.
- Remove ast layer in test case
- Move common repositories settings in submodule to parent
- Use ResourceFilter in spock-helper for ExtensionModules.
- Remove unused methodName at expressionProcessed

# Bug 
- 0.1.0 Exception when gq(gq(5))

# Feature
- 0.1.0 Support for / and |
- 0.1.0 @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
- @Gq Exception - Print source code context e.g. source code snippets and line numbers
- @Gq(vars=true) to print all variable expression
- GqSupport - Support removing comments from expression text e.g. // in multiline or /* */ in one liner
- GqSupport - Support void better by removing the =null or change it to =void
- q.d