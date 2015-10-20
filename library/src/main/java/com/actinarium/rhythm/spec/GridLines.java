/*
 * Copyright (C) 2015 Actinarium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actinarium.rhythm.spec;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.ColorInt;
import android.view.Gravity;

/**
 * A spec layer for horizontal <b>or</b> vertical grid lines (not both at once!), repeating at a fixed step. Horizontal
 * grid can float either to the top or the bottom of the views, whereas vertical grid can float to the left or the
 * right. You can (and should) combine multiple grid line layers to form regular grids, or you may use them alone for
 * baseline grids and incremental keylines. <b>Note:</b> RTL properties are not supported, you only have <i>left</i> and
 * <i>right</i> at your disposal.
 */
public class GridLines implements RhythmSpecLayer {

    public static final int DEFAULT_GRID_COLOR = 0x60F50057;
    public static final int DEFAULT_BASELINE_COLOR = 0x800091EA;

    protected int mStep;
    protected int mThickness = 1;
    protected int mLimit = Integer.MAX_VALUE;
    protected boolean mMarginIsPercent;
    protected int mMarginLeft;
    protected int mMarginTop;
    protected int mMarginRight;
    protected int mMarginBottom;
    protected int mOffset;
    @LayerGravity
    protected int mGravity;
    protected Paint mPaint;

    /**
     * Create a layer that draws horizontal or vertical grid lines. Unless offset is applied, horizontal lines are
     * always drawn <i>below</i> the delimited pixel row, and vertical lines are always drawn <i>to the right</i> of the
     * delimited column: e.g. if a child view is fully aligned to the grid on all sides, top and bottom grid lines will
     * overdraw the view, whereas bottom and right grid lines will touch the view.
     *
     * @param gravity Control grid alignment <b>and</b> orientation. {@link Gravity#TOP} and {@link Gravity#BOTTOM} mean
     *                horizontal lines, and {@link Gravity#LEFT} and {@link Gravity#RIGHT} mean vertical lines, and the
     *                difference between those is from what side of the view the steps are counted. A good example where
     *                this can be useful is having a left-aligned and a right-aligned layer on the left and the right
     *                half of the view when its width is not an exact multiple of the step.
     * @param step    Grid step, in pixels
     */
    public GridLines(@LayerGravity int gravity, int step) {
        mStep = step;
        mGravity = gravity;

        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(DEFAULT_GRID_COLOR);
    }

    /**
     * Set grid line color
     *
     * @param color Grid line color, in #AARRGGBB format as usual
     * @return this for chaining
     */
    public GridLines color(@ColorInt int color) {
        mPaint.setColor(color);
        return this;
    }

    /**
     * Set grid line thickness
     *
     * @param thickness Grid line thickness, in pixels
     * @return this for chaining
     */
    public GridLines thickness(int thickness) {
        mThickness = thickness;
        return this;
    }

    /**
     * Set layer margins, in either pixels or percent of the view. This can be useful if you need to display e.g. a few
     * separate grids in the opposite sides of the view so that they don’t overlap. By default the margins are 0.
     *
     * @param isPercent false to treat parameters as pixels, true to treat parameters as percent in range 0..100.
     *                  Percent values outside this range will be treated as 0.
     * @param left      left margin (px or %)
     * @param top       top margin (px or %)
     * @param right     right margin (px or %)
     * @param bottom    bottom margin (px or %)
     * @return this for chaining
     */
    public GridLines margins(boolean isPercent, int left, int top, int right, int bottom) {
        mMarginIsPercent = isPercent;
        mMarginLeft = left;
        mMarginTop = top;
        mMarginRight = right;
        mMarginBottom = bottom;
        return this;
    }

    /**
     * Set the maximum number of steps to outline, respecting layer’s gravity (i.e. if gravity is set to {@link
     * Gravity#BOTTOM} and the limit is 4, this layer will draw four lines enclosing 4 cells. Default is no limit.
     *
     * @param limit Number of lines to draw. Setting zero or less means no limit.
     * @return this for chaining
     */
    public GridLines limit(int limit) {
        mLimit = limit > 0 ? limit : Integer.MAX_VALUE;
        return this;
    }

    /**
     * Set additional grid offset. Might be useful if you need to tweak the position of the grid just a few pixels up or
     * down, or prevent overdraw when combining a few interleaving grids (e.g. to add a 4dp baseline grid to a 8dp
     * regular grid you only need to draw each second baseline, which is done with a 8dp step and a 4dp offset).
     *
     * @param offset Grid offset in pixels. Regardless of gravity, positive offset means right/down, negative means
     *               left/up
     * @return this for chaining
     */
    public GridLines offset(int offset) {
        mOffset = offset;
        return this;
    }

    @Override
    public void draw(Canvas canvas, Rect drawableBounds) {
        // Calculate real left/right/top/bottom bounds based on drawable bounds and margins
        final int left, top, right, bottom;
        if (mMarginIsPercent) {
            final int width = drawableBounds.width();
            final int height = drawableBounds.height();
            left = drawableBounds.left + width * mMarginLeft / 100;
            top = drawableBounds.top + height * mMarginTop / 100;
            right = drawableBounds.right - width * mMarginRight / 100;
            bottom = drawableBounds.bottom - height * mMarginBottom / 100;
        } else {
            left = drawableBounds.left + mMarginLeft;
            top = drawableBounds.top + mMarginTop;
            right = drawableBounds.right - mMarginRight;
            bottom = drawableBounds.bottom - mMarginBottom;
        }

        // Calculate final width and height
        final int width = right - left;
        final int height = bottom - top;
        if (width <= 0 || height <= 0) {
            // Nothing to draw
            return;
        }

        // Depending on gravity the orientation, the order of drawing, and the starting point are different
        int line = 0;
        if (mGravity == Gravity.TOP) {
            int curY = top + mOffset;
            while (curY < bottom && line <= mLimit) {
                canvas.drawRect(left, curY, right, curY + mThickness, mPaint);
                curY += mStep;
                line++;
            }
        } else if (mGravity == Gravity.BOTTOM) {
            int curY = bottom + mOffset;
            while (curY >= top && line <= mLimit) {
                canvas.drawRect(left, curY, right, curY + mThickness, mPaint);
                curY -= mStep;
                line++;
            }
        } else if (mGravity == Gravity.LEFT) {
            int curX = left + mOffset;
            while (curX < right && line <= mLimit) {
                canvas.drawRect(curX, top, curX + mThickness, bottom, mPaint);
                curX += mStep;
                line++;
            }
        } else if (mGravity == Gravity.RIGHT) {
            int curX = right + mOffset;
            while (curX >= left && line <= mLimit) {
                canvas.drawRect(curX, top, curX + mThickness, bottom, mPaint);
                curX -= mStep;
                line++;
            }
        }
    }

}
