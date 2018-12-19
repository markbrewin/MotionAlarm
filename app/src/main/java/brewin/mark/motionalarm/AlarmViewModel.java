package brewin.mark.motionalarm;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.ArrayList;

public class AlarmViewModel extends AndroidViewModel {
    private AlarmRepository mRepository;
    private LiveData<ArrayList<Alarm>> mAllAlarms;

    public AlarmViewModel(Application application) {
        super(application);
        mRepository = new AlarmRepository(application);
        mAllAlarms = mRepository.getAllAlarms();
    }

    LiveData<ArrayList<Alarm>> getAllAlarms() {
        return mAllAlarms;
    }

    public void insert(Alarm alarm) {
        mRepository.insert(alarm);
    }
}
