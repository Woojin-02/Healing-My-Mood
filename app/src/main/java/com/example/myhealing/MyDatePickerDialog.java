package com.example.myhealing;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class MyDatePickerDialog extends DialogFragment {

    private static final int MAX_YEAR = 2099;
    private static final int MIN_YEAR = 1980;

    private DatePickerDialog.OnDateSetListener listener;
    public Calendar cal = Calendar.getInstance();
    private String updateDate;  // 추가: 업데이트할 날짜를 저장하는 변수

    public void setListener(DatePickerDialog.OnDateSetListener listener) {
        this.listener = listener;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;  // 추가: 업데이트할 날짜 설정
    }

    Button btnConfirm;
    Button btnCancel;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View dialog = inflater.inflate(R.layout.date_picker, null);

        btnConfirm = dialog.findViewById(R.id.btn_confirm);
        btnCancel = dialog.findViewById(R.id.btn_cancel);

        final NumberPicker dayPicker = dialog.findViewById(R.id.picker_day);
        final NumberPicker monthPicker = dialog.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialog.findViewById(R.id.picker_year);

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MyDatePickerDialog.this.getDialog().cancel();
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int year = yearPicker.getValue();
                int month = monthPicker.getValue();
                int day = dayPicker.getValue();

                listener.onDateSet(null, year, month, day);
                MyDatePickerDialog.this.getDialog().cancel();
            }
        });

        // 년, 월, 일 NumberPicker 설정
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setValue(cal.get(Calendar.MONTH) + 1);

        int year = cal.get(Calendar.YEAR);
        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setValue(year);

        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(cal.get(Calendar.DAY_OF_MONTH));

        // 일(NumberPicker)의 형식을 두 자릿수로 표시하도록 포맷팅
        dayPicker.setFormatter(new NumberPicker.Formatter() {
            @Override
            public String format(int value) {
                return String.format("%02d", value);
            }
        });

        if (updateDate != null) {
            // updateDate가 설정되어 있다면 해당 날짜를 파싱하여 설정
            String[] dateParts = updateDate.split("-");
            if (dateParts.length == 3) {
                int parsedYear = Integer.parseInt(dateParts[0]);
                int parsedMonth = Integer.parseInt(dateParts[1]);
                int parsedDay = Integer.parseInt(dateParts[2]);

                yearPicker.setValue(parsedYear);
                monthPicker.setValue(parsedMonth);
                dayPicker.setValue(parsedDay);
            }
        }

        builder.setView(dialog);

        return builder.create();
    }
}


