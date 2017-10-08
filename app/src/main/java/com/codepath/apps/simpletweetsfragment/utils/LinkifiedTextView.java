package com.codepath.apps.simpletweetsfragment.utils;

import android.support.annotation.Nullable;
import android.content.Context;
import android.text.Layout;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

/**
 * This class is the attempt to solve the issue of when using android:autoLink, it breaks the
 * ability ro respond to events on the ListView through setOnItemClickListener.
 * This class extends from TextView to modify the onTouchEvent to correctly propagate
 * (communicate) the click.
 */

public class LinkifiedTextView extends TextView {

    public LinkifiedTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        TextView widget = (TextView) this;
        Object text = widget.getText(); // get current text
        // Spanned: interface for text that has markup bjects attached to ranges of it.
        // if text contains markup objects(Spanned)
        if (text instanceof Spanned) {
            // Spannable: interface for text to which markup objects can be attached and detached.
            Spannable buffer = (Spannable) text;
            // get onTouchEvent action
            int action = event.getAction();
            // ACTION_UP: A pressed gesture has finished, the motion contains the final release
            // location as well as any intermediate points since the last down or move event.
            // ACTION_DOWN: A pressed gesture has started, the motion contains the initial
            // starting location.
            if (action == MotionEvent.ACTION_UP
                    || action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX(); // x coordinate of the event happened
                int y = (int) event.getY(); // y coordinate of the event happened
                // Returns the total left padding of the view
                x -= widget.getTotalPaddingLeft();
                // Returns the total top padding of the view
                y -= widget.getTotalPaddingTop();
                // Return the scrolled left position of this view
                x += widget.getScrollX();
                // Return the scrolled top position of this view
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                // Get the character offset on the specified line whose position is closest to
                // the specified horizontal position.
                int off = layout.getOffsetForHorizontal(line, x);

                ClickableSpan[] link = buffer.getSpans(off, off,
                        ClickableSpan.class);

                if (link.length != 0) {
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        Selection.setSelection(buffer,
                                buffer.getSpanStart(link[0]),
                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                }
            }
        }
        return false;
    }
}
