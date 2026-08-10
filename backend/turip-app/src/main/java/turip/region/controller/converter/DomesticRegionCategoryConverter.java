package turip.region.controller.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import turip.region.domain.DomesticRegionCategory;

@Component
public class DomesticRegionCategoryConverter implements Converter<String, DomesticRegionCategory> {

    @Override
    public DomesticRegionCategory convert(@NonNull String regionCategory) {
        return DomesticRegionCategory.fromDisplayName(regionCategory);
    }
}
