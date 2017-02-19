package oxim.digital.rxanimations;

import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import java.util.concurrent.TimeUnit;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Completable;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;
import oxim.digital.rx2anim.RxValueAnimator;
import oxim.digital.rx2anim.RxValueObservable;

import static oxim.digital.rx2anim.RxAnimations.hideViewGroupChildren;

public class MainActivity extends AppCompatActivity {

    private static final int ANIMATION_DURATION = 2000;
    private static final int CANCEL_POINT = 1000;

    @Bind(R.id.container)
    ViewGroup container;

    @Bind(R.id.top_view)
    View topView;

    @Bind(R.id.bottom_view)
    View bottomView;

    private Disposable aniamtionDisposable = Disposables.disposed();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        hideViewGroupChildren(container).subscribe();
    }

    @OnClick(R.id.root_view)
    public void animateSampleView() {
        final ValueAnimator valueAnimator = ValueAnimator.ofFloat(0.f, 1.f);
        valueAnimator.setDuration(1000)
                     .setInterpolator(new LinearInterpolator());

        final Disposable disposable = RxValueObservable.from(valueAnimator)
                .subscribe(value -> topView.setAlpha((float)value),
                           Throwable::printStackTrace,
                           () -> Log.w("ANIM", "DONE"));

        Completable.timer(500, TimeUnit.MILLISECONDS)
                   .subscribe(disposable::dispose);
    }
}
