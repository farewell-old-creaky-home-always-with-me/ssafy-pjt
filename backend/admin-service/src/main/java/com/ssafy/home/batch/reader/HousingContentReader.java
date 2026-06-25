package com.ssafy.home.batch.reader;

import com.ssafy.home.external.housing.HousingContentClient;
import com.ssafy.home.external.housing.HousingContentSourceProperties;
import com.ssafy.home.external.housing.HousingRawContent;
import java.util.Iterator;
import java.util.List;
import org.springframework.batch.item.ItemReader;

public class HousingContentReader implements ItemReader<HousingRawContent> {

    private final HousingContentClient client;
    private final List<HousingContentSourceProperties.Source> sources;
    private Iterator<HousingRawContent> buffer;

    public HousingContentReader(
            HousingContentClient client,
            List<HousingContentSourceProperties.Source> sources
    ) {
        this.client = client;
        this.sources = sources;
    }

    @Override
    public HousingRawContent read() {
        if (buffer == null) {
            buffer = client.fetchAll(sources).iterator();
        }
        if (!buffer.hasNext()) {
            return null;
        }
        return buffer.next();
    }
}
