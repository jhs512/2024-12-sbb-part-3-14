package baekgwa.sbb.global.util;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;
import org.springframework.boot.Banner.Mode;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.servlet.support.RequestContextUtils;

@Component
public class ControllerUtils {

    private static final String BINDING_RESULT_PREFIX = "org.springframework.validation.BindingResult.";

    /**
     * FlashAttribute 로 전달된 Binding Result 를, model 에 다시 추가 해줍니다.
     * Redirect 시, Binding Result 는 모델에 자동 매핑되지 않음.
     * @param request
     * @param model
     * @param formName
     */
    public void restoreBindingResultFlashAttributesToModel(HttpServletRequest request, Model model, String formName) {
        String attributeName = BINDING_RESULT_PREFIX + formName;
        Map<String, ?> flashMap = RequestContextUtils.getInputFlashMap(request);
        if (flashMap != null && flashMap.containsKey(attributeName)) {
            model.addAttribute(attributeName, flashMap.get(attributeName));
        }
    }
}
