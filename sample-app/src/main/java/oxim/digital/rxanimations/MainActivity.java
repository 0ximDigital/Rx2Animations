package oxim.digital.rxanimations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import oxim.digital.rx2anim.RxAnimationBuilder;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 2000;
    private static final int CANCEL_POINT = 1000;

    @Bind(R.id.top_view)
    View topView;

    @Bind(R.id.bottom_view)
    View bottomView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.root_view)
    public void animateSampleView() {
        final Disposable animationDisposable = RxAnimationBuilder.animate(topView)
                                                                 .duration(ANIMATION_DURATION)
                                                                 .fadeIn()
                                                                 .onAnimationCancel(view -> view.setAlpha(1.f))
                                                                 .schedule()
                                                                 .subscribe(() -> Log.i("ANIM", "Done"));

        Completable.timer(CANCEL_POINT, TimeUnit.MILLISECONDS)
                   .observeOn(AndroidSchedulers.mainThread())
                   .subscribe(animationDisposable::dispose);
    }
}
