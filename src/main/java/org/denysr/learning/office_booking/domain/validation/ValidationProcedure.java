package org.denysr.learning.office_booking.domain.validation;

@FunctionalInterface
public interface ValidationProcedure {
    void process() throws RuntimeException;
}
