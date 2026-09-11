package com.codelens.codelens_backend.review;

import com.codelens.codelens_backend.model.FindingSeverity;
import com.codelens.codelens_backend.model.FindingType;
import com.codelens.codelens_backend.model.Review;
import com.codelens.codelens_backend.model.ReviewFinding;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class LocalCodeAnalyzer {

    public List<ReviewFinding> analyze(Review review) {

        List<ReviewFinding> findings = new ArrayList<>();

        String sourceCode = review.getSourceCode();

        detectSqlInjection(sourceCode, review, findings);
        detectDatabaseCallInsideLoop(sourceCode, review, findings);
        detectSystemOut(sourceCode, review, findings);
        detectSingleCharacterVariables(sourceCode, review, findings);

        return findings;
    }

    private void detectSqlInjection(
            String code,
            Review review,
            List<ReviewFinding> findings
    ) {

        String[] lines = code.split("\\R");

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i];

            if ((line.contains("SELECT")
                    || line.contains("select")
                    || line.contains("INSERT")
                    || line.contains("UPDATE")
                    || line.contains("DELETE"))
                    && line.contains("+")) {

                findings.add(
                        createFinding(
                                review,
                                FindingType.SECURITY,
                                FindingSeverity.HIGH,
                                "SQL Injection Risk",
                                "SQL query appears to be constructed using string concatenation.",
                                "Use parameterized queries or prepared statements.",
                                i + 1
                        )
                );
            }
        }
    }

    private void detectDatabaseCallInsideLoop(
            String code,
            Review review,
            List<ReviewFinding> findings
    ) {

        String[] lines = code.split("\\R");

        boolean insideLoop = false;
        int loopLine = -1;

        for (int i = 0; i < lines.length; i++) {

            String line = lines[i].trim();

            if (line.startsWith("for ")
                    || line.startsWith("for(")
                    || line.startsWith("while ")
                    || line.startsWith("while(")) {

                insideLoop = true;
                loopLine = i + 1;
            }

            if (insideLoop &&
                    (line.contains("repository.")
                            || line.contains("Repository.")
                            || line.contains(".findById(")
                            || line.contains(".findAll("))) {

                findings.add(
                        createFinding(
                                review,
                                FindingType.PERFORMANCE,
                                FindingSeverity.MEDIUM,
                                "Database Call Inside Loop",
                                "A database operation appears to execute inside a loop.",
                                "Load or cache the required data before the loop when possible.",
                                i + 1
                        )
                );

                insideLoop = false;
            }

            if (insideLoop && line.equals("}")) {
                insideLoop = false;
            }
        }
    }

    private void detectSystemOut(
            String code,
            Review review,
            List<ReviewFinding> findings
    ) {

        String[] lines = code.split("\\R");

        for (int i = 0; i < lines.length; i++) {

            if (lines[i].contains("System.out.println")) {

                findings.add(
                        createFinding(
                                review,
                                FindingType.MAINTAINABILITY,
                                FindingSeverity.LOW,
                                "System.out.println Detected",
                                "Direct console output is present in application code.",
                                "Use a proper logging framework such as SLF4J.",
                                i + 1
                        )
                );
            }
        }
    }

    private void detectSingleCharacterVariables(
            String code,
            Review review,
            List<ReviewFinding> findings
    ) {

        Pattern pattern = Pattern.compile(
                "\\b(?:int|long|double|float|String|boolean)\\s+([a-zA-Z])\\s*[=;]"
        );

        Matcher matcher = pattern.matcher(code);

        while (matcher.find()) {

            String variable = matcher.group(1);

            int lineNumber =
                    code.substring(0, matcher.start())
                            .split("\\R")
                            .length;

            findings.add(
                    createFinding(
                            review,
                            FindingType.FORMATTING,
                            FindingSeverity.LOW,
                            "Single-Character Variable Name",
                            "Single-character variable names can reduce code readability.",
                            "Use descriptive variable names that communicate intent.",
                            lineNumber
                    )
            );
        }
    }

    private ReviewFinding createFinding(
            Review review,
            FindingType type,
            FindingSeverity severity,
            String title,
            String description,
            String recommendation,
            int lineNumber
    ) {

        return ReviewFinding.builder()
                .id(UUID.randomUUID().toString())
                .reviewId(review.getId())
                .type(type)
                .severity(severity)
                .title(title)
                .description(description)
                .recommendation(recommendation)
                .lineNumber(lineNumber)
                .build();
    }
}