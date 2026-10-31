/** Remove máscara (pontos, barras, traços) e devolve só os dígitos de CPF/CNPJ. */
export function somenteDigitos(valor: string | null | undefined): string {
  return (valor ?? '').replace(/\D/g, '');
}
