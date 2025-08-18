import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordlistGenerator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter details for wordlist generation (leave blank to skip):");

        System.out.print("First Name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine().trim();
        System.out.print("Birthday (e.g., 19900101): ");
        String birthday = scanner.nextLine().trim();
        System.out.print("Home Address: ");
        String homeAddress = scanner.nextLine().trim();
        List<String> inputs = new ArrayList<>();
        if (!firstName.isEmpty()) inputs.add(firstName);
        if (!lastName.isEmpty()) inputs.add(lastName);
        if (!birthday.isEmpty()) inputs.add(birthday);
        if (!homeAddress.isEmpty()) inputs.add(homeAddress);

        // Loop for multiple 'Other Info' entries
        List<String> otherInfos = new ArrayList<>();
        System.out.println("Enter Other Info about the target (one per line, blank line to finish):");
        while (true) {
            String info = scanner.nextLine().trim();
            if (info.isEmpty()) break;
            otherInfos.add(info);
        }
        inputs.addAll(otherInfos);

        List<String> wordlist = generateCombinations(inputs);
        wordlist.addAll(applyPatterns(wordlist));
        wordlist.addAll(applySubstitutions(wordlist));
        wordlist.addAll(getCommonPasswords());

        // Remove duplicates
        List<String> finalWordlist = new ArrayList<>();
        for (String w : wordlist) {
            if (!finalWordlist.contains(w)) finalWordlist.add(w);
        }

        String filename = "wordlist.txt";
        try (FileWriter writer = new FileWriter(filename)) {
            for (String word : finalWordlist) {
                writer.write(word + "\n");
            }
            System.out.println("Wordlist generated: " + filename);
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }

    // Generate all combinations (single, pairs, triples, etc.)
    private static List<String> generateCombinations(List<String> inputs) {
        List<String> result = new ArrayList<>();
        int n = inputs.size();
        // Single items
        result.addAll(inputs);
        // Combinations
        for (int i = 2; i <= n; i++) {
            combine(inputs, new ArrayList<>(), 0, i, result);
        }
        return result;
    }

    private static void combine(List<String> arr, List<String> temp, int start, int k, List<String> result) {
        if (temp.size() == k) {
            result.add(String.join("", temp));
            return;
        }
        for (int i = start; i < arr.size(); i++) {
            temp.add(arr.get(i));
            combine(arr, temp, i + 1, k, result);
            temp.remove(temp.size() - 1);
        }
    }

    // Add common patterns (e.g., append/prepend years, numbers, special chars, infix)
    private static List<String> applyPatterns(List<String> base) {
        String[] years = {"2025", "2024", "2023", "2022", "2021", "2020", "2010", "2000", "1990", "1980"};
        String[] nums = {"1", "12", "123", "1234", "12345", "007", "111", "999", "000", "888", "555", "777", "987654", "654321"};
        String[] specials = {"!", "@", "#", "$", "%", "*", "?", "!!", "@@", "##", "$!", "!?", "!!@", "@!", "#?", "**", "$$", "!!1", "!@#"};
        List<String> result = new ArrayList<>();
        for (String word : base) {
            // Suffix
            for (String year : years) result.add(word + year);
            for (String num : nums) result.add(word + num);
            for (String sp : specials) result.add(word + sp);
            for (String year : years) for (String sp : specials) result.add(word + year + sp);
            for (String num : nums) for (String sp : specials) result.add(word + num + sp);
            // Prefix
            for (String year : years) result.add(year + word);
            for (String num : nums) result.add(num + word);
            for (String sp : specials) result.add(sp + word);
            for (String year : years) for (String sp : specials) result.add(sp + year + word);
            for (String num : nums) for (String sp : specials) result.add(sp + num + word);
            // Infix (split word in half)
            int mid = word.length() / 2;
            String first = word.substring(0, mid);
            String second = word.substring(mid);
            for (String sp : specials) result.add(first + sp + second);
            for (String num : nums) result.add(first + num + second);
        }
        return result;
    }

    // Add common substitutions (e.g., a->@, s->$, o->0, i->1, e->3)
    private static List<String> applySubstitutions(List<String> base) {
        List<String> result = new ArrayList<>();
        for (String word : base) {
            String w = word.replace('a', '@').replace('A', '@')
                          .replace('s', '$').replace('S', '$')
                          .replace('o', '0').replace('O', '0')
                          .replace('i', '1').replace('I', '1')
                          .replace('e', '3').replace('E', '3');
            if (!w.equals(word)) result.add(w);
        }
        return result;
    }

    // Add popular weak passwords
    private static List<String> getCommonPasswords() {
        String[] common = {
            "password", "123456", "12345678", "qwerty", "abc123", "letmein", "monkey", "dragon", "111111", "baseball",
            "iloveyou", "trustno1", "sunshine", "master", "welcome", "shadow", "ashley", "football", "jesus", "ninja",
            "mustang", "password1", "admin", "login", "passw0rd", "starwars", "hello", "freedom", "whatever", "qazwsx"
        };
        List<String> result = new ArrayList<>();
        for (String w : common) result.add(w);
        return result;
    }
}
