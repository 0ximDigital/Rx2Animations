package oxim.digital.rxanimations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.CompletableEmitter;
import io.reactivex.CompletableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 2000;

    @Bind(R.id.sample_view)
    View sampleView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.root_view)
    public void animateSampleView() {
        final Completable logJob = Completable.create(new LogCompletable());

        Log.i("MAIN", "Waiting");

        logJob.subscribeOn(Schedulers.computation())
              .observeOn(AndroidSchedulers.mainThread())
              .subscribe(() -> Log.i("MAIN", "Done"), Throwable::printStackTrace);
    }

    private static final class LogCompletable implements CompletableOnSubscribe {

        @Override
        public void subscribe(final CompletableEmitter emitter) throws Exception {
            if (!emitter.isDisposed()) {
                Log.i("CLAZZ", "Emitter is disposed");
                return;
            }

            Log.i("CLAZZ", "I am done");
            emitter.onComplete();
        }
    }
}
