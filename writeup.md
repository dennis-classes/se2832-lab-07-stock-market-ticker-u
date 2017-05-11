## Lab 7 - Stock Market Ticker
### Nicholas Roth (rothnj@msoe.edu)
### Steven Fontaine (fontainesw@msoe.edu)

#### Bug List

| Line # | Fault | Error | Fix |
|--------|-------|-------|-----|
| 151 | if(currentQuote != null) | An exception is thrown if the currentQuote *is not* null. We want it to be thrown only if the quote is null. | Set it so the exception is thrown only if the quote is null. |
| 185 | throw new NullPointerException("..."); | This is the wrong type of exception. Should be an InvalidAnalysisState. | Change the exception thrown to an InvalidAnalysisState. |
| 187 | return currentQuote.getChange()-currentQuote.getClose(); | This method should just return the change in the quote, but is also performing an incorrect subtraction. The quote's change is all we want returned. | Have the method just return currentQuote.getChange() |
| 204 | return Math.round((100000 * this.currentQuote.getChange() / this.currentQuote.getClose())) / 100.0; | Math error, one too many 0's in the first multiplier resulting in incorrect outputs. | Change the 100,000 to 10,000 |
| 220 | return currentQuote.getLastTrade() - currentQuote.getLastTrade(); | This operation will always return 0 as it is subtracting the same variable from itself. | Should just return currentQuote.getLastTrade() |

#### Coverage
We were able to obtain 100% coverage with our tests.

[100% Coverage](http://i.imgur.com/sQw4V6r.png)
