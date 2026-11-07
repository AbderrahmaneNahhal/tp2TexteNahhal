package ma.emsi.nahhal.tp2texte;

import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiTokenUsage;
import dev.langchain4j.model.output.TokenUsage;

import java.util.Locale;

public class Test2Couts {

    public static void main(String[] args) {
        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("La variable GEMINI_KEY est introuvable.");
            return;
        }

        ChatModel model = GoogleGenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-3.5-flash-lite")
                .temperature(0.7)
                .build();

        String question = "Quelle est la capitale du Maroc ?";
        ChatResponse response = model.chat(UserMessage.from(question));

        System.out.println("Question : " + question);
        System.out.println("Réponse : " + response.aiMessage().text());

        TokenUsage usage = response.tokenUsage();
        if (usage == null || usage.inputTokenCount() == null
                || usage.outputTokenCount() == null) {
            System.err.println("Gemini n'a pas fourni les compteurs de tokens.");
            return;
        }

        int entree = usage.inputTokenCount();
        int sortie = usage.outputTokenCount();
        int raisonnement = 0;

        if (usage instanceof GoogleGenAiTokenUsage geminiUsage
                && geminiUsage.thoughtsTokenCount() != null) {
            raisonnement = geminiUsage.thoughtsTokenCount();
        }

        // Tarif standard payant, en USD par million de tokens.
        double coutUsd = (entree * 0.30 + (sortie + raisonnement) * 2.50)
                / 1_000_000.0;

        System.out.println("Tokens d'entrée : " + entree);
        System.out.println("Tokens de sortie (réponse) : " + sortie);
        System.out.println("Tokens de raisonnement : " + raisonnement);
        System.out.printf(Locale.US, "Coût estimé : %.8f USD%n", coutUsd);

        if (coutUsd > 0) {
            long requetes = (long) Math.ceil(1.0 / coutUsd);
            System.out.println("Requêtes similaires pour atteindre 1 USD : " + requetes);
        }
    }
}