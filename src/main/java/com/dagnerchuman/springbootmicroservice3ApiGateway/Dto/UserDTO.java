package com.dagnerchuman.springbootmicroservice3ApiGateway.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDTO {
    private Long id;
    private String nombre;
    private String apellido;
}
