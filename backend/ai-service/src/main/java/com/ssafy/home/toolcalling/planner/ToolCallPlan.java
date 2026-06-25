package com.ssafy.home.toolcalling.planner;

import java.util.List;

public record ToolCallPlan(
    List<ToolCallStep> steps
) {

    public ToolCallPlan {
        steps = List.copyOf(steps);
    }

    public boolean hasStep(ToolCallStep step) {
        return steps.contains(step);
    }

    public boolean usesTool() {
        return !steps.isEmpty();
    }
}
