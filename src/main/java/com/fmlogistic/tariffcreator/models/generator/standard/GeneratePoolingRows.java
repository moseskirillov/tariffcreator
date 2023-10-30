package com.fmlogistic.tariffcreator.models.generator.standard;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class GeneratePoolingRows {
    private String fileName;
    private String description;
    private List<GeneratePoolingRow> rows;
}
