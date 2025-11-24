package io.github.prjkmo112.commonmysqldb.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserRoleEnum {
    @JsonProperty("admin") ADMIN,
    @JsonProperty("user") USER,
    @JsonProperty("visitor") VISITOR,
}
