//package com.dialmyca.onezed;
//import android.app.Dialog;
//import android.content.Context;
//import android.os.Bundle;
//import android.view.View;
//import android.view.Window;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//
//import com.google.android.material.button.MaterialButton;
//
//public class CustomAlertDialog extends Dialog {
//
//    private TextView content;
//    private ImageView titleText;
//
//    private MaterialButton positiveButton, negativeButton;
//    private ClickListener positiveClickListener, negativeClickListener;
//    private String title, msg;
//    String positiveText = "";
//    String negativeText = "";
//
//
//  /*  public enum posText{
//
//        GO,PROCEED,OK,YES,CONTINUE
//    }
//
//    public enum negText{
//
//        BACK,NO,CANCEL,DISMISS
//    }*/
//
//  /*  public posText PosText;
//    public negText Negtext;*/
//
//
//    public CustomAlertDialog(@NonNull Context context, int themeResId, String title, String msg) {
//        super(context, themeResId);
//        this.title = title;
//        this.msg = msg;
//
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setCancelable(false);
//        Window window = getWindow();
//        window.setBackgroundDrawableResource(android.R.color.transparent);
//        setContentView(R.layout.custom_dialog);
//        titleText = findViewById(R.id.title);
//        content = findViewById(R.id.content);
//        positiveButton = findViewById(R.id.positive);
//        negativeButton = findViewById(R.id.negetive);
//        positiveButton.setOnClickListener(v -> {
//            dismiss();
//            positiveClickListener.onClick(v);
//        });
//        negativeButton.setOnClickListener(v -> {
//            dismiss();
//            negativeClickListener.onClick(v);
//        });
//        content.setText(msg);
//        if (!positiveText.equals("")) {
//            positiveButton.setText(positiveText);
//        }
//        if (!negativeText.equals("")) {
//
//            negativeButton.setText(negativeText);
//        }
//
//     /*   positiveButton.setText(PosText.toString());
//        negativeButton.setText(Negtext.toString());*/
//
//
//    }
//
//    public void PositiveBtnText(String text) {
//        positiveText=text;
//    }
//
//    public void NegativeBtnText(String  text) {
//        negativeText=text;
//
//    }
//
//    public void onPositiveButton(ClickListener clickListener) {
//        this.positiveClickListener = clickListener;
//    }
//
//    public void onNegativeButton(ClickListener clickListener) {
//        negativeButton.setVisibility(View.VISIBLE);
//        this.negativeClickListener = clickListener;
//    }
//
//    public interface ClickListener extends View.OnClickListener {
//        @Override
//        void onClick(View view);
//    }
//}


package com.onezed.GlobalVariable;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.button.MaterialButton;
import com.onezed.R;
public class CustomAlertDialog extends Dialog {

    private TextView content;
    private ImageView titleText;

    private MaterialButton positiveButton, negativeButton;
    private ClickListener positiveClickListener, negativeClickListener;
    private String title, msg;
    String positiveText = "";
    String negativeText = "";


  /*  public enum posText{

        GO,PROCEED,OK,YES,CONTINUE
    }

    public enum negText{

        BACK,NO,CANCEL,DISMISS
    }*/

  /*  public posText PosText;
    public negText Negtext;*/


    public CustomAlertDialog(@NonNull Context context, int themeResId, String title, String msg) {
        super(context, themeResId);
        this.title = title;
        this.msg = msg;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setCancelable(false);
        Window window = getWindow();
        window.setBackgroundDrawableResource(android.R.color.transparent);
        setContentView(R.layout.custom_dialog);
        titleText = findViewById(R.id.title);
        content = findViewById(R.id.content);
        positiveButton = findViewById(R.id.positive);
        negativeButton = findViewById(R.id.negetive);
        positiveButton.setOnClickListener(v -> {
            dismiss();
            positiveClickListener.onClick(v);
        });
        negativeButton.setOnClickListener(v -> {
            dismiss();
            negativeClickListener.onClick(v);
        });
        content.setText(msg);
        if (!positiveText.equals("")) {
            positiveButton.setText(positiveText);
        }
        if (!negativeText.equals("")) {

            negativeButton.setText(negativeText);
        }

     /*   positiveButton.setText(PosText.toString());
        negativeButton.setText(Negtext.toString());*/


    }

    public void PositiveBtnText(String text) {
        positiveText=text;
    }

    public void NegativeBtnText(String  text) {
        negativeText=text;

    }

    public void onPositiveButton(ClickListener clickListener) {
        this.positiveClickListener = clickListener;
    }

    public void onNegativeButton(ClickListener clickListener) {
        negativeButton.setVisibility(View.VISIBLE);
        this.negativeClickListener = clickListener;
    }

    public interface ClickListener extends View.OnClickListener {
        @Override
        void onClick(View view);
    }
}