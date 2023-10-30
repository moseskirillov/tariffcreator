package com.fmlogistic.tariffcreator.models.minio;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RejectRequest {
    private String email;
    private String file;
    private String kind;
    private String reason;
}
