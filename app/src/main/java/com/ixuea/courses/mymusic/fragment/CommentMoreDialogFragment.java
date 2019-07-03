package com.ixuea.courses.mymusic.fragment;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;

import com.ixuea.courses.mymusic.R;

/**
 * Created by kaka
 * On 2019/7/1
 */
public class CommentMoreDialogFragment extends DialogFragment {
    private DialogInterface.OnClickListener onClickListener;
    private int selectIndex;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setItems(R.array.comment_more,onClickListener);
        return builder.create();
    }
    public void show(FragmentManager fragmentManager, DialogInterface.OnClickListener onClickListener){
        this.selectIndex  = selectIndex;
        this.onClickListener = onClickListener;
        show(fragmentManager,"CommentMoreDialogFragment");
    }
}
