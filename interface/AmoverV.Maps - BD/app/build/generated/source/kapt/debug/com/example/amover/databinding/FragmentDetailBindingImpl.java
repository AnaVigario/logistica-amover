package com.example.amover.databinding;
import com.example.amover.R;
import com.example.amover.BR;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.View;
@SuppressWarnings("unchecked")
public class FragmentDetailBindingImpl extends FragmentDetailBinding  {

    @Nullable
    private static final androidx.databinding.ViewDataBinding.IncludedLayouts sIncludes;
    @Nullable
    private static final android.util.SparseIntArray sViewsWithIds;
    static {
        sIncludes = null;
        sViewsWithIds = new android.util.SparseIntArray();
        sViewsWithIds.put(R.id.task_id_text, 1);
        sViewsWithIds.put(R.id.circle_task, 2);
        sViewsWithIds.put(R.id.arrow_image, 3);
        sViewsWithIds.put(R.id.task_address_text, 4);
        sViewsWithIds.put(R.id.linear_layout_task, 5);
        sViewsWithIds.put(R.id.task_type_text, 6);
        sViewsWithIds.put(R.id.rule_image, 7);
        sViewsWithIds.put(R.id.task_name_text, 8);
        sViewsWithIds.put(R.id.task_timewindow_text, 9);
        sViewsWithIds.put(R.id.task_note_text, 10);
        sViewsWithIds.put(R.id.task_time_text, 11);
        sViewsWithIds.put(R.id.linear_layout_latitude, 12);
        sViewsWithIds.put(R.id.task_latitude_text, 13);
        sViewsWithIds.put(R.id.task_longitude_text, 14);
    }
    // views
    @NonNull
    private final android.widget.RelativeLayout mboundView0;
    // variables
    // values
    // listeners
    // Inverse Binding Event Handlers

    public FragmentDetailBindingImpl(@Nullable androidx.databinding.DataBindingComponent bindingComponent, @NonNull View root) {
        this(bindingComponent, root, mapBindings(bindingComponent, root, 15, sIncludes, sViewsWithIds));
    }
    private FragmentDetailBindingImpl(androidx.databinding.DataBindingComponent bindingComponent, View root, Object[] bindings) {
        super(bindingComponent, root, 0
            , (android.widget.ImageView) bindings[3]
            , (android.widget.ImageView) bindings[2]
            , (android.widget.LinearLayout) bindings[12]
            , (android.widget.LinearLayout) bindings[5]
            , (android.widget.ImageView) bindings[7]
            , (android.widget.TextView) bindings[4]
            , (android.widget.TextView) bindings[1]
            , (android.widget.TextView) bindings[13]
            , (android.widget.TextView) bindings[14]
            , (android.widget.TextView) bindings[8]
            , (android.widget.TextView) bindings[10]
            , (android.widget.TextView) bindings[11]
            , (android.widget.TextView) bindings[9]
            , (android.widget.TextView) bindings[6]
            );
        this.mboundView0 = (android.widget.RelativeLayout) bindings[0];
        this.mboundView0.setTag(null);
        setRootTag(root);
        // listeners
        invalidateAll();
    }

    @Override
    public void invalidateAll() {
        synchronized(this) {
                mDirtyFlags = 0x1L;
        }
        requestRebind();
    }

    @Override
    public boolean hasPendingBindings() {
        synchronized(this) {
            if (mDirtyFlags != 0) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean setVariable(int variableId, @Nullable Object variable)  {
        boolean variableSet = true;
            return variableSet;
    }

    @Override
    protected boolean onFieldChange(int localFieldId, Object object, int fieldId) {
        switch (localFieldId) {
        }
        return false;
    }

    @Override
    protected void executeBindings() {
        long dirtyFlags = 0;
        synchronized(this) {
            dirtyFlags = mDirtyFlags;
            mDirtyFlags = 0;
        }
        // batch finished
    }
    // Listener Stub Implementations
    // callback impls
    // dirty flag
    private  long mDirtyFlags = 0xffffffffffffffffL;
    /* flag mapping
        flag 0 (0x1L): null
    flag mapping end*/
    //end
}