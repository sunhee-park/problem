package org.example.problem2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class AsyncTCPClient {

    // 클라이언트가 중복 읽기를 방지하기 위한 플래그
    private static AtomicBoolean readPending = new AtomicBoolean(false);

    public static void main(String[] args) {
        try {
            // 비동기 소켓 채널을 열고 서버와 연결
            AsynchronousSocketChannel clientChannel = AsynchronousSocketChannel.open();
            clientChannel.connect(new InetSocketAddress("127.0.0.1", 8888)).get();

            // 입력 스레드를 시작
            Thread inputThread = new Thread(() -> handleUserInput(clientChannel));
            inputThread.start();

            // 출력 스레드를 시작
            Thread outputThread = new Thread(() -> handleServerResponse(clientChannel));
            outputThread.start();

            // 두 스레드가 종료될 때까지 대기
            inputThread.join();
            outputThread.join();

            // 클라이언트 채널을 닫습니다.
            clientChannel.close();

        } catch (IOException | InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }

    // 사용자 입력을 처리하는 메서드 (별도 스레드)
    private static void handleUserInput(AsynchronousSocketChannel clientChannel) {
        Scanner scanner = new Scanner(System.in);
        int messageId = 1;

        while (true) {
            System.out.print("Send(" + messageId + "): ");
            String message = scanner.nextLine();

            // 'exit'을 입력하면 클라이언트를 종료
            if (message.equalsIgnoreCase("exit")) {
                break;
            }

            // 서버로 메시지를 전송
            sendMessage(clientChannel, message, messageId);
            messageId++;
        }

        scanner.close();
    }

    // 서버로부터 응답을 계속해서 읽고 출력하는 메서드 (별도 스레드)
    private static void handleServerResponse(AsynchronousSocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        while (true) {
            if (readPending.compareAndSet(false, true)) {  // 중복 읽기를 방지
                clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        buffer.flip();
                        String response = StandardCharsets.UTF_8.decode(buffer).toString().trim();
                        System.out.println("Received: " + response);
                        buffer.clear();
                        readPending.set(false);  // 읽기가 완료되면 플래그를 해제
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer buffer) {
                        exc.printStackTrace();
                        readPending.set(false);  // 실패 시에도 플래그를 해제
                    }
                });
            }

            try {
                Thread.sleep(100);  // 서버 응답을 기다리는 동안 잠시 대기
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    // 서버로 메시지를 전송하는 메서드
    private static void sendMessage(AsynchronousSocketChannel clientChannel, String message, int id) {
        ByteBuffer buffer = ByteBuffer.wrap(message.getBytes(StandardCharsets.UTF_8));
        clientChannel.write(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer buffer) {
                buffer.clear();
            }

            @Override
            public void failed(Throwable exc, ByteBuffer buffer) {
                exc.printStackTrace();
            }
        });
    }
}
