package com.alfaazplus.sunnah.ui.utils.text.span;

import android.content.Context;
import android.content.res.loader.ResourcesProvider;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ClickableSpan;
import android.util.Pair;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import androidx.core.graphics.ColorUtils;

import java.util.ArrayList;

public class LinkSpanDrawable<S extends CharacterStyle> {

    private static final int CORNER_RADIUS_DP = 4;

    private int cornerRadius;
    private int color;
    private Paint mSelectionPaint, mRipplePaint;
    private int mSelectionAlpha, mRippleAlpha;

    private static final ArrayList<LinkPath> pathCache = new ArrayList<>();
    private final ArrayList<LinkPath> mPathes = new ArrayList<>();
    private int mPathesCount = 0;

    private final S mSpan;
    private final ResourcesProvider mResourcesProvider;
    private final float mTouchX;
    private final float mTouchY;
    private final Path circlePath = new Path();

    private Rect mBounds;
    private float mMaxRadius;
    private long mStart = -1;
    private long mReleaseStart = -1;
    private final long mDuration;
    private final long mLongPressDuration;
    private final boolean mSupportsLongPress;
    private static final long mReleaseDelay = 75;
    private static final long mReleaseDuration = 100;

    private final float selectionAlpha = 0.2f;
    private final float rippleAlpha = 0.8f;

    public LinkSpanDrawable(S span, ResourcesProvider resourcesProvider, float touchX, float touchY) {
        mSpan = span;
        mResourcesProvider = resourcesProvider;
        setColor(Color.RED);
        mTouchX = touchX;
        mTouchY = touchY;
        final long tapTimeout = ViewConfiguration.getTapTimeout();
        mLongPressDuration = ViewConfiguration.getLongPressTimeout();
        mDuration = (long) Math.min(tapTimeout * 1.8f, mLongPressDuration * 0.8f);
        mSupportsLongPress = false;
    }

    private static int dp(float value) {
        return (int) (value * 2);
    }

    private static Handler handler = new Handler(Looper.getMainLooper());

    private static void runOnUIThread(Runnable runnable, long delay) {
        if (delay == 0) {
            handler.post(runnable);
        } else {
            handler.postDelayed(runnable, delay);
        }
    }

    public void setColor(int color) {
        this.color = color;
        if (mSelectionPaint != null) {
            mSelectionPaint.setColor(color);
            mSelectionAlpha = Color.alpha(color);
        }
        if (mRipplePaint != null) {
            mRipplePaint.setColor(color);
            mRippleAlpha = Color.alpha(color);
        }
    }

    public void release() {
        mReleaseStart = Math.max(mStart + mDuration, SystemClock.elapsedRealtime());
    }

    public LinkPath obtainNewPath() {
        LinkPath linkPath;
        if (!pathCache.isEmpty()) {
            linkPath = pathCache.remove(0);
        } else {
            linkPath = new LinkPath(true);
        }
        linkPath.reset();
        mPathes.add(linkPath);
        mPathesCount = mPathes.size();
        return linkPath;
    }

    public void reset() {
        if (mPathes.isEmpty()) {
            return;
        }
        pathCache.addAll(mPathes);
        mPathes.clear();
        mPathesCount = 0;
    }

    public S getSpan() {
        return mSpan;
    }

