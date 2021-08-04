package com.vincent.web.validator;

import com.vincent.model.OrderCommand;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

@Slf4j
public class OrderConstraintValidator implements ConstraintValidator<OrderValidation, OrderCommand> {


    @Override
    public void initialize(OrderValidation constraintAnnotation) {
        log.info("order command validation initialize.");
    }

    @Override
    public boolean isValid(OrderCommand orderCommand, ConstraintValidatorContext constraintValidatorContext) {
        log.info("checking order command.");
        return false;
    }
}
