package com.example.apiserver.domain.ledger.mapper;

import com.example.apiserver.domain.ledger.dto.LedgerResponseDto;
import com.example.apiserver.domain.ledger.entity.Ledger;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LedgerMapper {

    @Mapping(target = "userId", source = "user.id")
    LedgerResponseDto toDto(Ledger ledger);
}
