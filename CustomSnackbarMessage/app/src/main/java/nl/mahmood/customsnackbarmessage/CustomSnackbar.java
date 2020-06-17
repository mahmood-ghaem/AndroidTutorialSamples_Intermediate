package nl.mahmood.customsnackbarmessage;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;


public class CustomSnackbar extends BaseTransientBottomBar<CustomSnackbar>
{

    public enum Color
    {
        RED,
        GREEN,
        BLUE
    }

    /**
     * Constructor for the transient bottom bar.
     *
     * @param parent   The parent for this transient bottom bar.
     * @param content  The content view for this transient bottom bar.
     * @param callback The content view callback for this transient bottom bar.
     */
    private CustomSnackbar(ViewGroup parent, View content, ContentViewCallback callback)
    {
        super(parent, content, callback);
        getView().setBackgroundColor(ContextCompat.getColor(content.getContext(), android.R.color.transparent));
        getView().setPadding(10, 0, 10, 0);
    }

    /*
    To make CustomSnackbar
     */
    public static CustomSnackbar make(@NonNull ViewGroup parent, @Duration int duration)
    {
        ViewGroup root = findSuitableParent(parent);
        if (root != null)
        {
            final LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            final View content = inflater.inflate(R.layout.snackbar_view, parent, false);
            final ContentViewCallback viewCallback = new ContentViewCallback(content);
            final CustomSnackbar customSnackbar = new CustomSnackbar(parent, content, viewCallback);
            customSnackbar.setDuration(duration);
            return customSnackbar;
        } else
        {
            return null;
        }
    }

    public CustomSnackbar setColor(Color color)
    {
        ViewGroup layoutTopColor = getView().findViewById(R.id.layoutTopColor);
        switch (color)
        {
            case RED:
                layoutTopColor.setBackground(getContext().getDrawable(R.drawable.snackbar_top_color_red));
                break;
            case GREEN:
                layoutTopColor.setBackground(getContext().getDrawable(R.drawable.snackbar_top_color_green));
                break;
            case BLUE:
                layoutTopColor.setBackground(getContext().getDrawable(R.drawable.snackbar_top_color_blue));
                break;
        }

        return this;
    }

    public CustomSnackbar setIcon(int drawable)
    {
        ImageView imageViewIcon = getView().findViewById(R.id.imageViewIcon);
        imageViewIcon.setVisibility(View.VISIBLE);
        imageViewIcon.setImageResource(drawable);
        return this;
    }

    public CustomSnackbar setText(CharSequence text)
    {

        TextView textViewSnackbarText = getView().findViewById(R.id.textViewSnackbarText);
        textViewSnackbarText.setText(text);
        return this;
    }

    public CustomSnackbar setAction(CharSequence text, final View.OnClickListener listener)
    {
        Button buttonSnackbarAction = getView().findViewById(R.id.buttonSnackbarAction);
        buttonSnackbarAction.setText(text);
        buttonSnackbarAction.setVisibility(View.VISIBLE);
        buttonSnackbarAction.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                listener.onClick(view);
                // Now dismiss the Snackbar
                dismiss();
            }
        });
        return this;
    }

    /**
     * The purpose of this method is to lookup the upper view group of the view until the Coordinator Layout is found or the root layout is finished,
     * and return to the view group found.
     * Root Layout: The layout of id for android.R.id.content is actually the parent ViewGroup of the layout that we set ContentView to write ourselves.
     * The type is FrameLayout.
     */
    private static ViewGroup findSuitableParent(View view)
    {
        ViewGroup fallback = null;
        do
        {
            if (view instanceof CoordinatorLayout)
            {
                // We've found a CoordinatorLayout, use it
                return (ViewGroup) view;
            } else if (view instanceof FrameLayout)
            {
                if (view.getId() == android.R.id.content)
                {
                    // If we've hit the decor content view, then we didn't find a CoL in the
                    // hierarchy, so use it.
                    return (ViewGroup) view;
                } else
                {
                    // It's not the content view but we'll use it as our fallback
                    fallback = (ViewGroup) view;
                }
            }

            if (view != null)
            {
                // Else, we will loop and crawl up the view hierarchy and try to find a parent
                final ViewParent parent = view.getParent();
                view = parent instanceof View ? (View) parent : null;
            }
        } while (view != null);

        // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
        return fallback;
    }

    private static class ContentViewCallback implements com.google.android.material.snackbar.ContentViewCallback
    {
        ImageView imageViewIcon;
        TextView textViewSnackbarText;
        Button buttonSnackbarAction;
        ObjectAnimator scaleXIcon;
        ObjectAnimator scaleYIcon;
        AnimatorSet animatorSetIcon;
        ValueAnimator fadeAnimText;
        ObjectAnimator translationXButton;
        ObjectAnimator fadeAnimButton;

        public ContentViewCallback(View content)
        {
            imageViewIcon = content.findViewById(R.id.imageViewIcon);
            textViewSnackbarText = content.findViewById(R.id.textViewSnackbarText);
            textViewSnackbarText.setAlpha(0);
            buttonSnackbarAction = content.findViewById(R.id.buttonSnackbarAction);
            buttonSnackbarAction.setAlpha(0);
        }

        @Override
        public void animateContentIn(int delay, int duration)
        {
//          https://developer.android.com/guide/topics/graphics/prop-animation

            scaleXIcon = ObjectAnimator.ofFloat(imageViewIcon, View.SCALE_X, 0f, 1f);
            scaleYIcon = ObjectAnimator.ofFloat(imageViewIcon, View.SCALE_Y, 0f, 1f);
            animatorSetIcon = new AnimatorSet();
            animatorSetIcon.playTogether(scaleXIcon, scaleYIcon);
            animatorSetIcon.setInterpolator(new OvershootInterpolator());
            animatorSetIcon.setDuration(1000);

            fadeAnimText = ObjectAnimator.ofFloat(textViewSnackbarText, "alpha", 0f, 1f).setDuration(500);
            translationXButton = ObjectAnimator.ofFloat(buttonSnackbarAction, "translationX", 100f).setDuration(500);
            fadeAnimButton = ObjectAnimator.ofFloat(buttonSnackbarAction, "alpha", 0f, 1f).setDuration(500);

            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.play(animatorSetIcon).before(fadeAnimText);
            animatorSet.play(fadeAnimText).with(translationXButton);
            animatorSet.play(fadeAnimText).with(fadeAnimButton);
            animatorSet.start();


        }

        @Override
        public void animateContentOut(int delay, int duration)
        {
            // Reset all views
            imageViewIcon.setVisibility(View.GONE);
            textViewSnackbarText.setAlpha(0);
            textViewSnackbarText.setText("");
            buttonSnackbarAction.setVisibility(View.INVISIBLE);
            buttonSnackbarAction.setText("");
            buttonSnackbarAction.setTranslationX(0);
            buttonSnackbarAction.setAlpha(0);
        }
    }
}