    public boolean draw(Canvas canvas) {
        final int radius = CORNER_RADIUS_DP;
        boolean cornerRadiusUpdate = cornerRadius != radius;
        if (mSelectionPaint == null) {
            mSelectionPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mSelectionPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mSelectionPaint.setColor(color);
            mSelectionAlpha = Color.alpha(color);
        }
        if (mRipplePaint == null) {
            mRipplePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mRipplePaint.setStyle(Paint.Style.FILL_AND_STROKE);
            mRipplePaint.setColor(color);
            mRippleAlpha = Color.alpha(color);
        }
        if (cornerRadiusUpdate) {
            cornerRadius = radius;
            if (radius <= 0) {
                mSelectionPaint.setPathEffect(null);
                mRipplePaint.setPathEffect(null);
            } else {
                mSelectionPaint.setPathEffect(new CornerPathEffect(cornerRadius));
                mRipplePaint.setPathEffect(new CornerPathEffect(cornerRadius));
            }
        }
        if (mBounds == null && mPathesCount > 0) {
            mPathes.get(0).computeBounds(AndroidUtilities.rectTmp, false);
            mBounds = new Rect(
                    (int) AndroidUtilities.rectTmp.left,
                    (int) AndroidUtilities.rectTmp.top,
                    (int) AndroidUtilities.rectTmp.right,
                    (int) AndroidUtilities.rectTmp.bottom
            );
            for (int i = 1; i < mPathesCount; ++i) {
                mPathes.get(i).computeBounds(AndroidUtilities.rectTmp, false);
                mBounds.left = Math.min(mBounds.left, (int) AndroidUtilities.rectTmp.left);
                mBounds.top = Math.min(mBounds.top, (int) AndroidUtilities.rectTmp.top);
                mBounds.right = Math.max(mBounds.right, (int) AndroidUtilities.rectTmp.right);
                mBounds.bottom = Math.max(mBounds.bottom, (int) AndroidUtilities.rectTmp.bottom);
            }
            mMaxRadius = (float) Math.sqrt(
                    Math.max(
                            Math.max(
                                    Math.pow(mBounds.left - mTouchX, 2) + Math.pow(mBounds.top - mTouchY, 2),
                                    Math.pow(mBounds.right - mTouchX, 2) + Math.pow(mBounds.top - mTouchY, 2)
                            ),
                            Math.max(
                                    Math.pow(mBounds.left - mTouchX, 2) + Math.pow(mBounds.bottom - mTouchY, 2),
                                    Math.pow(mBounds.right - mTouchX, 2) + Math.pow(mBounds.bottom - mTouchY, 2)
                            )
                    )
            );
        }

        final long now = SystemClock.elapsedRealtime();
        if (mStart < 0) {
            mStart = now;
        }
        float pressT = CubicBezierInterpolator.DEFAULT.getInterpolation(Math.min(1, (now - mStart) / (float) mDuration)),
                releaseT = mReleaseStart < 0 ? 0 : Math.min(1, Math.max(0, (now - mReleaseDelay - mReleaseStart) / (float) mReleaseDuration));
        float longPress;
        if (mSupportsLongPress) {
            longPress = Math.max(0, (now - mStart - mDuration * 2) / (float) (mLongPressDuration - mDuration * 2));
            if (longPress > 1f) {
                longPress = 1f - ((now - mStart - mLongPressDuration) / (float) mDuration);
            } else {
                longPress *= .5f;
            }
            longPress *= (1f - releaseT);
        } else {
            longPress = 1f;
        }

        mSelectionPaint.setAlpha((int) (mSelectionAlpha * selectionAlpha * Math.min(1, pressT * 5f) * (1f - releaseT)));
        mSelectionPaint.setStrokeWidth(Math.min(1, 1f - longPress) * dp(5));
        for (int i = 0; i < mPathesCount; ++i) {
            canvas.drawPath(mPathes.get(i), mSelectionPaint);
        }

        mRipplePaint.setAlpha((int) (mRippleAlpha * rippleAlpha * (1f - releaseT)));
        mRipplePaint.setStrokeWidth(Math.min(1, 1f - longPress) * dp(5));
        if (pressT < 1f) {
            float r = pressT * mMaxRadius;
            canvas.save();
            circlePath.reset();
            circlePath.addCircle(mTouchX, mTouchY, r, Path.Direction.CW);
            canvas.clipPath(circlePath);
            for (int i = 0; i < mPathesCount; ++i) {
                canvas.drawPath(mPathes.get(i), mRipplePaint);
            }
            canvas.restore();
        } else {
            for (int i = 0; i < mPathesCount; ++i) {
                canvas.drawPath(mPathes.get(i), mRipplePaint);
            }
        }

        return pressT < 1f || mReleaseStart >= 0 || (mSupportsLongPress && now - mStart < mLongPressDuration + mDuration);
    }

    public static class LinkCollector {

        private View mParent;

        public LinkCollector() {
        }

        public LinkCollector(View parentView) {
            mParent = parentView;
        }

        private ArrayList<Pair<LinkSpanDrawable, Object>> mLinks = new ArrayList<>();
        private int mLinksCount = 0;

        public void addLink(LinkSpanDrawable link) {
            addLink(link, null);
        }

        public void addLink(LinkSpanDrawable link, Object obj) {
            mLinks.add(new Pair<>(link, obj));
            mLinksCount++;
            invalidate(obj);
        }

        public void removeLink(LinkSpanDrawable link) {
            removeLink(link, true);
        }

        public void removeLink(LinkSpanDrawable link, boolean animated) {
            if (link == null) {
                return;
            }
            Pair<LinkSpanDrawable, Object> pair = null;
            for (int i = 0; i < mLinksCount; ++i) {
                if (mLinks.get(i).first == link) {
                    pair = mLinks.get(i);
                    break;
                }
            }
            if (pair == null) {
                return;
            }
            if (animated) {
                if (link.mReleaseStart < 0) {
                    link.release();
                    invalidate(pair.second);
                    final long now = SystemClock.elapsedRealtime();
                    runOnUIThread(
                            () -> removeLink(link, false),
                            Math.max(0, (link.mReleaseStart - now) + mReleaseDelay + mReleaseDuration)
                    );
                }
            } else {
                mLinks.remove(pair);
                link.reset();
                mLinksCount = mLinks.size();
                invalidate(pair.second);
            }
        }

