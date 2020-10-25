package org.denysr.learning.office_booking.domain.validation;

/**
 * Class to wrap all possible validations during Value object/entity initialization
 * Should wrap only domain validation exceptions, which can be show to the customer
 */
public class ValidatorWrapper {
    public static void wrapValidators(ValidationProcedure... procedures) {
        for (ValidationProcedure procedure: procedures) {
            try {
                procedure.process();
            } catch (RuntimeException e) {
                throw new IllegalValueException(e.getMessage());
            }
        }
    }
}
