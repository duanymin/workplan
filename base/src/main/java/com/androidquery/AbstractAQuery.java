/*
 * Copyright 2011 - AndroidQuery.com (tinyeeliu@gmail.com)
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.androidquery;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextPaint;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.facebook.drawee.generic.GenericDraweeHierarchy;
import com.facebook.drawee.view.SimpleDraweeView;
import com.util.DensityUtil;

import org.apache.http.HttpHost;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.WeakHashMap;


/**
 * The core class of AQuery. Contains all the methods available from an AQuery object.
 *
 * @param <T> the generic type
 */
public abstract class AbstractAQuery<T extends AbstractAQuery<T>> {

    private View root;
    private Activity act;
    @Nullable
    private Context context;

    protected View view;
    @Nullable
    protected Object progress;
    @Nullable
    private HttpHost proxy;

    @Nullable
    @SuppressWarnings("unchecked")
    protected T create(View view) {

        AbstractAQuery<?> result = null;

        try {
            Constructor<? extends AbstractAQuery> c = getConstructor();
            result = c.newInstance(view);
            result.act = act;
        } catch (Exception e) {
            //should never happen
            e.printStackTrace();
        }
        return (T) result;

    }


    private Constructor<? extends AbstractAQuery> constructor;

    private Constructor<? extends AbstractAQuery> getConstructor() {

        if (constructor == null) {
            try {
                constructor = getClass().getConstructor(View.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return constructor;
    }


    /**
     * Instantiates a new AQuery object.
     *
     * @param act Activity that's the parent of the to-be-operated views.
     */
    public AbstractAQuery(Activity act) {
        this.act = act;
    }

    /**
     * Instantiates a new AQuery object.
     *
     * @param root View container that's the parent of the to-be-operated views.
     */
    public AbstractAQuery(View root) {
        this.root = root;
        this.view = root;
    }

    /**
     * Instantiates a new AQuery object. This constructor should be used for Fragments.
     *
     * @param act  Activity
     * @param root View container that's the parent of the to-be-operated views.
     */
    public AbstractAQuery(Activity act, View root) {
        this.root = root;
        this.view = root;
        this.act = act;
    }


    /**
     * Instantiates a new AQuery object.
     *
     * @param context Context that will be used in async operations.
     */

    public AbstractAQuery(Context context) {
        this.context = context;
    }

    @Nullable
    private View findView(int id) {
        View result = null;
        if (root != null) {
            result = root.findViewById(id);
        } else if (act != null) {
            result = act.findViewById(id);
        }
        return result;
    }

    @Nullable
    private View findView(String tag) {

        //((ViewGroup)findViewById(android.R.id.content)).getChildAt(0)
        View result = null;
        if (root != null) {
            result = root.findViewWithTag(tag);
        } else if (act != null) {
            //result = act.findViewById(id);
            View top = ((ViewGroup) act.findViewById(android.R.id.content)).getChildAt(0);
            if (top != null) {
                result = top.findViewWithTag(tag);
            }
        }
        return result;

    }

    @Nullable
    private View findView(@NotNull int... path) {

        View result = findView(path[0]);

        for (int i = 1; i < path.length && result != null; i++) {
            result = result.findViewById(path[i]);
        }

        return result;

    }


    /**
     * Return a new AQuery object that uses the found view as a root.
     *
     * @param id the id
     * @return new AQuery object
     */
    @Nullable
    public T find(int id) {
        View view = findView(id);
        return create(view);
    }

    /**
     * Return a new AQuery object that uses the found parent as a root.
     * If no parent with matching id is found, operating view will be null and isExist() will return false.
     *
     * @param id the parent id
     * @return new AQuery object
     */
    @Nullable
    public T parent(int id) {

        View node = view;
        View result = null;

        while (node != null) {
            if (node.getId() == id) {
                result = node;
                break;
            }
            ViewParent p = node.getParent();
            if (!(p instanceof View)) break;
            node = (View) p;
        }

        return create(result);
    }


    /**
     * Recycle this AQuery object.
     * <p/>
     * The method is designed to avoid recreating an AQuery object repeatedly, such as when in list adapter getView method.
     *
     * @param root The new root of the recycled AQuery.
     * @return self
     */
    @NotNull
    public T recycle(View root) {
        this.root = root;
        this.view = root;
        reset();
        this.context = null;
        return self();
    }


    @NotNull
    @SuppressWarnings("unchecked")
    protected T self() {
        return (T) this;
    }

    /**
     * Return the current operating view.
     *
     * @return the view
     */
    public View getView() {
        return view;
    }

    /**
     * Points the current operating view to the first view found with the id under the root.
     *
     * @param id the id
     * @return self
     */
    @NotNull
    public T id(int id) {

        return id(findView(id));
    }

    /**
     * Points the current operating view to the specified view.
     *
     * @param view
     * @return self
     */
    @NotNull
    public T id(View view) {
        this.view = view;
        reset();
        return self();
    }


    /**
     * Points the current operating view to the specified view with tag.
     *
     * @param tag
     * @return self
     */

    @NotNull
    public T id(String tag) {
        return id(findView(tag));
    }

    /**
     * Find the first view with first id, under that view, find again with 2nd id, etc...
     *
     * @param path The id path.
     * @return self
     */
    @NotNull
    public T id(int... path) {

        return id(findView(path));
    }

    /**
     * Find the progress bar and show the progress for the next ajax/image request.
     * Once ajax or image is called, current progress view is consumed.
     * Subsequent ajax/image calls won't show progress view unless progress is called again.
     * <p/>
     * If a file or network requests is required, the progress bar is set to be "VISIBLE".
     * Once the requests completes, progress bar is set to "GONE".
     *
     * @param id the id of the progress bar to be shown
     * @return self
     */
    @NotNull
    public T progress(int id) {
        progress = findView(id);
        return self();
    }


    /**
     * Set the progress bar and show the progress for the next ajax/image request.
     * <p/>
     * Once ajax or image is called, current progress view is consumed.
     * Subsequent ajax/image calls won't show progress view unless progress is called again.
     * <p/>
     * If a file or network requests is required, the progress bar is set to be "VISIBLE".
     * Once the requests completes, progress bar is set to "GONE".
     *
     * @param view the progress bar to be shown
     * @return self
     */

    @NotNull
    public T progress(Object view) {
        progress = view;
        return self();
    }

    /**
     * Set the progress dialog and show the progress for the next ajax/image request.
     * <p/>
     * Progress dialogs cannot be reused. They are dismissed on ajax callback.
     * <p/>
     * If a file or network requests is required, the dialog is shown.
     * Once the requests completes, dialog is dismissed.
     * <p/>
     * It's the caller responsibility to dismiss the dialog when the activity terminates before the ajax is completed.
     * Calling aq.dismiss() in activity's onDestroy() will ensure all dialogs are properly dismissed.
     *
     * @param dialog
     * @return self
     */

    @NotNull
    public T progress(Dialog dialog) {
        progress = dialog;
        return self();
    }


    protected void reset() {
        progress = null;
        proxy = null;
    }

    /**
     * Set the rating of a RatingBar.
     *
     * @param rating the rating
     * @return self
     */
    @NotNull
    public T rating(float rating) {

        if (view instanceof RatingBar) {
            RatingBar rb = (RatingBar) view;
            rb.setRating(rating);
        }
        return self();
    }


    /**
     * Set the text of a TextView.
     *
     * @param resid the resid
     * @return self
     */
    @NotNull
    public T text(int resid) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(resid);
        }
        return self();
    }

    @NotNull
    public T error(String errorInfo) {
        if (view instanceof EditText) {
            EditText et = (EditText) view;
            et.setError(errorInfo);
        }
        return self();
    }

    @NotNull
    public T compoundDrawables(Drawable left, Drawable top, Drawable right, Drawable bottom) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            setBounds(left, top, right, bottom);
            tv.setCompoundDrawables(left, top, right, bottom);
        }
        return self();
    }

    private void setBounds(@NotNull Drawable... drawables) {
        for (Drawable drawable : drawables) {
            if (drawable != null) {
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
            }
        }
    }

    @NotNull
    public T fakeBoldText(boolean b) {
        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            TextPaint tp = tv.getPaint();
            tp.setFakeBoldText(b);
        }
        return self();
    }

    @NotNull
    public T focus() {
        view.requestFocus();
        return self();
    }


    /**
     * Set the text of a TextView with localized formatted string
     * from application's package's default string table
     *
     * @param resid the resid
     * @return self
     * @see Context#getString(int, Object...)
     */
    @NotNull
    public T text(int resid, Object... formatArgs) {
        Context context = getContext();
        if (context != null) {
            CharSequence text = context.getString(resid, formatArgs);
            text(text);
        }
        return self();
    }

    /**
     * Set the text of a TextView.
     *
     * @param text the text
     * @return self
     */
    @NotNull
    public T text(CharSequence text) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }

        return self();
    }

    /**
     * Set the text of a TextView. Hide the view (gone) if text is empty.
     *
     * @param text        the text
     * @param goneIfEmpty hide if text is null or length is 0
     * @return self
     */

    @NotNull
    public T text(@Nullable CharSequence text, boolean goneIfEmpty) {

        if (goneIfEmpty && (text == null || text.length() == 0)) {
            return gone();
        } else {
            return text(text);
        }
    }


    /**
     * Set the text of a TextView.
     *
     * @param text the text
     * @return self
     */
    @NotNull
    public T text(Spanned text) {


        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setText(text);
        }
        return self();
    }

    /**
     * Set the text color of a TextView. Note that it's not a color resource id.
     *
     * @param color color code in ARGB
     * @return self
     */
    @NotNull
    public T textColor(int color) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextColor(color);
        }
        return self();
    }

    /**
     * Set the text color of a TextView from  a color resource id.
     *
     * @return self
     */
    @NotNull
    public T textColorId(int id) {

        return textColor(getContext().getResources().getColor(id));
    }


    /**
     * Set the text typeface of a TextView.
     *
     * @return self
     */
    @NotNull
    public T typeface(Typeface tf) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTypeface(tf);
        }
        return self();
    }

    /**
     * Set the text size (in sp) of a TextView.
     *
     * @param size size
     * @return self
     */
    @NotNull
    public T textSize(float size) {

        if (view instanceof TextView) {
            TextView tv = (TextView) view;
            tv.setTextSize(size);
        }
        return self();
    }


    /**
     * Set the adapter of an AdapterView.
     *
     * @param adapter adapter
     * @return self
     */

    @NotNull
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T adapter(Adapter adapter) {

        if (view instanceof AdapterView) {
            AdapterView av = (AdapterView) view;
            av.setAdapter(adapter);
        }

        return self();
    }

    /**
     * Set the adapter of an ExpandableListView.
     *
     * @param adapter adapter
     * @return self
     */
    @NotNull
    public T adapter(ExpandableListAdapter adapter) {

        if (view instanceof ExpandableListView) {
            ExpandableListView av = (ExpandableListView) view;
            av.setAdapter(adapter);
        }

        return self();
    }

    /**
     * Set the image of an ImageView.
     *
     * @param resid the resource id
     * @return self
     */
    @NotNull
    public T image(int resid) {
        if (view instanceof ImageView) {
            ImageView iv = (ImageView) view;
            if (resid == 0) {
                iv.setImageBitmap(null);
            } else {
                iv.setImageResource(resid);
            }
        }
        if (view instanceof SimpleDraweeView) {
            SimpleDraweeView sdv = (SimpleDraweeView) view;
            GenericDraweeHierarchy hierarchy = sdv.getHierarchy();
            if (resid == 0) {
                hierarchy.setPlaceholderImage(null);
            } else {
                hierarchy.setPlaceholderImage(resid);
            }
        }
        return self();
    }

    @NotNull
    public T image(Uri uri) {
        if (view instanceof SimpleDraweeView) {
            SimpleDraweeView sdv = (SimpleDraweeView) view;
            if (uri != null) {
                sdv.setImageURI(uri);
            }
        }
        return self();
    }

    /**
     * Set tag object of a view.
     *
     * @param tag
     * @return self
     */
    @NotNull
    public T tag(Object tag) {

        if (view != null) {
            view.setTag(tag);
        }

        return self();
    }

    /**
     * Set tag object of a view.
     *
     * @param key
     * @param tag
     * @return self
     */
    @NotNull
    public T tag(int key, Object tag) {

        if (view != null) {
            view.setTag(key, tag);
        }

        return self();
    }


    /**
     * Enable a view.
     *
     * @param enabled state
     * @return self
     */
    @NotNull
    public T enabled(boolean enabled) {

        if (view != null) {
            view.setEnabled(enabled);
        }

        return self();
    }

    /**
     * Set checked state of a compound button.
     *
     * @param checked state
     * @return self
     */
    @NotNull
    public T checked(boolean checked) {

        if (view instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) view;
            cb.setChecked(checked);
        }

        return self();
    }

    /**
     * Get checked state of a compound button.
     *
     * @return checked
     */
    public boolean isChecked() {

        boolean checked = false;

        if (view instanceof CompoundButton) {
            CompoundButton cb = (CompoundButton) view;
            checked = cb.isChecked();
        }

        return checked;
    }

    /**
     * Set clickable for a view.
     *
     * @param clickable
     * @return self
     */
    @NotNull
    public T clickable(boolean clickable) {

        if (view != null) {
            view.setClickable(clickable);
        }

        return self();
    }


    /**
     * Set view visibility to View.GONE.
     *
     * @return self
     */
    @NotNull
    public T gone() {
        /*
        if(view != null && view.getVisibility() != View.GONE){
			view.setVisibility(View.GONE);
		}
		
		return self();
		*/
        return visibility(View.GONE);
    }

    /**
     * Set view visibility to View.INVISIBLE.
     *
     * @return self
     */
    @NotNull
    public T invisible() {

		/*
        if(view != null && view.getVisibility() != View.INVISIBLE){
			view.setVisibility(View.INVISIBLE);
		}
		
		return self();
		*/
        return visibility(View.INVISIBLE);
    }

    /**
     * Set view visibility to View.VISIBLE.
     *
     * @return self
     */
    @NotNull
    public T visible() {

		/*
        if(view != null && view.getVisibility() != View.VISIBLE){
			view.setVisibility(View.VISIBLE);
		}
		
		return self();
		*/
        return visibility(View.VISIBLE);
    }

    /**
     * Set view visibility, such as View.VISIBLE.
     *
     * @return self
     */
    @NotNull
    public T visibility(int visibility) {

        if (view != null && view.getVisibility() != visibility) {
            view.setVisibility(visibility);
        }

        return self();
    }


    /**
     * Set view background.
     *
     * @param id the id
     * @return self
     */
    @NotNull
    public T background(int id) {

        if (view != null) {

            if (id != 0) {
                view.setBackgroundResource(id);
            } else {
                view.setBackgroundDrawable(null);
            }

        }

        return self();
    }

    /**
     * Set view background color.
     *
     * @param color color code in ARGB
     * @return self
     */
    @NotNull
    public T backgroundColor(int color) {

        if (view != null) {
            view.setBackgroundColor(color);
        }

        return self();
    }

    /**
     * Set view background color.
     *
     * @return self
     */
    @NotNull
    public T backgroundColorId(int colorId) {

        if (view != null) {
            view.setBackgroundColor(getContext().getResources().getColor(colorId));
        }

        return self();
    }

    /**
     * Notify a ListView that the data of it's adapter is changed.
     *
     * @return self
     */
    @NotNull
    public T dataChanged() {

        if (view instanceof AdapterView) {

            AdapterView<?> av = (AdapterView<?>) view;
            Adapter a = av.getAdapter();

            if (a instanceof BaseAdapter) {
                BaseAdapter ba = (BaseAdapter) a;
                ba.notifyDataSetChanged();
            }

        }


        return self();
    }


    /**
     * Checks if the current view exist.
     *
     * @return true, if is exist
     */
    public boolean isExist() {
        return view != null;
    }

    /**
     * Gets the tag of the view.
     *
     * @return tag
     */
    @Nullable
    public Object getTag() {
        Object result = null;
        if (view != null) {
            result = view.getTag();
        }
        return result;
    }

    /**
     * Gets the tag of the view.
     *
     * @param id the id
     * @return tag
     */
    @Nullable
    public Object getTag(int id) {
        Object result = null;
        if (view != null) {
            result = view.getTag(id);
        }
        return result;
    }

    /**
     * Gets the current view as an image view.
     *
     * @return ImageView
     */
    @NotNull
    public ImageView getImageView() {
        return (ImageView) view;
    }

    /**
     * Gets the current view as an Gallery.
     *
     * @return Gallery
     */
    @NotNull
    public Gallery getGallery() {
        return (Gallery) view;
    }


    /**
     * Gets the current view as a text view.
     *
     * @return TextView
     */
    @NotNull
    public TextView getTextView() {
        return (TextView) view;
    }

    /**
     * Gets the current view as an edit text.
     *
     * @return EditText
     */
    @NotNull
    public EditText getEditText() {
        return (EditText) view;
    }

    /**
     * Gets the current view as an progress bar.
     *
     * @return ProgressBar
     */
    @NotNull
    public ProgressBar getProgressBar() {
        return (ProgressBar) view;
    }

    /**
     * Gets the current view as seek bar.
     *
     * @return SeekBar
     */

    @NotNull
    public SeekBar getSeekBar() {
        return (SeekBar) view;
    }

    /**
     * Gets the current view as a button.
     *
     * @return Button
     */
    @NotNull
    public Button getButton() {
        return (Button) view;
    }

    /**
     * Gets the current view as a checkbox.
     *
     * @return CheckBox
     */
    @NotNull
    public CheckBox getCheckBox() {
        return (CheckBox) view;
    }

    /**
     * Gets the current view as a listview.
     *
     * @return ListView
     */
    @NotNull
    public ListView getListView() {
        return (ListView) view;
    }

    /**
     * Gets the current view as a ExpandableListView.
     *
     * @return ExpandableListView
     */
    @NotNull
    public ExpandableListView getExpandableListView() {
        return (ExpandableListView) view;
    }

    /**
     * Gets the current view as a gridview.
     *
     * @return GridView
     */
    @NotNull
    public GridView getGridView() {
        return (GridView) view;
    }

    /**
     * Gets the current view as a RatingBar.
     *
     * @return RatingBar
     */
    @NotNull
    public RatingBar getRatingBar() {
        return (RatingBar) view;
    }

    /**
     * Gets the current view as a webview.
     *
     * @return WebView
     */
    @NotNull
    public WebView getWebView() {
        return (WebView) view;
    }

    /**
     * Gets the current view as a spinner.
     *
     * @return Spinner
     */
    @NotNull
    public Spinner getSpinner() {
        return (Spinner) view;
    }

    /**
     * Gets the editable.
     *
     * @return the editable
     */
    @Nullable
    public Editable getEditable() {

        Editable result = null;

        if (view instanceof EditText) {
            result = ((EditText) view).getEditableText();
        }

        return result;
    }

    /**
     * Gets the text of a TextView.
     *
     * @return the text
     */
    @Nullable
    public CharSequence getText() {

        CharSequence result = null;

        if (view instanceof TextView) {
            result = ((TextView) view).getText();
        }

        return result;
    }

    /**
     * Gets the selected item if current view is an adapter view.
     *
     * @return selected
     */
    @Nullable
    public Object getSelectedItem() {

        Object result = null;

        if (view instanceof AdapterView<?>) {
            result = ((AdapterView<?>) view).getSelectedItem();
        }

        return result;

    }


    /**
     * Gets the selected item position if current view is an adapter view.
     * <p/>
     * Returns AdapterView.INVALID_POSITION if not valid.
     *
     * @return selected position
     */
    public int getSelectedItemPosition() {

        int result = AdapterView.INVALID_POSITION;

        if (view instanceof AdapterView<?>) {
            result = ((AdapterView<?>) view).getSelectedItemPosition();
        }

        return result;

    }


    private static final Class<?>[] ON_CLICK_SIG = {View.class};

    /**
     * Register a callback method for when the view is clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    @NotNull
    public T clicked(OnClickListener listener) {

        if (view != null) {
            view.setOnClickListener(listener);
        }

        return self();
    }

    @NotNull
    public T check(RadioButton.OnCheckedChangeListener listener) {
        if (view != null) {
            if (view instanceof CompoundButton) {
                CompoundButton cb = (CompoundButton) view;
                cb.setOnCheckedChangeListener(listener);
            }
        }
        return self();
    }

    /**
     * Register a callback method for when the view is long clicked.
     *
     * @param listener The callback method.
     * @return self
     */
    @NotNull
    public T longClicked(OnLongClickListener listener) {

        if (view != null) {
            view.setOnLongClickListener(listener);
        }

        return self();
    }


    @NotNull
    private static Class<?>[] ON_ITEM_SIG = {AdapterView.class, View.class, int.class, long.class};

    /**
     * Register a callback method for when an item is clicked in the ListView. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
     *
     * @param handler The handler that has the public callback method.
     * @param method  The method name of the callback.
     * @return self
     */


    /**
     * Register a callback method for when an item is clicked in the ListView.
     *
     * @param listener The callback method.
     * @return self
     */
    @NotNull
    public T itemClicked(OnItemClickListener listener) {

        if (view instanceof AdapterView) {

            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemClickListener(listener);


        }

        return self();

    }

    /**
     * Register a callback method for when an item is long clicked in the ListView. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
     *
     * @param handler The handler that has the public callback method.
     * @param method  The method name of the callback.
     * @return self
     */


    /**
     * Register a callback method for when an item is long clicked in the ListView.
     *
     * @param listener The callback method.
     * @return self
     */
    @NotNull
    public T itemLongClicked(OnItemLongClickListener listener) {

        if (view instanceof AdapterView) {

            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemLongClickListener(listener);


        }

        return self();

    }

    /**
     * Register a callback method for when an item is selected. Method must have signature of method(AdapterView<?> parent, View v, int pos, long id).
     *
     * @param handler The handler that has the public callback method.
     * @param method  The method name of the callback.
     * @return self
     */


    /**
     * Register a callback method for when an item is selected.
     *
     * @param listener The item selected listener.
     * @return self
     */
    @NotNull
    public T itemSelected(OnItemSelectedListener listener) {

        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setOnItemSelectedListener(listener);
        }

        return self();

    }


    /**
     * Set selected item of an AdapterView.
     *
     * @param position The position of the item to be selected.
     * @return self
     */
    @NotNull
    public T setSelection(int position) {

        if (view instanceof AdapterView) {
            AdapterView<?> alv = (AdapterView<?>) view;
            alv.setSelection(position);
        }

        return self();

    }

    @NotNull
    private static Class<?>[] ON_SCROLLED_STATE_SIG = {AbsListView.class, int.class};


    private static final Class<?>[] TEXT_CHANGE_SIG = {CharSequence.class, int.class, int.class, int.class};

    /**
     * Register a callback method for when a textview text is changed. Method must have signature of method(CharSequence s, int start, int before, int count)).
     *
     * @param handler The handler that has the public callback method.
     * @param method  The method name of the callback.
     * @return self
     */

    @NotNull
    private static Class<?>[] PENDING_TRANSITION_SIG = {int.class, int.class};

    private static final Class<?>[] OVER_SCROLL_SIG = {int.class};

    @NotNull
    private static Class<?>[] LAYER_TYPE_SIG = {int.class, Paint.class};


    /**
     * Clear a view. Applies to ImageView, WebView, and TextView.
     *
     * @return self
     */
    @NotNull
    public T clear() {

        if (view != null) {

            if (view instanceof ImageView) {
                ImageView iv = ((ImageView) view);
                iv.setImageBitmap(null);
            } else if (view instanceof TextView) {
                TextView tv = ((TextView) view);
                tv.setText("");
            }


        }

        return self();
    }


    /**
     * Set the margin of a view. Notes all parameters are in DIP, not in pixel.
     *
     * @param leftDip   the left dip
     * @param topDip    the top dip
     * @param rightDip  the right dip
     * @param bottomDip the bottom dip
     * @return self
     */
    @NotNull
    public T margin(float leftDip, float topDip, float rightDip, float bottomDip) {

        if (view != null) {
            LayoutParams lp = view.getLayoutParams();
            if (lp instanceof MarginLayoutParams) {
                Context context = getContext();
                int left = DensityUtil.dip2px(context, leftDip);
                int top = DensityUtil.dip2px(context, topDip);
                int right = DensityUtil.dip2px(context, rightDip);
                int bottom = DensityUtil.dip2px(context, bottomDip);
                ((MarginLayoutParams) lp).setMargins(left, top, right, bottom);
                view.setLayoutParams(lp);
            }
        }
        return self();
    }

    /**
     * Set the width of a view in dip.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param dip width in dip
     * @return self
     */

    @NotNull
    public T width(int dip) {
        size(true, dip, true);
        return self();
    }

    /**
     * Set the height of a view in dip.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param dip height in dip
     * @return self
     */

    @NotNull
    public T height(int dip) {
        size(false, dip, true);
        return self();
    }

    /**
     * Set the width of a view in dip or pixel.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param width width
     * @param dip   dip or pixel
     * @return self
     */

    @NotNull
    public T width(int width, boolean dip) {
        size(true, width, dip);
        return self();
    }

    /**
     * Set the height of a view in dip or pixel.
     * Can also be ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT, or ViewGroup.LayoutParams.MATCH_PARENT.
     *
     * @param height height
     * @param dip    dip or pixel
     * @return self
     */

    @NotNull
    public T height(int height, boolean dip) {
        size(false, height, dip);
        return self();
    }

    private void size(boolean width, int n, boolean dip) {

        if (view != null) {

            LayoutParams lp = view.getLayoutParams();

            Context context = getContext();

            if (n > 0 && dip) {
                n = DensityUtil.dip2px(context, n);
            }

            if (width) {
                lp.width = n;
            } else {
                lp.height = n;
            }

            view.setLayoutParams(lp);

        }

    }


    /**
     * Return the context of activity or view.
     *
     * @return Context
     */

    @Nullable
    public Context getContext() {
        if (act != null) {
            return act;
        }
        if (root != null) {
            return root.getContext();
        }
        return context;
    }


    /**
     * Determines if a group item of an expandable list should delay loading a url resource.
     * <p/>
     * Designed to be used inside
     * getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent)  of an expandable list adapter.
     *
     * @param groupPosition the group position of the item
     * @param isExpanded    the group is expanded
     * @param convertView   the list item view
     * @param parent        the parent input of getView
     * @param url           the content url to be checked if cached and is available immediately
     * @return delay should delay loading a particular resource
     */


    /**
     * Determines if a child item of an expandable list item should delay loading a url resource.
     * <p/>
     * Designed to be used inside
     * getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) of an expandable list adapter.
     *
     * @param groupPosition the group position of the item
     * @param childPosition the child position of the item
     * @param isLastChild   the item is last child
     * @param convertView   the list item view
     * @param parent        the parent input of getView
     * @param url           the content url to be checked if cached and is available immediately
     * @return delay should delay loading a particular resource
     */


    /**
     * Determines if a list or gallery view item should delay loading a url resource because the view is scrolling very fast.
     * The primary purpose of this method is to skip loading remote resources (such as images) over the internet
     * until the list stop flinging and the user is focusing on the displaying items.
     * <p/>
     * If the scrolling stops and there are delayed items displaying, the getView method will be called again to force
     * the delayed items to be redrawn. During redraw, this method will always return false, thus allowing a particular
     * resource to be loaded.
     * <p/>
     * Designed to be used inside getView(int position, View convertView, ViewGroup parent) of an adapter.
     * <p/>
     * <p/>
     * <br>
     * <br>
     * Example usage:
     * <pre>
     * 		public View getView(int position, View convertView, ViewGroup parent) {
     *
     * 			...
     *
     * 			if(aq.shouldDelay(position, convertView, parent, tbUrl)){
     * 				aq.id(R.id.tb).image(placeholder);
     *            }else{
     * 				aq.id(R.id.tb).image(tbUrl, true, true, 0, 0, placeholder, 0, 0);
     *            }
     *
     * 			...
     *
     *        }
     * </pre>
     * <p/>
     * <br>
     * NOTE:
     * <p/>
     * <br>
     * This method uses the setOnScrollListener() method and will override any previously non-aquery assigned scroll listener.
     * If a scrolled listener is required, use the aquery method scrolled(OnScrollListener listener) to set the listener
     * instead of directly calling setOnScrollListener().
     *
     * @param position    the position of the item
     * @param convertView the list item view
     * @param parent      the parent input of getView
     * @param url         the content url to be checked if cached and is available immediately
     * @return delay should delay loading a particular resource
     */


    /**
     * Starts an animation on the view.
     * <p/>
     * <br>
     * contributed by: marcosbeirigo
     *
     * @param animId Id of the desired animation.
     * @return self
     */
    @NotNull
    public T animate(int animId) {
        return animate(animId, null);
    }

    /**
     * Starts an animation on the view.
     * <p/>
     * <br>
     * contributed by: marcosbeirigo
     *
     * @param animId   Id of the desired animation.
     * @param listener The listener to recieve notifications from the animation on its events.
     * @return self
     */
    @NotNull
    public T animate(int animId, AnimationListener listener) {
        Animation anim = AnimationUtils.loadAnimation(getContext(), animId);
        anim.setAnimationListener(listener);
        return animate(anim);
    }

    /**
     * Starts an animation on the view.
     * <p/>
     * <br>
     * contributed by: marcosbeirigo
     *
     * @param anim The desired animation.
     * @return self
     */
    @NotNull
    public T animate(@Nullable Animation anim) {
        if (view != null && anim != null) {
            view.startAnimation(anim);
        }
        return self();
    }

    /**
     * Trigger click event
     * <p/>
     * <br>
     * contributed by: neocoin
     *
     * @return self
     * @see View#performClick()
     */
    @NotNull
    public T click() {
        if (view != null) {
            view.performClick();
        }
        return self();
    }

    /**
     * Trigger long click event
     * <p/>
     * <br>
     * contributed by: neocoin
     *
     * @return self
     * @see View#performClick()
     */
    @NotNull
    public T longClick() {
        if (view != null) {
            view.performLongClick();
        }
        return self();
    }


    //weak hash map that holds the dialogs so they will never memory leaked
    @NotNull
    private static WeakHashMap<Dialog, Void> dialogs = new WeakHashMap<Dialog, Void>();

    /**
     * Show a dialog. Method dismiss() or dismissAll() should be called later.
     *
     * @return self
     */
    @NotNull
    public T show(@Nullable Dialog dialog) {

        try {
            if (dialog != null) {
                dialog.show();
                dialogs.put(dialog, null);
            }
        } catch (Exception e) {
        }

        return self();
    }

    /**
     * Dismiss a dialog previously shown with show().
     *
     * @return self
     */
    @NotNull
    public T dismiss(@Nullable Dialog dialog) {

        try {
            if (dialog != null) {
                dialogs.remove(dialog);
                dialog.dismiss();
            }
        } catch (Exception e) {
        }

        return self();
    }

    /**
     * Dismiss any AQuery dialogs.
     *
     * @return self
     */
    @NotNull
    public T dismiss() {

        Iterator<Dialog> keys = dialogs.keySet().iterator();

        while (keys.hasNext()) {

            Dialog d = keys.next();
            try {
                d.dismiss();
            } catch (Exception e) {
            }
            keys.remove();

        }
        return self();

    }

    @NotNull
    public T expand(int position, boolean expand) {

        if (view instanceof ExpandableListView) {

            ExpandableListView elv = (ExpandableListView) view;
            if (expand) {
                elv.expandGroup(position);
            } else {
                elv.collapseGroup(position);
            }
        }

        return self();
    }

    @NotNull
    public T expand(boolean expand) {

        if (view instanceof ExpandableListView) {

            ExpandableListView elv = (ExpandableListView) view;
            ExpandableListAdapter ela = elv.getExpandableListAdapter();

            if (ela != null) {

                int count = ela.getGroupCount();

                for (int i = 0; i < count; i++) {
                    if (expand) {
                        elv.expandGroup(i);
                    } else {
                        elv.collapseGroup(i);
                    }
                }

            }


        }

        return self();
    }
}