        private void removeLink(int index, boolean animated) {
            if (index < 0 || index >= mLinksCount) {
                return;
            }
            if (animated) {
                Pair<LinkSpanDrawable, Object> pair = mLinks.get(index);
                LinkSpanDrawable link = pair.first;
                if (link.mReleaseStart < 0) {
                    link.release();
                    invalidate(pair.second);
                    final long now = SystemClock.elapsedRealtime();
                    runOnUIThread(
                            () -> removeLink(link, false),
                            Math.max(0, (link.mReleaseStart - now) + mReleaseDelay + mReleaseDuration)
                    );
                }
            } else {
                Pair<LinkSpanDrawable, Object> pair = mLinks.remove(index);
                LinkSpanDrawable link = pair.first;
                link.reset();
                mLinksCount = mLinks.size();
                invalidate(pair.second);
            }
        }

        public void clear() {
            clear(true);
        }

        public void clear(boolean animated) {
            if (animated) {
                for (int i = 0; i < mLinksCount; ++i) {
                    removeLink(i, true);
                }
            } else if (mLinksCount > 0) {
                for (int i = 0; i < mLinksCount; ++i) {
                    mLinks.get(i).first.reset();
                    invalidate(mLinks.get(i).second, false);
                }
                mLinks.clear();
                mLinksCount = 0;
                invalidate();
            }
        }

        public void removeLinks(Object obj) {
            removeLinks(obj, true);
        }

        public void removeLinks(Object obj, boolean animated) {
            for (int i = 0; i < mLinksCount; ++i) {
                if (mLinks.get(i).second == obj) {
                    removeLink(i, animated);
                }
            }
        }

        public boolean draw(Canvas canvas) {
            boolean invalidate = false;
            for (int i = 0; i < mLinksCount; ++i) {
                invalidate = mLinks.get(i).first.draw(canvas) || invalidate;
            }
            return invalidate;
        }

        public boolean draw(Canvas canvas, Object obj) {
            boolean invalidate = false;
            for (int i = 0; i < mLinksCount; ++i) {
                if (mLinks.get(i).second == obj) {
                    invalidate = mLinks.get(i).first.draw(canvas) || invalidate;
                }
            }
            invalidate(obj, false);
            return invalidate;
        }

        public boolean isEmpty() {
            return mLinksCount <= 0;
        }

        private void invalidate() {
            invalidate(null, true);
        }

        private void invalidate(Object obj) {
            invalidate(obj, true);
        }

        private void invalidate(Object obj, boolean tryParent) {
            if (obj instanceof View) {
                ((View) obj).invalidate();
            } else if (tryParent && mParent != null) {
                mParent.invalidate();
            }
        }
    }

    public static class LinksTextView extends androidx.appcompat.widget.AppCompatTextView {
        public interface OnLinkPress {
            public void run(ClickableSpan span);
        }

        private boolean isCustomLinkCollector;
        private LinkCollector links;
        private ResourcesProvider resourcesProvider;

        private LinkSpanDrawable<ClickableSpan> pressedLink;

        private OnLinkPress onPressListener;
        private OnLinkPress onLongPressListener;

        private boolean disablePaddingsOffset;
        private boolean disablePaddingsOffsetX;
        private boolean disablePaddingsOffsetY;

        public LinksTextView(Context context) {
            this(context, null);
        }

        public LinksTextView(Context context, ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = false;
            this.links = new LinkCollector(this);
            this.resourcesProvider = resourcesProvider;
        }

        public LinksTextView(Context context, LinkCollector customLinkCollector, ResourcesProvider resourcesProvider) {
            super(context);
            this.isCustomLinkCollector = true;
            this.links = customLinkCollector;
            this.resourcesProvider = resourcesProvider;
        }

        public void setDisablePaddingsOffset(boolean disablePaddingsOffset) {
            this.disablePaddingsOffset = disablePaddingsOffset;
        }

        public void setDisablePaddingsOffsetX(boolean disablePaddingsOffsetX) {
            this.disablePaddingsOffsetX = disablePaddingsOffsetX;
        }

        public void setDisablePaddingsOffsetY(boolean disablePaddingsOffsetY) {
            this.disablePaddingsOffsetY = disablePaddingsOffsetY;
        }

        public void setOnLinkPressListener(OnLinkPress listener) {
            onPressListener = listener;
        }

        public void setOnLinkLongPressListener(OnLinkPress listener) {
            onLongPressListener = listener;
        }

