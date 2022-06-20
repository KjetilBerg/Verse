package com.kbindiedev.verse.math.shape;

import com.kbindiedev.verse.math.MathUtil;

// TODO consider: extends Polygon

/** Rectangle but with floats. */
public class Rectanglef {

    private float x, y, width, height;

    public Rectanglef() { this(0, 0, 0, 0); }
    public Rectanglef(float x, float y, float width, float height) {
        set(x, y, width, height);
    }

    public void set(float x, float y, float width, float height) {
        this.x = x; this.y = y; this.width = width; this.height = height;
    }

    public void setX(float x) { this.x = x; }
    public void setY(float y) { this.y = y; }
    public void setWidth(float width) { this.width = width; }
    public void setHeight(float height) { this.height = height; }

    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }

    public float getCenterX() { return x + width / 2; }
    public float getCenterY() { return y + height / 2; }

    public float getX2() { return x + width; }
    public float getY2() { return y + height; }

    /** Translate this rectangle such that its center x value becomes the given centerX value. */
    public void focusCenterX(float centerX) { x = centerX - width / 2; }
    /** Translate this rectangle such that its center y value becomes the given centerY value. */
    public void focusCenterY(float centerY) { y = centerY - height / 2; }
    /** Translate this rectangle such that its center becomes the given values (width and height are maintained). */
    public void focusCenter(float centerX, float centerY) { focusCenterX(centerX); focusCenterY(centerY); }

    /** Set this rectangle's width whilst maintaining centerX. */
    public void expandXByCenterTo(float newWidth) {
        x -= (newWidth - width) / 2; // width increase / 2
        width = newWidth;
    }
    /** Set this rectangle's height whilst maintaining centerY. */
    public void expandYByCenterTo(float newHeight) {
        y -= (newHeight - height) / 2; // height increase / 2
        height = newHeight;
    }
    /** Set this rectangle's width and height whilst maintaining its center coordinate. */
    public void expandByCenterTo(float newWidth, float newHeight) { expandXByCenterTo(newWidth); expandYByCenterTo(newHeight); }

    /** Shrink this rectangle on all sides by the given amount (x1, x2, y1, y2). */
    public void shrink(float amount) {
        x += amount; width -= amount * 2;
        y += amount; height -= amount * 2;
    }

    /** Scale this rectangle by s, assuming the rectangle's center. */
    public void scale(float s) {
        float newWidth = width * s;
        float newHeight = height * s;

        float widthIncrease = newWidth - width;
        float heightIncrease = newHeight - height;

        x -= widthIncrease / 2;
        y -= heightIncrease / 2;
        width = newWidth;
        height = newHeight;
    }

    /**
     * Clamp this rectangle by the given rectangle, so that my x1,y1,x2,y2 is at most within the given rectangle.
     * If the given rectangle is null, then nothing happens.
     */
    public void clamp(Rectanglef rectangle) {
        if (rectangle == null) return;
        x = Math.max(x, rectangle.x);
        y = Math.max(y, rectangle.y);
        width = Math.min(getX2(), rectangle.getX2()) - x;
        height = Math.min(getY2(), rectangle.getY2()) - y;
    }

    // TODO: remove these methods ??

    /**
     * Bound this rectangle by shrinking it.
     * If the rectangle's minimum size would still make it fall outside the bound, then this
     *      rectangle is translated so that it fits inside.
     * @param minWidth - The minimum width the rectangle will be shrunk to.
     * @param minHeight - The minimum height the rectangle will be shrunk to.
     * @return the amount that this rectangle was shrunk.
     */
    public void shrinkBound(Rectanglef bound, float minWidth, float minHeight) {
        if (bound == null) return;

        if (width < minWidth) expandXByCenterTo(minWidth);
        if (height < minHeight) expandYByCenterTo(minHeight);

        float boundX2 = bound.getX2(), boundY2 = bound.getY2();

        // ensure center is within the bound (of smallest size)
        float minFinalX = bound.x + minWidth / 2;
        float maxFinalX = boundX2 - minWidth / 2;
        float minFinalY = bound.y + minHeight / 2;
        float maxFinalY = boundY2 - minHeight / 2;
        if (minFinalX >= maxFinalX) {
            focusCenterX(bound.getCenterX());   // cannot fit x
        } else {
            float centerX = getCenterX();
            if (centerX < minFinalX) focusCenterX(minFinalX);
            if (centerX > maxFinalX) focusCenterX(maxFinalX);
        }
        if (minFinalY >= maxFinalY) {
            focusCenterY(bound.getCenterY());   // cannot fit y
        } else {
            float centerY = getCenterY();
            if (centerY < minFinalY) focusCenterY(minFinalY);
            if (centerY > maxFinalY) focusCenterY(maxFinalY);
        }

        // scale down to fit inside the bound
        float outsideLeft = bound.x - x;
        float outsideRight = getX2() - boundX2;
        float mustShrinkX = MathUtil.max(outsideLeft, outsideRight, 0);
        float mustScaleX = (width - mustShrinkX * 2) / width;

        float outsideBottom = bound.y - y;
        float outsideTop = getY2() - boundY2;
        float mustShrinkY = MathUtil.max(outsideBottom, outsideTop, 0);
        float mustScaleY = (height - mustShrinkY * 2) / height;

        float scale = Math.min(mustScaleX, mustScaleY);
        scale(scale);
    }

    /**
     * Bound this rectangle by translating it such that it fits inside the given bound.
     * If this rectangle does not fall outside the given bound, then nothing happens.
     * If it is impossible to fit an axis inside the bound, then the rectangle will be scaled to fit inside.
     */
    public void translateBound(Rectanglef bound) {
        if (bound == null) return;

        // scale down so that it fits inside
        float mustScaleX = bound.width / width, mustScaleY = bound.height / height;
        scale(MathUtil.min(mustScaleX, mustScaleY, 1));

        // translate so that it fits inside the bound
        float minCenterX = bound.getX() + width / 2;
        float maxCenterX = bound.getX2() - width / 2;
        float minCenterY = bound.getY() + height / 2;
        float maxCenterY = bound.getY2() - height / 2;

        float centerX = getCenterX(), centerY = getCenterY();
        if (centerX < minCenterX) focusCenterX(minCenterX);
        if (centerX > maxCenterX) focusCenterX(maxCenterX);
        if (centerY < minCenterY) focusCenterY(minCenterY);
        if (centerY > maxCenterY) focusCenterY(maxCenterY);
    }

    @Override
    public String toString() {
        return "Rectanglef[ x1: " + x + ", y1: " + y + ", x2: " + getX2() + ", y2: " + getY2() + ", width: " + width + ", height: " + height + " ]";
    }

}