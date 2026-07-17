package cream.cli;

import java.util.ArrayList;
import java.util.List;

public class PathAnimationSteps {

    public static List<String> computeSteps(String from, String to) {
        String[] a = from.split("\\\\");
        String[] b = to.split("\\\\");

        int common = 0;
        while (common < a.length && common < b.length && a[common].equals(b[common])) {
            common++;
        }

        List<String> steps = new ArrayList<>();

        // Schritte zurück
        for (int i = common; i < a.length; i++) {
            steps.add("..");
        }

        // Schritte vorwärts
        for (int i = common; i < b.length; i++) {
            steps.add(b[i]);
        }

        return steps;
    }

    public static void main(String[] args) {
        String from = "C:\\Users\\andre\\Documents\\2026-06-14-Work-FastJava\\FastTUI\\examples\\Demo";
        String to = "C:\\Users\\andre\\Documents\\2026-07-16-Work-CreamCLI\\examples\\Demo";

        List<String> steps = computeSteps(from, to);
        steps.forEach(System.out::println);
    }
}
