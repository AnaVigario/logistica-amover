// Generated by view binder compiler. Do not edit!
package com.example.amover.databinding;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewbinding.ViewBinding;
import androidx.viewbinding.ViewBindings;
import com.example.amover.R;
import java.lang.NullPointerException;
import java.lang.Override;
import java.lang.String;

public final class FragmentHomeBinding implements ViewBinding {
  @NonNull
  private final RelativeLayout rootView;

  @NonNull
  public final AppCompatButton buttondelivery;

  @NonNull
  public final AppCompatButton buttondeliverycheck;

  @NonNull
  public final AppCompatButton buttonlogout;

  @NonNull
  public final AppCompatButton buttonperfil;

  private FragmentHomeBinding(@NonNull RelativeLayout rootView,
      @NonNull AppCompatButton buttondelivery, @NonNull AppCompatButton buttondeliverycheck,
      @NonNull AppCompatButton buttonlogout, @NonNull AppCompatButton buttonperfil) {
    this.rootView = rootView;
    this.buttondelivery = buttondelivery;
    this.buttondeliverycheck = buttondeliverycheck;
    this.buttonlogout = buttonlogout;
    this.buttonperfil = buttonperfil;
  }

  @Override
  @NonNull
  public RelativeLayout getRoot() {
    return rootView;
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater) {
    return inflate(inflater, null, false);
  }

  @NonNull
  public static FragmentHomeBinding inflate(@NonNull LayoutInflater inflater,
      @Nullable ViewGroup parent, boolean attachToParent) {
    View root = inflater.inflate(R.layout.fragment_home, parent, false);
    if (attachToParent) {
      parent.addView(root);
    }
    return bind(root);
  }

  @NonNull
  public static FragmentHomeBinding bind(@NonNull View rootView) {
    // The body of this method is generated in a way you would not otherwise write.
    // This is done to optimize the compiled bytecode for size and performance.
    int id;
    missingId: {
      id = R.id.buttondelivery;
      AppCompatButton buttondelivery = ViewBindings.findChildViewById(rootView, id);
      if (buttondelivery == null) {
        break missingId;
      }

      id = R.id.buttondeliverycheck;
      AppCompatButton buttondeliverycheck = ViewBindings.findChildViewById(rootView, id);
      if (buttondeliverycheck == null) {
        break missingId;
      }

      id = R.id.buttonlogout;
      AppCompatButton buttonlogout = ViewBindings.findChildViewById(rootView, id);
      if (buttonlogout == null) {
        break missingId;
      }

      id = R.id.buttonperfil;
      AppCompatButton buttonperfil = ViewBindings.findChildViewById(rootView, id);
      if (buttonperfil == null) {
        break missingId;
      }

      return new FragmentHomeBinding((RelativeLayout) rootView, buttondelivery, buttondeliverycheck,
          buttonlogout, buttonperfil);
    }
    String missingId = rootView.getResources().getResourceName(id);
    throw new NullPointerException("Missing required view with ID: ".concat(missingId));
  }
}
