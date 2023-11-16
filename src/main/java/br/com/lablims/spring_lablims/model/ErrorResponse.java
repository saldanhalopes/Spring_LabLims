package br.com.lablims.spring_lablims.model;

import java.util.List;

import br.com.lablims.spring_lablims.util.FieldError;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class ErrorResponse {

    private Integer httpStatus;
    private String exception;
    private String message;
    private List<FieldError> fieldErrors;

}
