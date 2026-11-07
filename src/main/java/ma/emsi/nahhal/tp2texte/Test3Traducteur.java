package ma.emsi.nahhal.tp2texte;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.google.genai.GoogleGenAiChatModel;
import dev.langchain4j.model.input.Prompt;
import dev.langchain4j.model.input.PromptTemplate;

import java.util.Map;

public class Test3Traducteur {

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

        String texte = "Je prépare un entretien de stage.";

        // Version 1 : langue fixe, seul le texte change.
        PromptTemplate versAnglais = PromptTemplate.from(
                "Traduis le texte suivant en anglais. Donne uniquement la traduction : {{it}}"
        );
        Prompt promptAnglais = versAnglais.apply(texte);
        traduire(model, promptAnglais);

        // Version 2 : la langue et le texte peuvent changer.
        PromptTemplate versLangue = PromptTemplate.from(
                "Traduis le texte suivant en {{langue}}. Donne uniquement la traduction : {{texte}}"
        );
        Prompt promptAllemand = versLangue.apply(Map.of(
                "langue", "allemand",
                "texte", texte
        ));
        traduire(model, promptAllemand);
    }

    private static void traduire(ChatModel model, Prompt prompt) {
        System.out.println("Prompt envoyé : " + prompt.text());
        System.out.println("Traduction : " + model.chat(prompt.text()));
        System.out.println();
    }
}