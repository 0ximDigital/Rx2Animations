package oxim.digital.rx2anim;

import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.support.v4.view.ViewPropertyAnimatorListenerAdapter;
import android.view.View;

import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Consumer;

public class AnimationDisposable extends MainThreadDisposable {

    private final ViewPropertyAnimatorCompat animator;
    private final Consumer<View> animationCancelAction;

    public AnimationDisposable(final ViewPropertyAnimatorCompat animator, final Consumer<View> animationCancelAction) {
        this.animator = animator;
        this.animationCancelAction = animationCancelAction;
    }

    @Override
    protected void onDispose() {
        animator.setListener(new ViewPropertyAnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(final View view) {
                try {
                    animationCancelAction.accept(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        animator.cancel();
        animator.setListener(null);
    }
}
