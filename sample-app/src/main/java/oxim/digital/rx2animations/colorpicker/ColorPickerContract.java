package oxim.digital.rx2animations.colorpicker;


import io.reactivex.Completable;

public interface ColorPickerContract {

    interface Presenter extends ScopedPresenter<ColorPickerContract.View> {

        void refreshView();
    }

    interface View extends BaseView {

        Completable setupInitialAnimation();

        Completable startInitialAnimation();
    }
}
