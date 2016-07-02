package in.medhajnews.app.ui.radialactionmenu;

public class ExpectedFontIconDataTypeException extends IllegalArgumentException {

    public ExpectedFontIconDataTypeException(RadialActionMenuAction action) {
        super(String.format("Expected LongPressAction to be instance of IconFontLongPressIcon, received %s", action.getClass().getSimpleName()));
    }
}
