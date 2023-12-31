package com.mdnhs.graduateassist.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.mdnhs.graduateassist.R;
import com.mdnhs.graduateassist.util.Events;
import com.mdnhs.graduateassist.util.GetPath;
import com.mdnhs.graduateassist.util.GlobalBus;
import com.mdnhs.graduateassist.util.Method;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ProImage extends BottomSheetDialogFragment {

    private Method method;
    private int REQUEST_CODE_CHOOSE = 100;
    private int REQUEST_CODE_PERMISSION = 101;
    private ConstraintLayout conRemove, conImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.pro_image, container, false);

        method = new Method(getActivity());
        if (method.isRtl()) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        }

        conRemove = view.findViewById(R.id.con_remove_proImage);
        conImage = view.findViewById(R.id.con_image_proImage);

        conRemove.setOnClickListener(v -> {
            Events.ProImage proImage = new Events.ProImage("", "", false, true);
            GlobalBus.getBus().post(proImage);
            dismiss();
        });

        conImage.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSION);
            } else {
                selectImage();
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            try {
                String filePath;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    filePath = method.getTempUploadPath(data.getData());
                } else {
                    filePath = GetPath.getPath(getActivity(), data.getData());
                }
                if (filePath != null) {
                    dismiss();
                    Events.ProImage proImage = new Events.ProImage("", filePath, true, false);
                    GlobalBus.getBus().post(proImage);
                } else {
                    method.alertBox(getResources().getString(R.string.upload_folder_error));
                }
            } catch (Exception e) {
                method.alertBox(getResources().getString(R.string.upload_folder_error));
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CODE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                selectImage(); // perform action when allow permission success
            } else {
                method.alertBox(getResources().getString(R.string.storage_permission));
            }
        }
    }

    private void selectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_CODE_CHOOSE);
    }
}