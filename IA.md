\# Utilisation de l’IA — TP2 Texte



\## Test 6 — Dépendance Maven du modèle d’embeddings BGE



\*\*Problème rencontré :\*\* Le compilateur Java ne trouvait pas le package `dev.langchain4j.model.embedding.onnx.bgesmallenv15` utilisé dans `Test6Rag2`.



\*\*Raison de l’utilisation de l’IA :\*\* L’erreur persistait après mes modifications du `pom.xml`. J’ai demandé de l’aide pour identifier la dépendance correspondant à la classe importée.



\*\*Prompt utilisé, reformulé en français :\*\* « Pourquoi Java ne trouve-t-il toujours pas le package `dev.langchain4j.model.embedding.onnx.bgesmallenv15` ? Quelle dépendance Maven dois-je ajouter pour que `BgeSmallEnV15EmbeddingModel` soit disponible ? »



\*\*Réponse de l’IA — résumé :\*\* L’import correspond à la variante non quantifiée du modèle BGE. Il faut ajouter explicitement la dépendance `langchain4j-embeddings-bge-small-en-v15` dans le `pom.xml`, puis recharger le projet Maven dans IntelliJ.



\*\*Ce que j’ai appris :\*\* Un import Java ne télécharge aucune bibliothèque. Il faut que Maven fournisse la dépendance qui contient la classe exacte utilisée. Dans ce cas, distinguer la variante quantifiée (`-q`) de la variante non quantifiée m’a permis de comprendre pourquoi mon import ne fonctionnait pas.

