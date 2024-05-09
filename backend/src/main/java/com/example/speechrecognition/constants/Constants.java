package com.example.speechrecognition.constants;

public class Constants {

    public static String EMAIL_VERIFICATION_HTML = "<!DOCTYPE html>\n" +
        "<html lang=\"en\">\n" +
        "<head>\n" +
        "  <meta charset=\"UTF-8\">\n" +
        "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "  <title>Email Verified Successfully</title>\n" +
        "  <style>\n" +
        "    body {\n" +
        "      font-family: Arial, sans-serif;\n" +
        "      background-color: #f4f4f4;\n" +
        "      margin: 0;\n" +
        "      padding: 0;\n" +
        "      display: flex;\n" +
        "      justify-content: center;\n" +
        "      align-items: center;\n" +
        "      height: 100vh;\n" +
        "      text-align: center;\n" +
        "    }\n" +
        "\n" +
        "    .container {\n" +
        "      background-color: #fff;\n" +
        "      padding: 20px;\n" +
        "      border-radius: 8px;\n" +
        "      box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);\n" +
        "    }\n" +
        "\n" +
        "    .message {\n" +
        "      color: #333;\n" +
        "      margin-bottom: 20px;\n" +
        "    }\n" +
        "\n" +
        "    .info {\n" +
        "      color: #666;\n" +
        "    }\n" +
        "  </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "  <div class=\"container\">\n" +
        "    <h1 class=\"message\">Email Verified Successfully</h1>\n" +
        "    <p class=\"info\">Your email has been successfully verified. You can now <a href=\"http://armenianspeechrecognition.s3-website-us-east-1.amazonaws.com/login\">login here</a> to access all features of our platform.</p>\n" +
        "  </div>\n" +
        "</body>\n" +
        "</html>\n";

    public static String EMAIL_SUBSCRIPTION = "<!DOCTYPE html>\n" +
        "<html lang=\"en\">\n" +
        "<head>\n" +
        "    <meta charset=\"UTF-8\">\n" +
        "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
        "    <title>You are now a subscriber!</title>\n" +
        "    <style>\n" +
        "        body {\n" +
        "            font-family: Helvetica, Arial, sans-serif;\n" +
        "            font-size: 16px;\n" +
        "            margin: 0;\n" +
        "            color: #0b0c0c;\n" +
        "        }\n" +
        "        .container {\n" +
        "            max-width: 580px;\n" +
        "            margin: 0 auto;\n" +
        "        }\n" +
        "        .header {\n" +
        "            background-color: #0b0c0c;\n" +
        "            padding: 20px;\n" +
        "            text-align: center;\n" +
        "            color: #ffffff;\n" +
        "        }\n" +
        "        .content {\n" +
        "            padding: 20px;\n" +
        "            background-color: #f9f9f9;\n" +
        "            border-left: 10px solid #b1b4b6;\n" +
        "            margin-top: 20px;\n" +
        "        }\n" +
        "        a {\n" +
        "            color: #1D70B8;\n" +
        "            text-decoration: none;\n" +
        "        }\n" +
        "        .cta-button {\n" +
        "             background-color: white;\n" +
        "             color: #000000;\n" +
        "             padding: 8px 20px;\n" +
        "             border: 1px solid var(--primary);\n" +
        "             transition: all 0.3s ease-out;" +
        "        }\n" +
        "        .cta-button:hover {\n" +
        "                transition: all 0.3s ease-out;\n" +
        "                background: #000000;\n" +
        "                color: white;\n" +
        "                transition: 250ms;\n" +
        "        }\n" +
        "    </style>\n" +
        "</head>\n" +
        "<body>\n" +
        "    <div class=\"container\">\n" +
        "        <div class=\"header\">\n" +
        "            <h1>You are now a subscriber!</h1>\n" +
        "        </div>\n" +
        "        <div class=\"content\">\n" +
        "            <p>Congratulations!</p>\n" +
        "            <p>You've just subscribed to the Armenian Speech Recognition platform. Get ready for an exciting journey!</p>\n" +
        "            <p>Stay tuned for groundbreaking updates and cutting-edge innovations that will redefine how you interact with technology!</p>\n" +
        "            <a class=\"cta-button\" href=\"http://armenianspeechrecognition.s3-website-us-east-1.amazonaws.com\">Let's get started!</a>\n" +
        "        </div>\n" +
        "    </div>\n" +
        "</body>\n" +
        "</html>\n";
}


