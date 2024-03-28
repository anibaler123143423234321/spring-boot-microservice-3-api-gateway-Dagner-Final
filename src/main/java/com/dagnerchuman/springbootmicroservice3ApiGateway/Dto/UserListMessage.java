package com.dagnerchuman.springbootmicroservice3ApiGateway.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserListMessage {
    private List<String> userList;
}
