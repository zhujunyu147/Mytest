package com.honeywell.printer.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.honeywell.printer.R;
import com.honeywell.printer.adapter.ConfigListAdapter;
import com.honeywell.printer.util.FileUtil;

import java.util.List;

/**
 * Created by zhujunyu on 16/9/20.
 */
public class CustomDialog extends Dialog {
    public CustomDialog(Context context) {
        super(context);
    }

    public CustomDialog(Context context, int theme) {
        super(context, theme);
    }

    public static class Builder {
        private Context context;
        private String title;
        private String message;
        private String orignalData;
        private String positiveButtonText;
        private String negativeButtonText;
        private View contentView;
        private DialogInterface.OnClickListener positiveButtonClickListener;
        private DialogInterface.OnClickListener negativeButtonClickListener;

        private DialogPostiveInterface positiveStringCallback;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }


        public interface DialogPostiveInterface {
            void callBack(DialogInterface dialog, String name);
        }

        /**
         * Set the Dialog message from resource
         *
         * @param
         * @return
         */
        public Builder setMessage(int message) {
            this.message = (String) context.getText(message);
            return this;
        }

        public Builder setOrignalData(String num) {
            this.orignalData = num;
            return this;
        }

        /**
         * Set the Dialog title from resource
         *
         * @param title
         * @return
         */
        public Builder setTitle(int title) {
            this.title = (String) context.getText(title);
            return this;
        }

