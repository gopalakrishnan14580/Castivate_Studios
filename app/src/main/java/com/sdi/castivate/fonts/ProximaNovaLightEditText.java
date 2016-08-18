package com.sdi.castivate.fonts;

import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;

import com.sdi.castivate.CastingScreen;

public class ProximaNovaLightEditText extends EditText
{

    Context context;


    public ProximaNovaLightEditText(Context context, AttributeSet attrs, int defStyle)
    {
	super(context, attrs, defStyle);
	this.context = context;
	inite();
    }

    public ProximaNovaLightEditText(Context context, AttributeSet attrs)
    {
	super(context, attrs);
	this.context = context;
	inite();
    }

    public ProximaNovaLightEditText(Context context)
    {
	super(context);
	this.context = context;
	inite();
    }
    @Override
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            dispatchKeyEvent(event);
CastingScreen.tap_current_location.setVisibility(View.GONE);


            return false;
        }
        return super.onKeyPreIme(keyCode, event);
    }
    private void inite()
    {
	if (!isInEditMode())
	{
	    // Typeface face = Typeface.createFromAsset(context.getAssets(),
	    // "Bariol_Regular.otf");
	    // this.setTypeface(face);

	    this.setTypeface(TypeFaceProvider.get(context, "fonts/PROXIMANOVA-LIGHT.OTF"));

	}
    }
}
