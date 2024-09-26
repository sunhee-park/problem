package org.example.problem5;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RandomResponseCounter {

    // 서버에서 랜덤한 값을 받는 메서드
    public static String getRandomResponse() throws Exception {
        String url = "http://codingtest.brique.kr:8080/random";
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");

        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String response = in.readLine();
        in.close();

        return response;
    }

    public static void main(String[] args) {
        Map<String, Integer> responseCount = new HashMap<>();  // 응답 값을 저장할 맵
        int totalCalls = 100;

        try {
            // 서버를 100번 호출
            for (int i = 0; i < totalCalls; i++) {
                String response = getRandomResponse();
                // 응답 값을 맵에 저장하고, 동일한 값이 나왔을 경우 카운트를 증가
                responseCount.put(response, responseCount.getOrDefault(response, 0) + 1);
            }

            // 응답 값의 횟수를 내림차순으로 정렬
            List<Map.Entry<String, Integer>> sortedResponses = responseCount.entrySet()
                    .stream()
                    .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))  // 횟수 기준 내림차순 정렬
                    .collect(Collectors.toList());

            // 결과 출력
            int totalCount = 0;
            for (Map.Entry<String, Integer> entry : sortedResponses) {
                System.out.println("count:"+entry.getValue()+" "+entry.getKey());
                totalCount += entry.getValue();
            }

            // 총 호출 횟수 출력
            System.out.println("Total count: " + totalCount);

        } catch (Exception e) {
            System.out.println("오류 발생: " + e.getMessage());
        }
    }
}