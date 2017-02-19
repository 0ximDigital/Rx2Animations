package oxim.digital.rx2anim;

import android.support.annotation.Nullable;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorCompat;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

public final class AnimateCompletable extends Completable {

    private static final int NONE = 0;

    private final WeakReference<View> viewWeakRef;

    private final List<Consumer<ViewPropertyAnimatorCompat>> preTransformActions;
    private final List<Consumer<ViewPropertyAnimatorCompat>> animationActions;

    private final Consumer<View> onAnimationCancelAction;

    public static AnimateCompletable forView(final WeakReference<View> viewWeakRef,
                                             @Nullable final List<Consumer<ViewPropertyAnimatorCompat>> preTransformActions,
                                             final List<Consumer<ViewPropertyAnimatorCompat>> animationActions,
                                             final Consumer<View> onAnimationCancelAction) {
        return new AnimateCompletable(viewWeakRef, preTransformActions, animationActions, onAnimationCancelAction);
    }

    private AnimateCompletable(final WeakReference<View> viewWeakRef,
                               @Nullable final List<Consumer<ViewPropertyAnimatorCompat>> preTransformActions,
                               final List<Consumer<ViewPropertyAnimatorCompat>> animationActions,
                               final Consumer<View> onAnimationCancelAction) {
        this.viewWeakRef = viewWeakRef;
        this.preTransformActions = preTransformActions;
        this.animationActions = animationActions;
        this.onAnimationCancelAction = onAnimationCancelAction;
    }

    @Override
    protected void subscribeActual(final CompletableObserver completableObserver) {
        verifyMainThread();
        final View view = viewWeakRef.get();
        if (view == null) {
            completableObserver.onComplete();
            return;
        }

        final ViewPropertyAnimatorCompat animator = ViewCompat.animate(view);

        completableObserver.onSubscribe(createAnimationDisposable(animator, onAnimationCancelAction));

        if (preTransformActions != null) {
            applyActions(preTransformActions, animator);
            animator.setDuration(NONE).setStartDelay(NONE)
                    .withEndAction(() -> runAnimation(completableObserver, animator))
                    .start();
        } else {
            runAnimation(completableObserver, animator);
        }
    }

    private void applyActions(final List<Consumer<ViewPropertyAnimatorCompat>> actions, final ViewPropertyAnimatorCompat animator) {
        for (final Consumer<ViewPropertyAnimatorCompat> action : actions) {
            try {
                action.accept(animator);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void runAnimation(final CompletableObserver completableObserver, final ViewPropertyAnimatorCompat animator) {
        applyActions(animationActions, animator);
        animator.withEndAction(completableObserver::onComplete)
                .start();
    }

    private Disposable createAnimationDisposable(final ViewPropertyAnimatorCompat animator,
                                                 final Consumer<View> animationCancelAction) {
        return new AnimationDisposable(animator, animationCancelAction);
    }
}
