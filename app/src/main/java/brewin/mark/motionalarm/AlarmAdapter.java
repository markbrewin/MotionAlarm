package brewin.mark.motionalarm;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private static final String TAG = "AlarmAdapter";

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView alarmName;
        private final TextView alarmTime;
        private final TextView noAlarms;

        private AlarmViewHolder(View itemView) {
            super(itemView);

            alarmName = itemView.findViewById(R.id.txtAlarmName);
            alarmTime = itemView.findViewById(R.id.txtAlarmTime);
            noAlarms = itemView.findViewById(R.id.txtNoAlarms);
        }
    }

    private final LayoutInflater mInflator;
    private List<Alarm> mAlarms;

    AlarmAdapter(Context context) {
        mInflator = LayoutInflater.from(context);
    }

    @Override
    public AlarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflator.inflate(R.layout.list_alarm, parent, false);
        return new AlarmViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AlarmViewHolder holder, int pos) {
        if(mAlarms != null) {
            Alarm current = mAlarms.get(pos);
            holder.alarmName.setText(current.getName());
            holder.alarmTime.setText(current.getStrHour() + ":" + current.getStrMin());
        } else {
            holder.noAlarms.setVisibility(View.VISIBLE);
        }
    }

    void setAlarms(List<Alarm> alarms) {
        mAlarms = alarms;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(mAlarms != null) {
            return mAlarms.size();
        } else {
            return 0;
        }
    }
}
