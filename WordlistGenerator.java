import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class WordlistGenerator {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        System.out.println("Enter details for wordlist generation (leave blank to skip):");

        System.out.print("First Name: ");
        String firstName = sc.nextLine().trim();
        System.out.print("Last Name: ");
        String lastName = sc.nextLine().trim();
        System.out.print("Birthday (YYYYMMDD or blank): ");
        String birthday = sc.nextLine().trim();
        String bYear = "", bMonth = "", bDay = "";
        if (birthday.length() == 8) {
            bYear = birthday.substring(0, 4);
            bMonth = birthday.substring(4, 6);
            bDay = birthday.substring(6, 8);
        }
        System.out.print("Home Address: ");
        String homeAddress = sc.nextLine().trim();
        List<String> inputs = new ArrayList<>();
        if (!firstName.isEmpty()) inputs.add(firstName);
        if (!lastName.isEmpty()) inputs.add(lastName);
        if (!birthday.isEmpty()) inputs.add(birthday);
        if (!homeAddress.isEmpty()) inputs.add(homeAddress);

        // Loop for multiple 'Other Info' entries
        List<String> otherInfos = new ArrayList<>();
        System.out.println("Enter Other Info about the target (one per line, blank line to finish):");
        while (true) {
            String info = sc.nextLine().trim();
            if (info.isEmpty()) break;
            otherInfos.add(info);
        }
        inputs.addAll(otherInfos);

    List<String> wordlist = generateCombinations(inputs);
    wordlist.addAll(applyPatterns(wordlist, bYear, bMonth, bDay));
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
        sc.close();
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

    // Add common patterns (append/prepend birthday parts, numbers, special chars, infix)
    private static List<String> applyPatterns(List<String> base, String bYear, String bMonth, String bDay) {
        String[] nums = {"1", "12", "123", "1234", "12345", "007", "111", "999", "000", "888", "555", "777", "987654", "654321"};
        String[] specials = {"!", "@", "#", "$", "%", "*", "?", "!!", "@@", "##", "$!", "!?", "!!@", "@!", "#?", "**", "$$", "!!1", "!@#"};
        List<String> result = new ArrayList<>();
        String[] bParts = {bYear, bMonth, bDay};
        for (String word : base) {
            // Suffix
            for (String part : bParts) if (!part.isEmpty()) result.add(word + part);
            for (String num : nums) result.add(word + num);
            for (String sp : specials) result.add(word + sp);
            for (String part : bParts) for (String sp : specials) if (!part.isEmpty()) result.add(word + part + sp);
            for (String num : nums) for (String sp : specials) result.add(word + num + sp);
            // Prefix
            for (String part : bParts) if (!part.isEmpty()) result.add(part + word);
            for (String num : nums) result.add(num + word);
            for (String sp : specials) result.add(sp + word);
            for (String part : bParts) for (String sp : specials) if (!part.isEmpty()) result.add(sp + part + word);
            for (String num : nums) for (String sp : specials) result.add(sp + num + word);
            // Infix (split word in half)
            int mid = word.length() / 2;
            String first = word.substring(0, mid);
            String second = word.substring(mid);
            for (String sp : specials) result.add(first + sp + second);
            for (String num : nums) result.add(first + num + second);
            for (String part : bParts) if (!part.isEmpty()) result.add(first + part + second);
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
