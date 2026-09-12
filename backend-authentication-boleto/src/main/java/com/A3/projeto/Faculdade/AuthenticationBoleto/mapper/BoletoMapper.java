package com.A3.projeto.Faculdade.AuthenticationBoleto.mapper;


import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.BoletoRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Boleto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BoletoMapper extends GenericMapper<Boleto, BoletoRequest> {


    @Override
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "empresa", ignore = true)
    @Mapping(target = "status", expression = "java(StatusBoleto.GERADO)")
    @Mapping(target = "dataGeracao", expression = "java(LocalDateTime.now())")
    @Mapping(target = "autenticado", constant = "false")
    @Mapping(target = "codigoAutenticacao", expression = "java(UUID.randomUUID().toString())")
    @Mapping(target = "tokenVerificacao", expression = "java(UUID.randomUUID().toString())")
    Boleto toEntity(BoletoRequest request);
}
