package com.panizio.agenda.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;

import com.panizio.agenda.exception.ValidacaoException;

import org.locationtech.jts.geom.Point;
import org.json.JSONException;
import org.json.JSONObject;

import io.github.cdimascio.dotenv.Dotenv;

public class ValidacaoUtils {

    public static class ApiKeyProvider {
        private static final Dotenv dotenv = Dotenv.load();

        public String getApiKey() {
            return dotenv.get("GOOGLE_MAPS_API_KEY");
        }
    }

    private static final ApiKeyProvider apiKeyProvider = new ApiKeyProvider();

    private static final Set<String> DDDS_VALIDOS = Set.of(
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "21", "22", "24", "27", "28",
            "31", "32", "33", "34", "35", "37", "38", "41", "42", "43", "44", "45", "46", "47",
            "48", "49", "51", "53", "54", "55", "61", "62", "63", "64", "65", "66", "67", "68",
            "69", "71", "73", "74", "75", "77", "79", "81", "82", "83", "84", "85", "86", "87",
            "88", "89", "91", "92", "93", "94", "95", "96", "97", "98", "99");

    private static final Set<String> DOMINIOS_INVALIDOS = Set.of(
            "yopmail.com", "mailinator.com", "tempmail.com", "10minutemail.com");

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final Pattern ENDERECO_PATTERN = Pattern.compile(".+,\\s*\\d+\\s*-\\s*.+");

    public static boolean validarCPF(String cpf) {
        String cleaned = limparNumeros(cpf);
        return isValid(cleaned, 11, ValidacaoUtils::validarDigitosCPF);
    }

    public static boolean validarCNPJ(String cnpj) {
        String cleaned = limparNumeros(cnpj);
        return isValid(cleaned, 14, ValidacaoUtils::validarDigitosCNPJ);
    }

    public static Point buscarCoordenadasPorEndereco(String enderecoCompleto) throws ValidacaoException {
        try {
            String apiKey = apiKeyProvider.getApiKey(); // Obtenha a chave da API
            String urlEncodedAddress = URLEncoder.encode(enderecoCompleto, StandardCharsets.UTF_8);
            URL url = new URL("https://maps.googleapis.com/maps/api/geocode/json?address=" + urlEncodedAddress + "&key="
                    + apiKey);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                JSONObject jsonResponse = new JSONObject(response.toString());
                if ("OK".equals(jsonResponse.getString("status"))) {
                    JSONObject location = jsonResponse.getJSONArray("results")
                            .getJSONObject(0)
                            .getJSONObject("geometry")
                            .getJSONObject("location");

                    double lat = location.getDouble("lat");
                    double lon = location.getDouble("lng");

                    GeometryFactory geometryFactory = new GeometryFactory();
                    return geometryFactory.createPoint(new Coordinate(lon, lat));
                } else {
                    throw new ValidacaoException(
                            Map.of("endereco", "Não foi possível obter coordenadas para o endereço"));
                }
            } else {
                throw new ValidacaoException(Map.of("endereco", "Erro ao conectar à API de geocodificação"));
            }
        } catch (IOException | JSONException e) {
            throw new ValidacaoException(Map.of("endereco", "Erro ao processar a resposta da API: " + e.getMessage()));
        }
    }

    public static boolean validarCEP(String cep) {
        return limparNumeros(cep).length() == 8;
    }

    public static boolean validarEmail(String email) {
        if (email == null)
            return false;
        if (!EMAIL_PATTERN.matcher(email).matches())
            return false;

        String dominio = email.substring(email.indexOf('@') + 1).toLowerCase();
        return !DOMINIOS_INVALIDOS.contains(dominio);
    }

    public static boolean validarDataNascimento(String data) {
        try {
            LocalDate dataNascimento = LocalDate.parse(data, DATE_FORMATTER);
            return !dataNascimento.isAfter(LocalDate.now());
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    public static boolean validarTelefone(String telefone) {
        String cleaned = limparNumeros(telefone);
        return isValidLength(cleaned, 11) &&
                DDDS_VALIDOS.contains(cleaned.substring(0, 2));
    }

    public static boolean validarEndereco(String endereco) {
        return endereco != null &&
                !endereco.isEmpty() &&
                ENDERECO_PATTERN.matcher(endereco).matches();
    }

    public static boolean validarNome(String nome) {
        return nome != null &&
                nome.trim().length() >= 3 &&
                nome.chars().allMatch(c -> Character.isLetter(c) || Character.isWhitespace(c));
    }

    public static boolean validarTexto(String texto) {
        return texto != null && !texto.trim().isEmpty();
    }

    private static boolean validarDigitosCPF(String cpf) {
        int digito1 = calcularDigito(cpf, 10, 9);
        int digito2 = calcularDigito(cpf, 11, 10);
        return cpf.charAt(9) - '0' == digito1 && cpf.charAt(10) - '0' == digito2;
    }

    private static boolean validarDigitosCNPJ(String cnpj) {
        int digito1 = calcularDigitoCNPJ(cnpj, 5);
        int digito2 = calcularDigitoCNPJ(cnpj, 6);
        return cnpj.charAt(12) - '0' == digito1 && cnpj.charAt(13) - '0' == digito2;
    }

    private static int calcularDigito(String numero, int pesoInicial, int length) {
        int soma = 0;
        for (int i = 0; i < length; i++) {
            soma += (numero.charAt(i) - '0') * (pesoInicial - i);
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static int calcularDigitoCNPJ(String cnpj, int pesoInicial) {
        int soma = 0;
        int[] pesos = (pesoInicial == 5) ? new int[] { 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 }
                : new int[] { 6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2 };
        int limite = (pesoInicial == 5) ? 12 : 13;

        for (int i = 0; i < limite; i++) {
            soma += (cnpj.charAt(i) - '0') * pesos[i];
        }
        int resto = soma % 11;
        return resto < 2 ? 0 : 11 - resto;
    }

    private static String limparNumeros(String input) {
        return input == null ? "" : input.replaceAll("\\D", "");
    }

    private static boolean isValid(String input, int expectedLength, ValidacaoDigitos validator) {
        if (input.length() != expectedLength || input.matches("(\\d)\\1+")) {
            return false;
        }
        return validator.validar(input);
    }

    private static boolean isValidLength(String input, int length) {
        return input != null && input.length() == length;
    }

    @FunctionalInterface
    private interface ValidacaoDigitos {
        boolean validar(String numero);
    }
}