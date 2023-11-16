package br.com.lablims.spring_lablims.util;

public class HtmlRegisterContent {

    public static String html(String nomeUsuario, String urlAtivacao,  String senha) {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Novo Usuário - Seu Site</title>\n" +
                "    <script src=\"https://cdn.rawgit.com/davidshimjs/qrcodejs/gh-pages/qrcode.min.js\"></script>\n" +
                "</head>\n" +
                "<body>\n" +
                "\n" +
                "    <div style=\"max-width: 600px; margin: 0 auto; padding: 20px; font-family: Arial, sans-serif;\">\n" +
                "\n" +
                "        <h2>Olá " + nomeUsuario + ",</h2>\n" +
                "\n" +
                "        <p>Seja bem-vindo ao nosso site! Estamos animados para tê-lo(a) como nosso novo membro.</p>\n" +
                "\n" +
                "        <p>Para completar o processo de registro, clique no link abaixo:</p>\n" +
                "\n" +
                "        <a href=\"" + urlAtivacao + "\" style=\"display: inline-block; padding: 10px 20px; background-color: #3498db; color: #ffffff; text-decoration: none; border-radius: 5px; margin-top: 15px;\">Ativar Conta</a>\n" +
                "\n" +
                "        <p>Sua senha: " + senha + "</p>\n" +
                "\n" +
                "        <p>Se você encontrar algum problema ou tiver alguma dúvida, não hesite em nos contatar.</p>\n" +
                "\n" +
                "        <p>Obrigado!</p>\n" +
                "\n" +
                "        <p>Atenciosamente,<br>Equipe Lablims</p>\n" +
                "\n" +
                "    </div>\n" +
                "\n" +
                "</body>\n" +
                "</html>";
    }

}
