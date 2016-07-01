package in.medhajnews.app.ui;

import android.content.Context;
import android.graphics.Canvas;

public interface ActionButtonRenderer {

    void init(Context context, MetricsHolder metrics, PaintHolder paints);
    void draw(ActionButtonDataHolder holder, Canvas canvas, boolean isSelected);
}
