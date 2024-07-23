package co.com.minvivienda.inurbe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.camel.Exchange;
import org.apache.camel.processor.aggregate.AggregationStrategy;

public class ExpedientesAggregator implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        if (oldExchange == null) {
            return newExchange;
        }
        
        List<Expediente> expedientes = oldExchange.getIn().getBody(List.class);
        Long total = newExchange.getIn().getBody(Long.class);
        
        Map<String, Object> mergedResult = new HashMap<>();
        mergedResult.put("expedientes", expedientes);
        mergedResult.put("total", total);
        
        oldExchange.getIn().setBody(mergedResult);
        return oldExchange;
    }
}
