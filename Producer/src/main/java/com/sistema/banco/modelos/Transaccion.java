package com.sistema.banco.modelos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaccion {
    public String idTransaccion;
    public double monto;
    public String moneda;
    public String cuentaOrigen;
    public String bancoDestino;
    public Detalle detalle; 
    public String carnet; 
    public String nombre;
    public String correo;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Detalle {
        public String nombreBeneficiario;
        public String tipoTransferencia;
        public String descripcion;
        public Referencias referencias; 
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Referencias {
        public String factura;
        public String codigoInterno;
    }
}