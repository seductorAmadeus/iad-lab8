package validation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

@FacesValidator("validation.InputValidator")
public class InputValidator implements Validator {

    private static final String Y_PATTERN = "^([-]?[0-9]+(([,.])[0-9]+)?)$";
    private Pattern pattern;

    public InputValidator() {
        pattern = Pattern.compile(Y_PATTERN);
    }

    @Override
    public void validate(FacesContext context, UIComponent component,
                         Object value) throws ValidatorException {

        Matcher matcher = pattern.matcher(value.toString());
        if (!matcher.matches()) {
            FacesMessage msg =
                    new FacesMessage("Ошибка валидации 'y'",
                            "Некорректный формат 'y'");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg); // TODO: обработка запятых на точки при добавлении в БД! Или здесь подправить.
        } else if ((Double.valueOf(value.toString().replace(",", ".")) < -5) ||
                (Double.valueOf(value.toString().replace(",", ".")) > 3)) {
            FacesMessage msg =
                    new FacesMessage("Ошибка валидации 'y'",
                            "'y' должно принадлежать промежутку [-5;3]");
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(msg);
        }
    }
}