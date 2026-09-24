package br.com.raizurbana.api.util;

public class CpfValidator {

    private CpfValidator() {
    }

    public static boolean isValid(String cpf) {

        if (cpf == null) {
            return false;
        }

        String numeros = cpf.replaceAll("\\D", "");

        if (numeros.length() != 11) {
            return false;
        }

        if (todosDigitosIguais(numeros)) {
            return false;
        }

        try {
            int soma = 0;

            for (int i = 0; i < 9; i++) {
                int numero = Character.getNumericValue(
                        numeros.charAt(i)
                );

                soma += numero * (10 - i);
            }

            int resto = soma % 11;

            int primeiroDigito =
                    resto < 2 ? 0 : 11 - resto;

            if (primeiroDigito !=
                    Character.getNumericValue(
                            numeros.charAt(9)
                    )) {
                return false;
            }

            soma = 0;

            for (int i = 0; i < 10; i++) {
                int numero = Character.getNumericValue(
                        numeros.charAt(i)
                );

                soma += numero * (11 - i);
            }

            resto = soma % 11;

            int segundoDigito =
                    resto < 2 ? 0 : 11 - resto;

            return segundoDigito ==
                    Character.getNumericValue(
                            numeros.charAt(10)
                    );

        } catch (Exception e) {
            return false;
        }
    }

    private static boolean todosDigitosIguais(
            String cpf
    ) {
        char primeiro = cpf.charAt(0);

        for (int i = 1; i < cpf.length(); i++) {
            if (cpf.charAt(i) != primeiro) {
                return false;
            }
        }

        return true;
    }
}