        /**
         * Set the Dialog title from String
         *
         * @param title
         * @return
         */

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }


        /**
         * Set the positive button resource and it's listener
         *
         * @param positiveButtonText
         * @return
         */
        public Builder setPositiveButton(int positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = (String) context
                    .getText(positiveButtonText);
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveButtonClickListener = listener;
            return this;
        }

        public Builder setPositiveButton(String positiveButtonText,
                                         DialogPostiveInterface listener) {
            this.positiveButtonText = positiveButtonText;
            this.positiveStringCallback = listener;
            return this;
        }

        public Builder setNegativeButton(int negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = (String) context
                    .getText(negativeButtonText);
            this.negativeButtonClickListener = listener;
            return this;
        }

        public Builder setNegativeButton(String negativeButtonText,
                                         DialogInterface.OnClickListener listener) {
            this.negativeButtonText = negativeButtonText;
            this.negativeButtonClickListener = listener;
            return this;
        }

        public CustomDialog createDefaultDialog(){
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_select_model, null);
            TextView titleText = (TextView) layout.findViewById(R.id.title);
            TextView messageText = (TextView) layout.findViewById(R.id.message);
            Button positiveBtn = (Button) layout.findViewById(R.id.positiveButton);
            Button negativeBtn = (Button) layout.findViewById(R.id.negativeButton);
            titleText.setText(this.title);
            messageText.setText(this.message);
            positiveBtn.setText(this.positiveButtonText);
            negativeBtn.setText(this.negativeButtonText);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if (positiveButtonClickListener != null) {
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positiveButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            if (negativeButtonClickListener != null) {
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            return dialog;
        }

        public CustomDialog createSelectModeDialog() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_select_model, null);
            TextView titleText = (TextView) layout.findViewById(R.id.title);
            TextView messageText = (TextView) layout.findViewById(R.id.message);
            Button positiveBtn = (Button) layout.findViewById(R.id.positiveButton);
            Button negativeBtn = (Button) layout.findViewById(R.id.negativeButton);
            titleText.setText(this.title);
            messageText.setText(this.message);
            positiveBtn.setText(this.positiveButtonText);
            negativeBtn.setText(this.negativeButtonText);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if (positiveButtonClickListener != null) {
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        positiveButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_POSITIVE);
                    }
                });
            }
            if (negativeButtonClickListener != null) {
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            return dialog;
        }

        public CustomDialog createSaveConfigDialog() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_save_config, null);
            TextView titleText = (TextView) layout.findViewById(R.id.title);
            TextView messageText = (TextView) layout.findViewById(R.id.message);
            Button positiveBtn = (Button) layout.findViewById(R.id.positiveButton);
            Button negativeBtn = (Button) layout.findViewById(R.id.negativeButton);
            titleText.setText(this.title);
            messageText.setText(this.message);
            positiveBtn.setText(this.positiveButtonText);
            negativeBtn.setText(this.negativeButtonText);
            final EditText editText = (EditText) layout.findViewById(R.id.et_modify_config);
            editText.setText("" + orignalData);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            if (positiveStringCallback != null) {
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (!TextUtils.isEmpty(editText.getText())) {
                            positiveStringCallback.callBack(dialog, editText.getText().toString());
                        }
                    }
                });
            }
            if (negativeButtonClickListener != null) {
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            return dialog;
        }


        public CustomDialog createSelectConfigDialog() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_select_config, null);
            TextView titleText = (TextView) layout.findViewById(R.id.title);
            TextView messageText = (TextView) layout.findViewById(R.id.message);
            Button positiveBtn = (Button) layout.findViewById(R.id.positiveButton);
            Button negativeBtn = (Button) layout.findViewById(R.id.negativeButton);
            ListView listView = (ListView) layout.findViewById(R.id.lv_select_config);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            titleText.setText(this.title);
            messageText.setText(this.message);
            positiveBtn.setText(this.positiveButtonText);
            negativeBtn.setText(this.negativeButtonText);

            List<String> stringList = FileUtil.getFileNameListFromDir(context);

            final ConfigListAdapter configListAdapter = new ConfigListAdapter(context, stringList, R.layout.activity_select_config_item);
            listView.setAdapter(configListAdapter);
            if (positiveStringCallback != null) {
                positiveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String name = configListAdapter.selectName;
                        Log.e("选中的文件名称", name+"");
                        if(TextUtils.isEmpty(name)){
                            Toast.makeText(context,"请选择文件",Toast.LENGTH_SHORT).show();
                        }else {
                            positiveStringCallback.callBack(dialog, name);
                        }

                    }
                });
            }
            if (negativeButtonClickListener != null) {
                negativeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        negativeButtonClickListener.onClick(dialog,
                                DialogInterface.BUTTON_NEGATIVE);
                    }
                });
            }
            return dialog;
        }


        public CustomDialog create() {
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            // instantiate the dialog with the custom Theme
            final CustomDialog dialog = new CustomDialog(context, R.style.Dialog);
            View layout = inflater.inflate(R.layout.dialog_normal_layout, null);
            dialog.addContentView(layout, new LayoutParams(
                    LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
            // set the dialog title
            ((TextView) layout.findViewById(R.id.title)).setText(title);
            final EditText editText = (EditText) layout.findViewById(R.id.et_modify_config);
            editText.setText(orignalData + "");
            if (orignalData != null)
                editText.setSelection(orignalData.length());
            // set the confirm button
            if (positiveButtonText != null) {
                ((Button) layout.findViewById(R.id.positiveButton))
                        .setText(positiveButtonText);
                if (positiveButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.positiveButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {

                                    if (!TextUtils.isEmpty(editText.getText())) {
                                        positiveButtonClickListener.onClick(dialog,
                                                Integer.parseInt(editText.getText().toString()));
                                    } else {
                                        Toast.makeText(context, "请输入参数", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.positiveButton).setVisibility(
                        View.GONE);
            }
            // set the cancel button
            if (negativeButtonText != null) {
                ((Button) layout.findViewById(R.id.negativeButton))
                        .setText(negativeButtonText);
                if (negativeButtonClickListener != null) {
                    ((Button) layout.findViewById(R.id.negativeButton))
                            .setOnClickListener(new View.OnClickListener() {
                                public void onClick(View v) {
                                    negativeButtonClickListener.onClick(dialog,
                                            DialogInterface.BUTTON_NEGATIVE);
                                }
                            });
                }
            } else {
                // if no confirm button just set the visibility to GONE
                layout.findViewById(R.id.negativeButton).setVisibility(
                        View.GONE);
            }
            // set the content message
            if (message != null) {
                ((TextView) layout.findViewById(R.id.message)).setText(message);
            } else if (contentView != null) {
                // if no message set
                // add the contentView to the dialog body
                ((LinearLayout) layout.findViewById(R.id.content))
                        .removeAllViews();
                ((LinearLayout) layout.findViewById(R.id.content))
                        .addView(contentView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
            }
            dialog.setContentView(layout);
            return dialog;
        }
    }
}
