package fiveplay.dangchienhsgs.com.xosokienthiet.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import fiveplay.dangchienhsgs.com.xosokienthiet.R;

/**
 * Created by dangchienhsgs on 05/11/2014.
 */
public class TwoColumnArrayAdapter extends ArrayAdapter<String> {
    private final String TAG = "Two Array Adapter";

    private List<String> listFirstColumn;
    private List<String> listSecondColumn;

    public TwoColumnArrayAdapter(Context context, int resource, int textViewResourceId, List<String> listFirstColumn, List<String> listSecondColumn) {
        super(context, resource, textViewResourceId, listFirstColumn);
        this.listFirstColumn = listFirstColumn;
        this.listSecondColumn = listSecondColumn;

        Log.d(TAG, listFirstColumn.toString());
        Log.d(TAG, listSecondColumn.toString());
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        super.getView(position, convertView, parent);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.row_two_columns, parent, false);
        }

        TextView textFirstColumn = (TextView) convertView.findViewById(R.id.text_first_column);
        TextView textSecondColumn = (TextView) convertView.findViewById(R.id.text_second_column);

        textFirstColumn.setText(listFirstColumn.get(position));
        textSecondColumn.setText(listSecondColumn.get(position));

        Log.d(TAG, listFirstColumn.get(position));

        return convertView;
    }

    public List<String> getListFirstColumn() {
        return listFirstColumn;
    }

    public void setListFirstColumn(List<String> listFirstColumn) {
        this.listFirstColumn = listFirstColumn;
    }

    public List<String> getListSecondColumn() {
        return listSecondColumn;
    }

    public void setListSecondColumn(List<String> listSecondColumn) {
        this.listSecondColumn = listSecondColumn;
    }

    @Override
    public int getCount() {
        return listFirstColumn.size();
    }
}
