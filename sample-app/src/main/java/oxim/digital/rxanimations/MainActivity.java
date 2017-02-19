package oxim.digital.rxanimations;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.disposables.Disposable;
import io.reactivex.disposables.Disposables;

import static oxim.digital.rx2anim.RxAnimations.fadeOut;
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
        fadeOut(topView, 1000, 500)
                .subscribe();
    }
}
