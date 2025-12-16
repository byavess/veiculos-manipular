package org.veiculo.util;

public class PlacaUtil {

    /**
     * Mascara a placa, mantendo os 3 primeiros caracteres e ocultando o restante.
     * Exemplo: ABC1D23 -> ABC****
     */
    public static String mascararPlaca(String placa) {
        if (placa == null || placa.length() < 3) return "***";
        return placa.substring(0, 3) + "****";
    }


}
