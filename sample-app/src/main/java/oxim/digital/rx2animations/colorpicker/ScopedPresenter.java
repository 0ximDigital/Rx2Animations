package oxim.digital.rx2animations.colorpicker;

public interface ScopedPresenter<T extends BaseView> {

    void bind(T view);

    void unbind();
}
