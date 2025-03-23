package com.warba.assessment.exception.suppliers;

import com.warba.assessment.exception.ResourceNotFoundException;

import java.util.function.Supplier;

public class ResourceNotFoundSupplier {

    private ResourceNotFoundSupplier() {
    }

    public static Supplier<ResourceNotFoundException> entityNotFoundSupplier(String message) {
        return () -> new ResourceNotFoundException(message);
    }
}
