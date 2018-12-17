package brewin.mark.motionalarm;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AlarmAdapter extends ArrayAdapter<String> {
    private ArrayList<String> alarms;

    public AlarmAdapter(Context context, int textViewResourceId, ArrayList<String> alarms) {
        super(context, textViewResourceId, alarms);

        this.alarms = alarms;
    }

    public View getView(int pos, View convertView, ViewGroup parent){
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.list_alarm, null);
        }

        String i = alarms.get(pos);

        if(i != null){
            TextView name = v.findViewById(R.id.alarmName);

            if(name != null) {
                name.setText(i);
            }
        }

        return v;
    }
}
