package org.example.problem8;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Josephus {

    public static List<Integer> getJosephusPermutation(int N, int M) {
        List<Integer> people = new ArrayList<>();  // 1부터 N까지의 사람 리스트
        List<Integer> result = new ArrayList<>();  // 제거된 사람들의 순서를 저장할 리스트

        // 1번부터 N번까지의 사람들을 리스트에 추가
        for (int i = 1; i <= N; i++) {
            people.add(i);
        }

        int index = 0;  // 제거할 사람의 인덱스

        // 리스트에 남은 사람이 없을 때까지 반복
        while (!people.isEmpty()) {
            index = (index + M - 1) % people.size();  // M번째 사람의 인덱스 계산
            result.add(people.remove(index));  // 해당 인덱스의 사람을 제거하고 결과에 추가
        }

        return result;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // N과 M 입력받기
        System.out.print("Enter N (number of people): ");
        int N = scanner.nextInt();
        System.out.print("Enter M (step count): ");
        int M = scanner.nextInt();

        // 조세퍼스 순열 구하기
        List<Integer> result = getJosephusPermutation(N, M);

        // 결과 출력
        System.out.print("<");
        for (int i = 0; i < result.size(); i++) {
            if (i != 0) {
                System.out.print(", ");
            }
            System.out.print(result.get(i));
        }
        System.out.println(">");

        scanner.close();
    }
}
