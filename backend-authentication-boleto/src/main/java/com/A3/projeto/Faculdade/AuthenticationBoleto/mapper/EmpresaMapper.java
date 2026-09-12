package com.A3.projeto.Faculdade.AuthenticationBoleto.mapper;

import com.A3.projeto.Faculdade.AuthenticationBoleto.dto.EmpresaRequest;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Empresa;
import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.Usuario;
import com.A3.projeto.Faculdade.AuthenticationBoleto.service.EmpresaService;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EmpresaMapper extends GenericMapper<Empresa, EmpresaRequest> {

}
