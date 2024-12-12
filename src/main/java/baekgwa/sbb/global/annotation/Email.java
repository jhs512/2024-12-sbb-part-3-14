package baekgwa.sbb.global.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Constraint(validatedBy = { })
@Retention(RetentionPolicy.RUNTIME)
@Size(min = 3, max = 25)
@NotEmpty(message = "이메일은 필수항목입니다.")
@jakarta.validation.constraints.Email
public @interface Email {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
