package ma.emsi.nahhal.tp2texte;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.bgesmallenv15.BgeSmallEnV15EmbeddingModel;
import dev.langchain4j.model.google.genai.GoogleGenAiEmbeddingModel;
import dev.langchain4j.store.embedding.CosineSimilarity;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Test4Embeddings {

    public static void main(String[] args) {
        String[][] couples = {
                {"Le train arrive à la gare.", "Le train entre en gare."},
                {"Le magasin est ouvert.", "Le magasin est fermé."},
                {"Je cherche un stage en informatique.",
                        "Je voudrais trouver un stage dans le domaine informatique."},
                {"Un chat dort sur le canapé.", "La Terre tourne autour du Soleil."},
                {"Le colis est arrivé hier.", "Le paquet a été livré la veille."},
                {"Il pleut à Casablanca.", "La recette demande trois œufs."}
        };

        comparer("BGE local", new BgeSmallEnV15EmbeddingModel(), couples);

        String apiKey = System.getenv("GEMINI_KEY");
        if (apiKey == null || apiKey.isBlank()) {
            System.err.println("GEMINI_KEY introuvable : seul le test local a été exécuté.");
            return;
        }

        EmbeddingModel gemini = GoogleGenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-embedding-001")
                .taskType(GoogleGenAiEmbeddingModel.TaskTypeEnum.SEMANTIC_SIMILARITY)
                .timeout(Duration.ofSeconds(10))
                .build();

        comparer("Gemini", gemini, couples);
    }

    private static void comparer(String nom, EmbeddingModel modele, String[][] couples) {
        List<TextSegment> phrases = new ArrayList<>();

        for (String[] couple : couples) {
            phrases.add(TextSegment.from(couple[0]));
            phrases.add(TextSegment.from(couple[1]));
        }

        List<Embedding> vecteurs = modele.embedAll(phrases).content();

        System.out.println("\n=== " + nom + " ===");
        for (int i = 0; i < couples.length; i++) {
            double score = CosineSimilarity.between(
                    vecteurs.get(2 * i), vecteurs.get(2 * i + 1)
            );

            System.out.printf(Locale.FRANCE, "%d. %.4f | %s / %s%n",
                    i + 1, score, couples[i][0], couples[i][1]);
        }
    }
}