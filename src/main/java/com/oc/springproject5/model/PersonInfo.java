package com.oc.springproject5.model;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonInfo {
    private String firstName;
    private String lastName;
    private String address;
    private String phone;
}
