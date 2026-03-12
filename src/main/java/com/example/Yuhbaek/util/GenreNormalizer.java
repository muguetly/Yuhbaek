package com.example.Yuhbaek.util;

import java.util.Set;

public final class GenreNormalizer {

    private GenreNormalizer() {}

    private static final Set<String> STANDARD_GENRES = Set.of(
            "소설", "에세이", "시", "자기계발", "인문",
            "경제경영", "과학", "역사", "사회", "예술",
            "IT", "아동", "청소년", "여행", "건강", "기타"
    );

    public static String normalizeFromAladin(String categoryName) {
        if (categoryName == null || categoryName.isBlank()) return "기타";

        String c = categoryName.replace(" ", "").toLowerCase();

        if (c.contains("에세이")) return "에세이";

        if (c.contains("시집")) return "시";
        if (c.contains("소설/시/희곡") && c.contains("시")) return "시";

        if (c.contains("소설")) return "소설";

        if (c.contains("자기계발") || c.contains("성공") || c.contains("처세") || c.contains("리더십")) {
            return "자기계발";
        }

        if (c.contains("인문학") || c.contains("철학") || c.contains("심리학") || c.contains("심리")) {
            return "인문";
        }

        if (c.contains("경제경영") || c.contains("재테크") || c.contains("투자") || c.contains("마케팅")) {
            return "경제경영";
        }

        if (c.contains("과학")) return "과학";
        if (c.contains("역사")) return "역사";

        if (c.contains("사회과학") || c.contains("정치") || c.contains("사회문제")) {
            return "사회";
        }

        if (c.contains("예술") || c.contains("대중문화") || c.contains("디자인") || c.contains("음악")) {
            return "예술";
        }

        if (c.contains("컴퓨터") || c.contains("모바일") || c.contains("프로그래밍") || c.contains("it")) {
            return "IT";
        }

        if (c.contains("어린이") || c.contains("유아")) return "아동";
        if (c.contains("청소년")) return "청소년";
        if (c.contains("여행")) return "여행";
        if (c.contains("건강") || c.contains("의학")) return "건강";

        return "기타";
    }

    public static String normalizeFromClient(String genre) {
        if (genre == null || genre.isBlank()) return "기타";

        String g = genre.trim();
        if (STANDARD_GENRES.contains(g)) {
            return g;
        }
        return "기타";
    }
}