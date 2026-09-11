package com.codelens.codelens_backend.dto;

import com.codelens.codelens_backend.model.FindingSeverity;
import com.codelens.codelens_backend.model.FindingType;

public record FindingResponse (

    String id,
    FindingType type,
    FindingSeverity severity,
    String title,
    String description,
    String recommendation,
    Integer lineNumber
){
}
