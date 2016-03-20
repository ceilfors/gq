```
This file must be moved to github issue tracker once we hit 0.1.0
```

# Technical debt
- Refactor Matcher message to be more meaningful e.g. to use Spock helper
- Move @Gq out of ast package to gq.gq.
- Rename package to com.ceilfors.groovy instead of transform.
- Remove ast layer in test case

# Bug 

# Feature
- 0.1.0 @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
- 0.1.0 Colouring in console. Ability to turn it off.
- @Gq Exception - Print source code context e.g. source code snippets and line numbers
- @Gq(vars=true) to print all variable expression
- GqSupport - Support removing comments from expression text e.g. // in multiline or /* */ in one liner
- GqSupport - Support void better by removing the =null or change it to =void
- q.d