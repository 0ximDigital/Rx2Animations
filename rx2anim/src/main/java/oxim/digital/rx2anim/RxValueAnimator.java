package oxim.digital.rx2anim;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;

import io.reactivex.Completable;
import io.reactivex.CompletableObserver;
import io.reactivex.android.MainThreadDisposable;
import io.reactivex.functions.Consumer;

import static io.reactivex.android.MainThreadDisposable.verifyMainThread;

public final class RxValueAnimator extends Completable {

    private static final float FULLY_ANIMATED = 1.0f;

    private final ValueAnimator valueAnimator;
    private final Consumer<ValueAnimator> valueUpdateAction;
    private final Consumer<ValueAnimator> animationCancelAction;

    public static RxValueAnimator from(final ValueAnimator valueAnimator, final Consumer<ValueAnimator> valueUpdateAction) {
        return from(valueAnimator, valueUpdateAction, aValueAnimator -> {});
    }

    public static RxValueAnimator from(final ValueAnimator valueAnimator, final Consumer<ValueAnimator> valueUpdateAction,
                                       final Consumer<ValueAnimator> animationCancelAction) {
        return new RxValueAnimator(valueAnimator, valueUpdateAction, animationCancelAction);
    }

    private RxValueAnimator(final ValueAnimator valueAnimator, final Consumer<ValueAnimator> valueUpdateAction,
                            final Consumer<ValueAnimator> animationCancelAction) {
        this.valueAnimator = valueAnimator;
        this.valueUpdateAction = valueUpdateAction;
        this.animationCancelAction = animationCancelAction;
    }

    @Override
    protected void subscribeActual(final CompletableObserver completableObserver) {
        verifyMainThread();
        final UpdateListener updateListener = new UpdateListener(completableObserver, valueUpdateAction);
        final AnimationEndListener endListener = new AnimationEndListener(completableObserver, valueAnimator, animationCancelAction);
        final ValueAnimatorDisposable animatorDisposable = new ValueAnimatorDisposable(valueAnimator, updateListener, endListener);
        completableObserver.onSubscribe(animatorDisposable);

        valueAnimator.addUpdateListener(updateListener);
        valueAnimator.addListener(endListener);
        valueAnimator.start();
    }

    private static final class ValueAnimatorDisposable extends MainThreadDisposable {

        private final ValueAnimator animator;

        private final UpdateListener updateListener;
        public final AnimationEndListener animationEndListener;

        public ValueAnimatorDisposable(final ValueAnimator animator, final UpdateListener updateListener,
                                       final AnimationEndListener animationEndListener) {
            this.animator = animator;
            this.updateListener = updateListener;
            this.animationEndListener = animationEndListener;
        }

        @Override
        protected void onDispose() {
            if (animator.getAnimatedFraction() != FULLY_ANIMATED) {
                animator.cancel();
            } else {
                animator.end();
            }
            animator.removeUpdateListener(updateListener);
            animator.removeListener(animationEndListener);
        }
    }

    private static final class UpdateListener implements AnimatorUpdateListener {

        private final CompletableObserver observer;
        private final Consumer<ValueAnimator> valueUpdateAction;

        public UpdateListener(final CompletableObserver observer, final Consumer<ValueAnimator> valueUpdateAction) {
            this.observer = observer;
            this.valueUpdateAction = valueUpdateAction;
        }

        @Override
        public void onAnimationUpdate(final ValueAnimator animator) {
            try {
                valueUpdateAction.accept(animator);
            } catch (Exception e) {
                observer.onError(e);
            }
        }
    }

    private static final class AnimationEndListener extends AnimatorListenerAdapter {

        private final CompletableObserver observer;
        private final ValueAnimator animator;
        private final Consumer<ValueAnimator> animationCancelAction;

        public AnimationEndListener(final CompletableObserver observer, final ValueAnimator animator,
                                    final Consumer<ValueAnimator> animationCancelAction) {
            this.observer = observer;
            this.animator = animator;
            this.animationCancelAction = animationCancelAction;
        }

        @Override
        public void onAnimationCancel(final Animator animation) {
            try {
                animationCancelAction.accept(animator);
                dispose();
            } catch (Exception e) {
                animator.removeListener(this);
                observer.onError(e);
            }
        }

        @Override
        public void onAnimationEnd(final Animator animation) {
            dispose();
        }

        private void dispose() {
            animator.removeListener(this);
            observer.onComplete();
        }
    }
}

