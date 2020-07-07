package financialadvisor.model;

import java.util.List;

public class TechnicalLinesAgg {
    List<TechnicalLine> technicalLines;

    public TechnicalLinesAgg(List<TechnicalLine> technicalLines) {
        this.technicalLines = technicalLines;
    }

    public List<TechnicalLine> getTechnicalLines() {
        return technicalLines;
    }

    public void setTechnicalLines(List<TechnicalLine> technicalLines) {
        this.technicalLines = technicalLines;
    }
}
