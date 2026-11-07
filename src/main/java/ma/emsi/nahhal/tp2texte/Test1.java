package ma.emsi.nahhal.tp2texte;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;

public class Test1 {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("La variable GEMINI_KEY est introuvable.");
            return;
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash")
                .temperature(0.7)
                .build();

        poserQuestion(model, "Quelle est la capitale du Maroc ?");
        poserQuestion(model, "Quelle heure est-il ?");
        poserQuestion(model, "Bonjour, je m'appelle Abderrahmane Nahhal.");
        poserQuestion(model, "Quel est mon nom ?");
    }

    private static void poserQuestion(ChatModel model, String question) {
        System.out.println("Question : " + question);
        System.out.println("Réponse : " + model.chat(question));
        System.out.println();
    }
}
