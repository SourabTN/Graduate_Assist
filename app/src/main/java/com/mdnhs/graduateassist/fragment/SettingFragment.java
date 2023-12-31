package com.mdnhs.graduateassist.fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.activity.AboutUs;
import com.mdnhs.graduateassist.activity.ContactUs;
import com.mdnhs.graduateassist.activity.Faq;
import com.mdnhs.graduateassist.activity.MainActivity;
import com.mdnhs.graduateassist.activity.PrivacyPolicy;
import com.mdnhs.graduateassist.activity.SplashScreen;
import com.mdnhs.graduateassist.activity.TermsConditions;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;


public class SettingFragment extends Fragment {

    private Method method;
    private String themMode;

    @SuppressLint("NonConstantResourceId")
    @Nullable
    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.setting_fragment, container, false);

        if (MainActivity.toolbar != null) {
            MainActivity.toolbar.setTitle(getResources().getString(R.string.setting));
        }

        method = new Method(getActivity());

        SwitchMaterial switchMaterial = view.findViewById(R.id.switch_setting);
        MaterialTextView textViewContactUs = view.findViewById(R.id.textView_contactUs_setting);
        MaterialTextView textViewFaq = view.findViewById(R.id.textView_faq_setting);
        MaterialTextView textViewTerms = view.findViewById(R.id.textView_terms_setting);
        MaterialTextView textViewShareApp = view.findViewById(R.id.textView_shareApp_setting);
        MaterialTextView textViewPrivacyPolicy = view.findViewById(R.id.textView_privacy_policy_setting);
        MaterialTextView textViewAboutUs = view.findViewById(R.id.textView_aboutUs_setting);
        final MaterialTextView textViewThemeType = view.findViewById(R.id.textView_themType_setting);
        ConstraintLayout conTheme = view.findViewById(R.id.con_them_setting);
        ImageView imageView = view.findViewById(R.id.imageView_them_setting);

        if (method.isDarkMode()) {
            Glide.with(getActivity().getApplicationContext()).load(R.drawable.ic_mode_dark)
                    .placeholder(R.drawable.placeholder_gallery)
                    .into(imageView);
        } else {
            Glide.with(getActivity().getApplicationContext()).load(R.drawable.ic_mode_icon)
                    .placeholder(R.drawable.placeholder_gallery)
                    .into(imageView);
        }

        switchMaterial.setChecked(method.pref.getBoolean(method.notification, true));

        switchMaterial.setOnCheckedChangeListener((buttonView, isChecked) -> {
            OneSignal.unsubscribeWhenNotificationsAreDisabled(isChecked);
            method.editor.putBoolean(method.notification, isChecked);
            method.editor.commit();
        });

        switch (method.themMode()) {
            case "system":
                textViewThemeType.setText(getResources().getString(R.string.system_default));
                break;
            case "light":
                textViewThemeType.setText(getResources().getString(R.string.light));
                break;
            case "dark":
                textViewThemeType.setText(getResources().getString(R.string.dark));
                break;
            default:
                break;
        }

        textViewFaq.setOnClickListener(v -> startActivity(new Intent(getActivity(), Faq.class)));

        textViewTerms.setOnClickListener(v -> startActivity(new Intent(getActivity(), TermsConditions.class)));

        textViewShareApp.setOnClickListener(v -> shareApp());

        textViewAboutUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), AboutUs.class)));

        textViewPrivacyPolicy.setOnClickListener(v -> startActivity(new Intent(getActivity(), PrivacyPolicy.class)));

        textViewContactUs.setOnClickListener(v -> startActivity(new Intent(getActivity(), ContactUs.class)));

        conTheme.setOnClickListener(v -> {

            final Dialog dialog = new Dialog(requireActivity());
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialogbox_theme);
            if (method.isRtl()) {
                dialog.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
            dialog.getWindow().setLayout(ViewPager.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.WRAP_CONTENT);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

            RadioGroup radioGroup = dialog.findViewById(R.id.radioGroup_theme);
            MaterialTextView textViewOk = dialog.findViewById(R.id.textView_ok_them);
            MaterialTextView textViewCancel = dialog.findViewById(R.id.textView_cancel_theme);

            switch (method.themMode()) {
                case "system":
                    radioGroup.check(radioGroup.getChildAt(0).getId());
                    break;
                case "light":
                    radioGroup.check(radioGroup.getChildAt(1).getId());
                    break;
                case "dark":
                    radioGroup.check(radioGroup.getChildAt(2).getId());
                    break;
                default:
                    break;
            }

            radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
                MaterialRadioButton rb = group.findViewById(checkedId);
                if (null != rb && checkedId > -1) {
                    switch (checkedId) {
                        case R.id.radioButton_system_theme:
                            themMode = "system";
                            break;
                        case R.id.radioButton_light_theme:
                            themMode = "light";
                            break;
                        case R.id.radioButton_dark_theme:
                            themMode = "dark";
                            break;
                        default:
                            break;
                    }
                }
            });

            textViewOk.setOnClickListener(v1 -> {
                method.editor.putString(method.themSetting, themMode);
                method.editor.commit();
                dialog.dismiss();

                startActivity(new Intent(getActivity(), SplashScreen.class));
                getActivity().finishAffinity();

            });

            textViewCancel.setOnClickListener(v12 -> dialog.dismiss());

            dialog.show();

        });

        return view;

    }

    private void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getActivity().getApplication().getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName())));
        }
    }

    private void moreApp() {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.play_more_app))));
    }

    private void shareApp() {

        try {

            String string = getResources().getString(R.string.Let_me_recommend_you_this_application) + "\n\n" + "https://play.google.com/store/apps/details?id=" + getActivity().getApplication().getPackageName();

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
            intent.putExtra(Intent.EXTRA_TEXT, string);
            startActivity(Intent.createChooser(intent, getResources().getString(R.string.choose_one)));

        } catch (Exception e) {
            Log.d("app_data", "");
        }

    }

}
