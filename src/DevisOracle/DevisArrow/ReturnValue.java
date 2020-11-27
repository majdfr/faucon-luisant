package DevisOracle.DevisArrow;

import DevisOracle.DevisArrow.ExcelQuoteRow;

import java.util.List;

public class ReturnValue {
    List<ExcelQuoteRow> arrowQuote;
    List<ExcelQuoteRow> computaQuote;
    List<String> logs;

    public List<ExcelQuoteRow> getArrowQuote() {
        return arrowQuote;
    }

    public void setArrowQuote(List<ExcelQuoteRow> arrowQuote) {
        this.arrowQuote = arrowQuote;
    }

    public List<ExcelQuoteRow> getComputaQuote() {
        return computaQuote;
    }

    public void setComputaQuote(List<ExcelQuoteRow> computaQuote) {
        this.computaQuote = computaQuote;
    }

    public List<String> getLogs() {
        return logs;
    }

    public void setLogs(List<String> logs) {
        this.logs = logs;
    }
}
