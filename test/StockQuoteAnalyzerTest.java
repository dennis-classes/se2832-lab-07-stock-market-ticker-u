import exceptions.InvalidAnalysisState;
import exceptions.InvalidStockSymbolException;
import exceptions.StockTickerConnectionError;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;


import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.mockito.Mock;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.*;

public class StockQuoteAnalyzerTest {
    @Mock
    private StockQuoteGeneratorInterface generatorMock;
    @Mock
    private StockTickerAudioInterface audioMock;
    @Mock
    private StockQuote quoteMock;

    private StockQuoteAnalyzer analyzer;

    @BeforeMethod
    public void setUp() throws Exception {
        generatorMock = mock(StockQuoteGeneratorInterface.class);
        audioMock = mock(StockTickerAudioInterface.class);
    }

    @AfterMethod
    public void tearDown() throws Exception {
        generatorMock = null;
        audioMock = null;
    }

    @Test(expectedExceptions = InvalidStockSymbolException.class)
    public void constructorShouldThrowExceptionWhenSymbolIsInvalid() throws Exception {
        analyzer = new StockQuoteAnalyzer("ZZZZZZZZZ", generatorMock, audioMock);


    }

    @Test
    public void playAppropriateAudioShouldPlayErrorMusicWhenPercentChangeSinceCloseIsInvalid() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(1)).playErrorMusic();
    }

    @Test
    public void playAppropriateAudioShouldPlayHappyMusicWhenPercentChangeSinceCloseIsGreaterThanZero() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(5.0);
        when(stockQuoteMock.getClose()).thenReturn(5.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(1)).playHappyMusic();
    }

    @Test
    public void playAppropriateAudioShouldPlaySadMusicWhenPercentChangeSinceCloseIsLessThanNegativeOne() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(-1.0);
        when(stockQuoteMock.getClose()).thenReturn(10.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(1)).playSadMusic();
    }

    @Test
    public void playAppropriateAudioShouldNotPlaySadMusicWhenPercentChangeSinceCloseIsBetweenZeroAndNegativeOne() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(-5.0);
        when(stockQuoteMock.getClose()).thenReturn(10000.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(0)).playSadMusic();
    }

    @Test
    public void playAppropriateAudioShouldNotPlayHappyMusicWhenPercentChangeSinceCloseIsBetweenZeroAndNegativeOne() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(-5.0);
        when(stockQuoteMock.getClose()).thenReturn(10000.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(0)).playHappyMusic();
    }

    @Test
    public void playAppropriateAudioShouldPlaySadMusicWhenPercentChangeSinceCloseIsEqualToNegativeOne() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(-10.0);
        when(stockQuoteMock.getClose()).thenReturn(1000.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();
        analyzer.playAppropriateAudio();

        //Assert
        verify(audioMock, times(1)).playSadMusic();
    }

    @Test
    public void playAppropriateAudioShouldDoNothingWhenAudioPlayerEqualsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, null);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class)
    public void getPercentChangeSinceCloseShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.getPercentChangeSinceClose();
    }

    @Test
    public void getPercentChangeSinceCloseShouldReturnProperPercentageWhenCalled() throws Exception {
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(-5.0);
        when(stockQuoteMock.getClose()).thenReturn(10000.0);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();

        //Assert
        assertEquals(analyzer.getPercentChangeSinceClose(), -0.05);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class) //Assert
    public void getCurrentPriceShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.getCurrentPrice();
    }

    @Test
    public void getCurrentPriceShouldReturnCurrentSellingPriceWhenCalled() throws Exception {
        double lastReturn = 20.1;
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getLastTrade()).thenReturn(lastReturn);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();

        //Assert
        assertEquals(analyzer.getCurrentPrice(), lastReturn);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class) //Assert
    public void getChangeSinceCloseShouldThrowExceptionWhenCurrentQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.getChangeSinceClose();
    }

    @Test
    public void getChangeSinceCloseShouldReturnChangeSinceLastCloseWhenCalled() throws Exception {
        double change = -5.6;
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getChange()).thenReturn(change);
        when(stockQuoteMock.getClose()).thenReturn(4.1);
        when(generatorMock.getCurrentQuote()).thenReturn(stockQuoteMock);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.refresh();

        //Assert
        assertEquals(analyzer.getChangeSinceClose(), change);
    }




    @Test(expectedExceptions = NullPointerException.class)
    public void constructorShouldThrowNullPointerExceptionWhenStockQuoteSourceIsNull() throws NullPointerException{
        //Arrange, Act, Assert
        try {
            analyzer = new StockQuoteAnalyzer("F", null, audioMock);
        }catch(InvalidStockSymbolException i){

        }
    }

    @Test
    public void refreshShouldrefreshWhenQuoteIsReturned(){
        //Arrange
        StockQuote quote = new StockQuote("F", 90.0, 80.0, 3.0);
        try {
            when(generatorMock.getCurrentQuote()).thenReturn(quote);
            analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        } catch (Exception e){}
        double a = -1;

        //Act
        try {
            analyzer.refresh();
        }catch(StockTickerConnectionError e){
            fail();
        }
        try{
            a = analyzer.getCurrentPrice();
        }catch (Exception e){

        }

        //Assert
        assertEquals(a, quote.getLastTrade());
    }

    @Test(expectedExceptions = StockTickerConnectionError.class)
    public void refreshShouldThrowStockTickerConnectionErrorExceptionIfConectionErrorOccurrs() throws StockTickerConnectionError{
        //Arrange
        StockQuote quote = null;
        try {
            when(generatorMock.getCurrentQuote()).thenThrow(Exception.class);
            analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        } catch (Exception e){}
        double a = -1;

        //Act, Assert
            analyzer.refresh();
    }

    @Test
    public void getSymbolShouldReturnSymbol(){
        //Arrange
        String ticker = "F";
        try{
            analyzer = new StockQuoteAnalyzer(ticker, generatorMock, audioMock);
        }catch(Exception e){

        }

        //Act
        String symbol = analyzer.getSymbol();

        //Assert
        assertEquals(ticker, symbol);
    }

    @Test(expectedExceptions = InvalidAnalysisState.class)
    public void getPreviousCloseShouldThrowInvalidAnalysisStateExceptionIfCurrentQuoteIsNull() throws InvalidAnalysisState{
        //Arrange
        try{
            analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        }catch(Exception e){

        }

        //Act, Assert
        analyzer.getPreviousClose();
    }

    @Test
    public void getPreviousCloseShouldReturnPreviousClose(){
        //Arrange
        double prev = 90.0;
        StockQuote quote = new StockQuote("F", prev, 80.0, 3.0);
        try {
            when(generatorMock.getCurrentQuote()).thenReturn(quote);
            analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
            analyzer.refresh();
        } catch (Exception e){}
        double prevClose = -1;

        //Act
        try{
            prevClose = analyzer.getPreviousClose();
        }catch (InvalidAnalysisState i){
            fail();
        }

        //Assert
        assertEquals(prevClose, prev);
    }

    @Test (expectedExceptions = InvalidAnalysisState.class) //Assert
    public void getChangeSinceLastCheckShouldThrowExceptionWhenQuoteIsNull() throws Exception {
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        analyzer.getChangeSinceLastCheck();
    }

    @Test
    public void getChangeSinceLastCheckShouldReturnLastCheckWhenCalled() throws Exception {
        //Arrange
        double lastTrade = 2.4;
        StockQuoteInterface stockQuoteMock = mock(StockQuote.class);
        when(stockQuoteMock.getLastTrade()).thenReturn(lastTrade);
        analyzer = new StockQuoteAnalyzer("F", generatorMock, audioMock);
        //Act
        analyzer.refresh();
        //Assert
        assertEquals(analyzer.getChangeSinceLastCheck(), lastTrade);
    }

}