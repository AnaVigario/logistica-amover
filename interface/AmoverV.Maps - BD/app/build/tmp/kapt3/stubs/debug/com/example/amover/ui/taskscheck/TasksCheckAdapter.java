package com.example.amover.ui.taskscheck;

import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.example.amover.R;
import com.example.amover.databinding.FragmentDetailBinding;
import com.example.amover.model.TaskModel;

@kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u00000\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\b\n\u0000\n\u0002\u0010\u0002\n\u0002\b\u0004\n\u0002\u0018\u0002\n\u0002\b\u0005\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001:\u0001\u0013B\u0013\u0012\f\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004\u00a2\u0006\u0002\u0010\u0006J\b\u0010\u0007\u001a\u00020\bH\u0016J\u0018\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\u00022\u0006\u0010\f\u001a\u00020\bH\u0016J\u0018\u0010\r\u001a\u00020\u00022\u0006\u0010\u000e\u001a\u00020\u000f2\u0006\u0010\u0010\u001a\u00020\bH\u0016J\u0014\u0010\u0011\u001a\u00020\n2\f\u0010\u0012\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004R\u0014\u0010\u0003\u001a\b\u0012\u0004\u0012\u00020\u00050\u0004X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0014"}, d2 = {"Lcom/example/amover/ui/taskscheck/TasksCheckAdapter;", "Landroidx/recyclerview/widget/RecyclerView$Adapter;", "Lcom/example/amover/ui/taskscheck/TasksCheckAdapter$TaskViewHolder;", "checklist", "", "Lcom/example/amover/model/TaskModel;", "(Ljava/util/List;)V", "getItemCount", "", "onBindViewHolder", "", "holder", "position", "onCreateViewHolder", "parent", "Landroid/view/ViewGroup;", "viewType", "updateList", "newlist", "TaskViewHolder", "app_debug"})
public final class TasksCheckAdapter extends androidx.recyclerview.widget.RecyclerView.Adapter<com.example.amover.ui.taskscheck.TasksCheckAdapter.TaskViewHolder> {
    @org.jetbrains.annotations.NotNull()
    private java.util.List<com.example.amover.model.TaskModel> checklist;
    
    public TasksCheckAdapter(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.amover.model.TaskModel> checklist) {
        super();
    }
    
    @java.lang.Override()
    @org.jetbrains.annotations.NotNull()
    public com.example.amover.ui.taskscheck.TasksCheckAdapter.TaskViewHolder onCreateViewHolder(@org.jetbrains.annotations.NotNull()
    android.view.ViewGroup parent, int viewType) {
        return null;
    }
    
    @java.lang.Override()
    public void onBindViewHolder(@org.jetbrains.annotations.NotNull()
    com.example.amover.ui.taskscheck.TasksCheckAdapter.TaskViewHolder holder, int position) {
    }
    
    @java.lang.Override()
    public int getItemCount() {
        return 0;
    }
    
    public final void updateList(@org.jetbrains.annotations.NotNull()
    java.util.List<com.example.amover.model.TaskModel> newlist) {
    }
    
    @kotlin.Metadata(mv = {1, 9, 0}, k = 1, xi = 48, d1 = {"\u0000&\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0004\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\u0018\u00002\u00020\u0001B\r\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\u0002\u0010\u0004J\u000e\u0010\n\u001a\u00020\u000b2\u0006\u0010\f\u001a\u00020\rR\u0019\u0010\u0005\u001a\n \u0007*\u0004\u0018\u00010\u00060\u0006\u00a2\u0006\b\n\u0000\u001a\u0004\b\b\u0010\t\u00a8\u0006\u000e"}, d2 = {"Lcom/example/amover/ui/taskscheck/TasksCheckAdapter$TaskViewHolder;", "Landroidx/recyclerview/widget/RecyclerView$ViewHolder;", "view", "Landroid/view/View;", "(Landroid/view/View;)V", "binding", "Lcom/example/amover/databinding/FragmentDetailBinding;", "kotlin.jvm.PlatformType", "getBinding", "()Lcom/example/amover/databinding/FragmentDetailBinding;", "bind", "", "taskcheck", "Lcom/example/amover/model/TaskModel;", "app_debug"})
    public static final class TaskViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        private final com.example.amover.databinding.FragmentDetailBinding binding = null;
        
        public TaskViewHolder(@org.jetbrains.annotations.NotNull()
        android.view.View view) {
            super(null);
        }
        
        public final com.example.amover.databinding.FragmentDetailBinding getBinding() {
            return null;
        }
        
        public final void bind(@org.jetbrains.annotations.NotNull()
        com.example.amover.model.TaskModel taskcheck) {
        }
    }
}