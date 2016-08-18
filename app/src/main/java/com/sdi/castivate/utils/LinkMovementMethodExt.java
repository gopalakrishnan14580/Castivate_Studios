package com.sdi.castivate.utils;

import android.content.Context;
import android.text.Layout;
import android.text.Spannable;
import android.text.method.LinkMovementMethod;
import android.text.method.MovementMethod;
import android.text.style.ClickableSpan;
import android.view.MotionEvent;
import android.widget.TextView;
import android.widget.Toast;

public class LinkMovementMethodExt extends LinkMovementMethod {
    private static LinkMovementMethod sInstance;

    Context context;
    public static MovementMethod getInstance() {
        if (sInstance == null){
        	sInstance = new LinkMovementMethodExt();
        }
        return sInstance;
    }

    @Override
    public boolean onTouchEvent(final TextView widget, final Spannable buffer, final MotionEvent event) {
        final int action = event.getAction();
        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_DOWN) {
            final int x = (int) event.getX() - widget.getTotalPaddingLeft() + widget.getScrollX();
            final int y = (int) event.getY() - widget.getTotalPaddingTop() + widget.getScrollY();
            final Layout layout = widget.getLayout();
            final int line = layout.getLineForVertical(y);
            final int off = layout.getOffsetForHorizontal(line, x);
            final ClickableSpan[] link = buffer.getSpans(off, off, ClickableSpan.class);
            if (link.length != 0) {
                //do something with the clicked item...
            	
            	System.out.println("Hi");
            	
                return true;
            }
        }
        return super.onTouchEvent(widget, buffer, event);
    }

}