        public ClickableSpan hit(int x, int y) {
            Layout textLayout = getLayout();
            if (textLayout == null) {
                return null;
            }
            x -= getPaddingLeft();
            y -= getPaddingTop();
            final int line = textLayout.getLineForVertical(y);
            final int off = textLayout.getOffsetForHorizontal(line, x);
            final float left = getLayout().getLineLeft(line);
            if (left <= x && left + textLayout.getLineWidth(line) >= x && y >= 0 && y <= textLayout.getHeight()) {
                Spannable buffer = new SpannableString(textLayout.getText());
                ClickableSpan[] spans = buffer.getSpans(off, off, ClickableSpan.class);
                if (spans.length != 0) {
                    return spans[0];
                }
            }
            return null;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (links != null) {
                Layout textLayout = getLayout();
                ClickableSpan span;
                if ((span = hit((int) event.getX(), (int) event.getY())) != null) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        final LinkSpanDrawable<ClickableSpan> link = new LinkSpanDrawable<>(span, resourcesProvider, event.getX(), event.getY());
                        pressedLink = link;
                        links.addLink(pressedLink);
                        Spannable buffer = new SpannableString(textLayout.getText());
                        int start = buffer.getSpanStart(pressedLink.getSpan());
                        int end = buffer.getSpanEnd(pressedLink.getSpan());
                        LinkPath path = pressedLink.obtainNewPath();
                        path.setCurrentLayout(textLayout, start, getPaddingTop());
                        textLayout.getSelectionPath(start, end, path);
                        runOnUIThread(() -> {
                            if (onLongPressListener != null && pressedLink == link) {
                                onLongPressListener.run(span);
                                pressedLink = null;
                                links.clear();
                            }
                        }, ViewConfiguration.getLongPressTimeout());
                        return true;
                    }
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    links.clear();
                    if (pressedLink != null && pressedLink.getSpan() == span) {
                        if (onPressListener != null) {
                            onPressListener.run(pressedLink.getSpan());
                        } else if (pressedLink.getSpan() != null) {
                            pressedLink.getSpan().onClick(this);
                        }
                        pressedLink = null;
                        return true;
                    }
                    pressedLink = null;
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    links.clear();
                    pressedLink = null;
                }
            }
            return pressedLink != null || super.onTouchEvent(event);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            if (!isCustomLinkCollector) {
                canvas.save();
                if (!disablePaddingsOffset) {
                    canvas.translate(disablePaddingsOffsetX ? 0 : getPaddingLeft(), disablePaddingsOffsetY ? 0 : getPaddingTop());
                }
                if (links.draw(canvas)) {
                    invalidate();
                }
                canvas.restore();
            }
            super.onDraw(canvas);
        }
    }

    public static class ClickableSmallTextView extends SimpleTextView {
        private final ResourcesProvider resourcesProvider;

        public ClickableSmallTextView(Context context) {
            this(context, null);
        }

        public ClickableSmallTextView(Context context, ResourcesProvider resourcesProvider) {
            super(context);
            this.resourcesProvider = resourcesProvider;
        }

        private int getLinkColor() {
            return ColorUtils.setAlphaComponent(getTextColor(), (int) (Color.alpha(getTextColor()) * .1175f));
        }

        private final LinkCollector links = new LinkCollector(this);
        private final Paint linkBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        @Override
        protected void onDraw(Canvas canvas) {
            if (isClickable()) {
                AndroidUtilities.rectTmp.set(0, 0, getPaddingLeft() + getTextWidth() + getPaddingRight(), getHeight());
                linkBackgroundPaint.setColor(getLinkColor());
                canvas.drawRoundRect(AndroidUtilities.rectTmp, dp(CORNER_RADIUS_DP), dp(CORNER_RADIUS_DP), linkBackgroundPaint);
            }

            super.onDraw(canvas);

            if (isClickable() && links.draw(canvas)) {
                invalidate();
            }
        }

        private LinkSpanDrawable<ClickableSpan> pressedLink;

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            if (!isClickable()) {
                return super.onTouchEvent(event);
            }
            if (links != null) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    final LinkSpanDrawable<ClickableSpan> link = new LinkSpanDrawable<>(null, resourcesProvider, event.getX(), event.getY());
                    link.setColor(getLinkColor());
                    pressedLink = link;
                    links.addLink(pressedLink);
                    LinkPath path = pressedLink.obtainNewPath();
                    path.setCurrentLayout(null, 0, 0, 0);
                    path.addRect(0, 0, getPaddingLeft() + getTextWidth() + getPaddingRight(), getHeight(), Path.Direction.CW);
                    runOnUIThread(() -> {
                        if (pressedLink == link) {
                            performLongClick();
                            pressedLink = null;
                            links.clear();
                        }
                    }, ViewConfiguration.getLongPressTimeout());
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    links.clear();
                    if (pressedLink != null) {
                        performClick();
                    }
                    pressedLink = null;
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                    links.clear();
                    pressedLink = null;
                    return true;
                }
            }
            return pressedLink != null || super.onTouchEvent(event);
        }
    }
}