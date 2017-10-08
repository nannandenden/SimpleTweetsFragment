package com.codepath.apps.simpletweetsfragment.utils;

import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Create clickable spans within a TextView.
 *
 */
public class PatternEditableBuilder {
    private final List<SpannablePatternItem> patterns;
    // records the pattern spans to apply to a TextView
    /* This stores a particular pattern item complete with pattern,
    span styles, and click listener */
    public class SpannablePatternItem {

        public Pattern pattern;
        public SpannableStyleListener styles;
        public SpannableClickedListener listener;

        public SpannablePatternItem(Pattern pattern, SpannableStyleListener styles,
                                    SpannableClickedListener listener) {
            this.pattern = pattern;
            this.styles = styles;
            this.listener = listener;
        }
    }

    /* This stores the style listener for a pattern item.
    Used to style a particular category of spans. */
    public static abstract class SpannableStyleListener {
        public int spanTextColor;

        public SpannableStyleListener() {
        }

        public SpannableStyleListener(int spanTextColor) {
            this.spanTextColor = spanTextColor;
        }

        abstract void onSpanStyled(TextPaint ds);
    }

    /* This stores the click listener for a pattern item.
    Used to handle clicks to a particular category of spans */
    public interface SpannableClickedListener {
        void onSpanClicked(String text);
    }

    /* This is the custom clickable span class used to handle user clicks to our pattern spans
    applying the styles and invoking click listener. */
    // ClickableSpan: If an object of this type is attached to the text of a Textview,
    // with a movement methods of LinkMovementMethod, the affected spans of
    // text can be selected. If clicked, The onClick method will be called.
    // so... ClickableSpan can be attched to textview and when the textview is clicked,
    // you can perform some actions
    public class StyledClickableSpan extends ClickableSpan {
        SpannablePatternItem item;


        public StyledClickableSpan(SpannablePatternItem item) {
            this.item = item;
        }
        // Makes the text underlined and in the link color.
        // TextPaint is an extension of Paint that leaves room for some extra
        // data used during text measuring and drawing
        @Override
        public void updateDrawState(TextPaint ds) {
            super.updateDrawState(ds);
            if (item.styles != null) {
                item.styles.onSpanStyled(ds);
            }
        }
        // define the onclick method
        @Override
        public void onClick(View widget) {
            if (item.listener != null) {
                TextView tv = (TextView) widget; // cast to TextView
                Spanned span = (Spanned) tv.getText();// cast to markup objects
                int start = span.getSpanStart(this);
                int end = span.getSpanEnd(this);
                CharSequence text = span.subSequence(start, end
                );
                // linked to the callback
                item.listener.onSpanClicked(text.toString());
            }
            // invalidate the whole view
            widget.invalidate();
        }
    }

    public PatternEditableBuilder() {
        this.patterns = new ArrayList<>();
    }
    /* 'addPattern' overloaded signature
    Each allows us to add a span pattern with different arguments */
    public PatternEditableBuilder addPattern(Pattern pattern, SpannableStyleListener spanStyle,
                                             SpannableClickedListener listener) {
        patterns.add(new SpannablePatternItem(pattern, spanStyle, listener));
        return this;
    }

    public PatternEditableBuilder addPattern(Pattern pattern, SpannableStyleListener spanStyle) {
        patterns.add(new SpannablePatternItem(pattern, spanStyle, null));
        return this;
    }

    public PatternEditableBuilder addPattern(Pattern pattern) {
        patterns.add(new SpannablePatternItem(pattern, null, null));
        return this;
    }

    public PatternEditableBuilder addPattern(Pattern pattern, int spanTextColor,
                                             SpannableClickedListener listener) {
        SpannableStyleListener styles = new SpannableStyleListener(spanTextColor) {
            @Override
            void onSpanStyled(TextPaint ds) {
                ds.linkColor = this.spanTextColor;
            }
        };
        addPattern(pattern, styles, listener);
        return this;
    }

    public PatternEditableBuilder addPattern(Pattern pattern, int textColor) {
        addPattern(pattern, textColor, null);
        return this;
    }

    public PatternEditableBuilder addPattern(Pattern pattern, SpannableClickedListener listener) {
        addPattern(pattern, null, listener);
        return this;
    }

    // this builds the pattern span and applies to a textview
    public void into(TextView textView) {
        SpannableStringBuilder result = build(textView.getText());
        textView.setText(result);
        // LinkMovementMethod: A movement method that traverses links in the text buffer and scrolls
        // if necessary.
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public SpannableStringBuilder build(CharSequence editable) {
        SpannableStringBuilder ssb = new SpannableStringBuilder(editable);
        for (SpannablePatternItem item : patterns) {
            Matcher matcher = item.pattern.matcher(ssb);
            while (matcher.find()) {
                int start = matcher.start();
                int end = matcher.end();
                StyledClickableSpan url = new StyledClickableSpan(item);
                ssb.setSpan(url, start, end, 0);
            }
        }
        return ssb;
    }
}
