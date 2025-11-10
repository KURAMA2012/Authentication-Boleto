import { Injectable } from '@angular/core';
import { BSResource } from './BSResource.service';

@Injectable()
export class UsuarioResource {

  constructor(private resoruce: BSResource) {
    resoruce.setPath('');
  }

  consultars(pageable) {
    return this.resoruce.get('usuarios/consultar?size=' + pageable.size + '&page=' + pageable.page);
  }

  consultar(requestPage) {
    return this.resoruce.post('usuarios/consultar', requestPage);
  }

  atualizar(data) {
    return this.resoruce.put('usuarios/' + data.id + '/atualizar', data);
  }

  inserir(data) {
    return this.resoruce.post('usuarios/inserir', data);
  }

  detalhar(data) {
    return this.resoruce.get('usuarios/detalhar/' + data.id);
  }

  deletar(id) {
    return this.resoruce.delete('usuarios/deletar/' + id.toString());
  }

  login(data) {
    return this.resoruce.post('usuarios/login/', data);
  }
}

@Injectable()
export class ClienteResource {

  constructor(private resoruce: BSResource) {
    resoruce.setPath('');
  }

  consultars(pageable) {
    return this.resoruce.get('clientes/consultar?size=' + pageable.size + '&page=' + pageable.page);
  }

  consultar(requestPage) {
    return this.resoruce.post('clientes/consultar', requestPage);
  }

  listar() {
    return this.resoruce.get('clientes/listar');
  }

  inserir(data) {
    return this.resoruce.post('clientes/inserir', data);
  }

  atualizar(data) {
    return this.resoruce.put('clientes/' + data.id + '/atualizar', data);
  }

  deletar(id) {
    return this.resoruce.delete('clientes/deletar/' + id.toString());
  }

   getClienteByCpfCnpj(cpfCnpj) {
    return this.resoruce.get('clientes/get-cliente-by-cpfCnpj?cpfCnpj=' + cpfCnpj);
  }

  getInfoPlanoByLogin(login) {
    return this.resoruce.get('clientes/get-info-plano-by-login?login=' + login);
  }
}

@Injectable()
export class ChamadoResource {

  constructor(private resoruce: BSResource) {
    resoruce.setPath('');
  }

  consultars(pageable) {
    return this.resoruce.get('chamados/consultar?size=' + pageable.size + '&page=' + pageable.page);
  }

  consultar(requestPage) {
    return this.resoruce.post('chamados/consultar', requestPage);
  }

  listar() {
    return this.resoruce.get('chamados/listar');
  }

  inserir(data) {
    return this.resoruce.post('chamados/inserir', data);
  }

  atualizar(data) {
    return this.resoruce.put('chamados/' + data.id + '/atualizar', data);
  }

  deletar(id) {
    return this.resoruce.delete('chamados/deletar/' + id.toString());
  }

  getResumoChamadosByLogin(login) {
    return this.resoruce.get('chamados/get-resumo-chamados-by-login?login=' + login);
  }

  getChamadosByLogin(login) {
    return this.resoruce.get('chamados/get-chamados-by-login?login=' + login);
  }

  getMensagensChamadoByIdChamado(idChamado) {
    return this.resoruce.get('chamados/get-mensagens-chamado-by-idChamado?idChamado=' + idChamado);
  }

  gravarChamado(data) {
    return this.resoruce.post('chamados/gravar-chamado', data);
  }

  gravarMensagem(idChamado,mensagem) {
    return this.resoruce.get('chamados/gravar-mensagem?idChamado=' + idChamado + '&mensagem=' + mensagem);
  }
}

@Injectable()
export class TituloResource {

  constructor(private resoruce: BSResource) {
    resoruce.setPath('');
  }

  consultars(pageable) {
    return this.resoruce.get('titulos/consultar?size=' + pageable.size + '&page=' + pageable.page);
  }

  consultar(requestPage) {
    return this.resoruce.post('titulos/consultar', requestPage);
  }

  listar() {
    return this.resoruce.get('titulos/listar');
  }

  inserir(data) {
    return this.resoruce.post('titulos/inserir', data);
  }

  atualizar(data) {
    return this.resoruce.put('titulos/' + data.id + '/atualizar', data);
  }

  deletar(id) {
    return this.resoruce.delete('titulos/deletar/' + id.toString());
  }

  getUlimaFaturaByLogin(login) {
    return this.resoruce.get('titulos/get-ultima-fatura-by-login?login=' + login);
  }

  listByLogin(login) {
    return this.resoruce.get('titulos/listByLogin?login=' + login);
  }

  downloadBoleto(idFatura) {
    return this.resoruce.get('titulos/download-boleto/' + idFatura );
  }
}