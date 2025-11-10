package com.A3.projeto.Faculdade.AuthenticationBoleto.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.A3.projeto.Faculdade.AuthenticationBoleto.entity.BoletoLog;
import com.A3.projeto.Faculdade.AuthenticationBoleto.repository.BoletoLogRepository;

@RestController
@RequestMapping("/api/logs")
public class BoletoLogController {

    @Autowired
    private BoletoLogRepository boletoLogRepository;

    @GetMapping
    public List<BoletoLog> listarTodos() {
        return boletoLogRepository.findAll();
    }
}