package com.ssafy.home.toolcalling.tool;

import static org.assertj.core.api.Assertions.assertThat;

import com.ssafy.home.toolcalling.support.FakeRealEstateToolDataProvider;
import org.junit.jupiter.api.Test;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

class ToolBeanRegistrationTest {

    @Test
    void statsTool과_houseSearchTool이_빈으로_등록된다() throws NoSuchMethodException {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext()) {
            context.register(FakeRealEstateToolDataProvider.class, StatsTool.class, HouseSearchTool.class);
            context.refresh();

            StatsTool statsTool = context.getBean(StatsTool.class);
            HouseSearchTool houseSearchTool = context.getBean(HouseSearchTool.class);

            assertThat(statsTool).isNotNull();
            assertThat(houseSearchTool).isNotNull();
            assertThat(StatsTool.class
                .getMethod("getRegionStats", String.class, String.class, String.class)
                .isAnnotationPresent(Tool.class)).isTrue();
            assertThat(HouseSearchTool.class
                .getMethod("searchHouses", String.class, String.class, Integer.class)
                .isAnnotationPresent(Tool.class)).isTrue();
        }
    }
}
