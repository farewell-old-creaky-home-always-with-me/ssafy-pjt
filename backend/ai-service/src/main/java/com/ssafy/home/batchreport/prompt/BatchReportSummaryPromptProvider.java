package com.ssafy.home.batchreport.prompt;

import com.ssafy.home.batchreport.dto.BatchReportSummaryRequest;
import com.ssafy.home.batchreport.dto.HouseDealSummaryItem;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class BatchReportSummaryPromptProvider {

    public String systemPrompt() {
        return """
            You are a real-estate batch report assistant.
            Return only strict JSON with keys summary and translatedSummary.
            summary must be Korean.
            translatedSummary must be English.
            Include collection outcome, notable transaction samples, data quality impact, and operational improvement points.
            """;
    }

    public String userPrompt(BatchReportSummaryRequest request) {
        StringBuilder builder = new StringBuilder();
        builder.append("Create an AI summary/translation report from this batch collection result.\n");
        builder.append("regionCode=").append(value(request.regionCode())).append('\n');
        builder.append("yearMonth=").append(value(request.yearMonth())).append('\n');
        builder.append("collectedCount=").append(value(request.collectedCount())).append('\n');
        builder.append("skippedCount=").append(value(request.skippedCount())).append('\n');
        builder.append("failedCount=").append(value(request.failedCount())).append('\n');
        builder.append("Sample house deals:\n");

        List<HouseDealSummaryItem> deals = request.deals() == null ? List.of() : request.deals();
        for (HouseDealSummaryItem deal : deals) {
            builder.append("- apt=").append(value(deal.aptName()))
                    .append(", dong=").append(value(deal.dongName()))
                    .append(", type=").append(value(deal.houseType()))
                    .append(", dealType=").append(value(deal.dealType()))
                    .append(", amount=").append(value(deal.dealAmount()))
                    .append(", date=").append(value(deal.dealDate()))
                    .append(", area=").append(value(deal.area()))
                    .append(", floor=").append(value(deal.floor()))
                    .append('\n');
        }
        return builder.toString();
    }

    private Object value(Object value) {
        return value == null ? "" : value;
    }
}
