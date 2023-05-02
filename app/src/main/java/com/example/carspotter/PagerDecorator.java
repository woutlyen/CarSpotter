package com.example.carspotter;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Pair;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class PagerDecorator extends RecyclerView.ItemDecoration {

    private Paint paintStroke;
    private Paint paintFill;

    private List<Pair<Float, Float>> indicators;

    private final float indicatorRadius = 20f;
    private final float indicatorPadding = 0f;

    private int activeIndicator = 0;
    private boolean isInitialized = false;

    public PagerDecorator() {
        paintStroke = new Paint();
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setStrokeWidth(4f);
        paintStroke.setColor(Color.rgb(115, 118, 120));

        paintFill = new Paint();
        paintFill.setStyle(Paint.Style.FILL);
        paintFill.setColor(Color.rgb(115, 118, 120));

        indicators = new ArrayList<>();
    }

    @Override
    public void onDrawOver(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        if (!isInitialized) {
            setupIndicators(parent);
        }

        // draw three indicators with stroke style
        RecyclerView.Adapter adapter = parent.getAdapter();
        if (adapter != null) {
            canvas.drawCircle(indicators.get(0).first, indicators.get(0).second, indicatorRadius, paintStroke);
            canvas.drawCircle(indicators.get(1).first, indicators.get(1).second, indicatorRadius, paintStroke);
            canvas.drawCircle(indicators.get(2).first, indicators.get(2).second, indicatorRadius, paintStroke);

            int visibleItem = ((LinearLayoutManager) parent.getLayoutManager()).findFirstCompletelyVisibleItemPosition();
            if (visibleItem >= 0) {
                activeIndicator = visibleItem;
            }

            // paint over the needed circle
            switch (activeIndicator) {
                case 0:
                    canvas.drawCircle(indicators.get(0).first, indicators.get(0).second, indicatorRadius, paintFill);
                    break;
                case 1:
                    canvas.drawCircle(indicators.get(1).first, indicators.get(1).second, indicatorRadius, paintFill);
                    break;
                case 2:
                    canvas.drawCircle(indicators.get(2).first, indicators.get(2).second, indicatorRadius, paintFill);
                    break;
            }
        }
    }


    private void setupIndicators(RecyclerView recyclerView) {
        isInitialized = true;

        float indicatorTotalWidth = indicatorRadius;
        float indicatorPosX = (recyclerView.getWidth() - indicatorTotalWidth) / 2f;
        float indicatorPosY = recyclerView.getHeight() - (indicatorRadius * 6 / 2f);

        indicators.add(new Pair<>(indicatorPosX - indicatorRadius * 2, indicatorPosY));
        indicators.add(new Pair<>(indicatorPosX + indicatorRadius, indicatorPosY));
        indicators.add(new Pair<>(indicatorPosX + indicatorRadius * 4, indicatorPosY));
    }
}
