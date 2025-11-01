package com.example.demo.mapper;

import com.example.demo.dto.CreateAccountRequest;
import com.example.demo.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountMapper {
    
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "balance", source = "initialBalance")
    @Mapping(target = "status", expression = "java(com.example.demo.entity.Account.AccountStatus.ACTIVE)")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toAccount(CreateAccountRequest request);
}

