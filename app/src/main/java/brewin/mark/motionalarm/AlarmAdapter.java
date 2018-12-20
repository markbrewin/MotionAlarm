package brewin.mark.motionalarm;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class AlarmAdapter extends RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder> {
    private static final String TAG = "AlarmAdapter";

    private Context context;

    class AlarmViewHolder extends RecyclerView.ViewHolder {
        private final TextView alarmName;
        private final TextView alarmTime;
        private final TextView noAlarms;
        private final ImageView deleteAlarm;

        private AlarmViewHolder(View itemView) {
            super(itemView);

            alarmName = itemView.findViewById(R.id.txtAlarmName);
            alarmTime = itemView.findViewById(R.id.txtAlarmTime);
            noAlarms = itemView.findViewById(R.id.txtNoAlarms);
            deleteAlarm = itemView.findViewById(R.id.btnDeleteAlarm);
        }
    }

    private final LayoutInflater mInflator;
    private List<Alarm> mAlarms;

    AlarmAdapter(Context context) {
        this.context = context;

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
            final Alarm current = mAlarms.get(pos);
            holder.alarmName.setText(current.getName());
            holder.alarmTime.setText(current.getStrHour() + ":" + current.getStrMin());
            holder.deleteAlarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteAlarm(current.getId());
                }
            });
            holder.noAlarms.setVisibility(View.GONE);
        }
    }

    void deleteAlarm(int id) {
        Intent intent = new Intent("DeleteAlarm");
        intent.putExtra("id", id);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
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
