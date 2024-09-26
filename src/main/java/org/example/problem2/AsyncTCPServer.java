package org.example.problem2;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class AsyncTCPServer {

    // 요청 번호를 관리하는 변수
    private static AtomicInteger requestNumber = new AtomicInteger(0);

    public static void main(String[] args) {
        try {
            // 서버 소켓 채널을 열고 IP와 포트에 바인딩합니다.
            AsynchronousServerSocketChannel serverChannel = AsynchronousServerSocketChannel.open();
            serverChannel.bind(new InetSocketAddress("127.0.0.1", 8888));
            System.out.println("Server is listening on port 8888...");

            // 클라이언트 연결을 비동기적으로 기다립니다.
            serverChannel.accept(null, new CompletionHandler<AsynchronousSocketChannel, Void>() {
                @Override
                public void completed(AsynchronousSocketChannel clientChannel, Void att) {

                    try {
                        // 클라이언트의 접속 정보를 출력
                        InetSocketAddress remoteAddress = (InetSocketAddress) clientChannel.getRemoteAddress();
                        System.out.println("Connected by (" + remoteAddress.getAddress().getHostAddress() + ", " + remoteAddress.getPort() + ")");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    // 다른 연결도 계속 받아들일 수 있도록 다시 accept 호출
                    serverChannel.accept(null, this);

                    // 클라이언트 연결을 처리하는 메서드 호출
                    handleClient(clientChannel);
                }

                @Override
                public void failed(Throwable exc, Void att) {
                    exc.printStackTrace();
                }
            });

            // 서버가 종료되지 않도록 대기합니다.
            System.in.read();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void handleClient(AsynchronousSocketChannel clientChannel) {
        ByteBuffer buffer = ByteBuffer.allocate(1024);
        int currentRequestNumber = requestNumber.incrementAndGet();  // 요청 번호 증가

        // 클라이언트로부터 메시지를 읽습니다.
        clientChannel.read(buffer, buffer, new CompletionHandler<Integer, ByteBuffer>() {
            @Override
            public void completed(Integer result, ByteBuffer attachment) {
                attachment.flip();
                String message = StandardCharsets.UTF_8.decode(attachment).toString().trim();
                System.out.println("Received(" + currentRequestNumber + "): " + message);

                String response = message.equalsIgnoreCase("Ping") ? "Pong (" + currentRequestNumber + ")" : message + " (" + currentRequestNumber + ")";

                // 3초 동안 응답을 지연시킵니다.
                try {
                    TimeUnit.SECONDS.sleep(3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                ByteBuffer responseBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));

                // 클라이언트로 응답을 비동기적으로 보냅니다.
                clientChannel.write(responseBuffer, responseBuffer, new CompletionHandler<Integer, ByteBuffer>() {
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        System.out.println("Send: " + response);
                        buffer.clear();

                        // 클라이언트로부터 다시 읽기 작업을 수행하여 추가 메시지 처리
                        handleClient(clientChannel);
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer buffer) {
                        exc.printStackTrace();
                    }
                });
            }

            @Override
            public void failed(Throwable exc, ByteBuffer attachment) {
                exc.printStackTrace();
            }
        });
    }
}
