```
This file must be moved to github issue tracker once we hit 0.1.0
```

# Technical debt
- Spock multline comparison is not clear. Also it requires denormalize everywhere. Introduce a method to compare multi line.
- Rename GqTransformation to GqASTTransformation to follow standard
- Move @Gq out of ast package.
- Rename package to com.ceilfors.groovy instead of transform.
- Remove ast layer in test case
- This code in GqFile seems to be a responsibility of someone else: writer.indentLevel = methodCalls.size()

# Bug 

# Feature
- 0.1.0 Escape characters e.g. \n in expression value
- 0.1.0 Timestamp on each line for readability
- 0.1.0 @Gq Exception - Test - nestedException1 catch exception from nestedexception2 and throw again. Indentation must stay the same.
- 0.1.0 Long expression - Generate temporary file. Handles expression value e.g. gq(<>), @Gq func(<>), @Gq return -> <>
- 0.1.0 Long expression - Prints ellipsis in the middle
- 0.1.0 Colouring in console. Ability to turn it off.
- @Gq Exception - Print source code context e.g. source code snippets and line numbers
- @Gq(vars=true) to print all variable expression
- GqSupport - Support removing comments from expression text e.g. // in multiline or /* */ in one liner
- Adopt @zefifier groovy-decorator
- GqSupport - Support void better by removing the =null or change it to =void
- q.d