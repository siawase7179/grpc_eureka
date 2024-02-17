# gRPC

- Remote Procedure Call의 약자로 RPC를 이용해 구글에서 만든 프로토콜
- gRPC를 이용하면 원격에 있는 애플리케이션의 메서드를 로컬 메서드인 것처럼 직접 호출할 수 있기 때문에 분산 애플리케이션과 서비스를 보다 쉽게 만들 수 있다.

### **장점**

1. 높은 생산성과 다양한 언어 및 플랫폼 지원. gRPC는 서비스와 메시지를 정의하기 위해 Protocol Buffers를 사용한다.
2. HTTP 2.0 프로토콜로 하나의 TCP 연결을 통해 여러 데이터 요청을 병렬처리 가능(multiplexing)
3. gRPC는 HTTP/2 레이어 위에서 Protocol Buffers를 사용해 직렬화된 바이트 스트림으로 통신하여 JSON 기반의 통신보다 더 가볍고 통신 속도가 빠르다.

### **Stub**

- Parameter 객체를 Message로 Marshalling/Unmarshalling하는 레이어
- client의 stub은 함수 호출에 사용된 파라미터의 변환(marshalling) 및 함수 실행 후 서버에서 전달된 결과의 변환 담당
- server의 stub은 클라이언트가 전달한 매개 변수의 역변환(unmarshalling) 및 함수 실행 결과 변환을 담당
- 
---

## HTTP/2

- **Multiplexed Streams**
    - Connection 한 개로 동시에 여러 개의 메시지를 주고 받을 수 있으며 응답은 순서에 상관없이 Stream으로 주고 받습니다.
    - HTTP/1.1의 Connection Keep-Alive, Pipelining의 개선
- **Header Compression**
    - 헤더 프레임을 압축해서 전송
- **Binary protocol**
    - 네트워크 지연시간을 줄이고 처리량 개선

---

## plugin 추가
``` groovy
plugins {
    ...
    id 'com.google.protobuf' version '0.8.18'
    ...
}
...(중략)
protobuf {
    generatedFilesBaseDir = "$projectDir/src/genereate"

    clean{
        delete generatedFilesBaseDir
    }

    protoc {
        artifact = 'com.google.protobuf:protoc:3.19.2'
    }

    plugins {
        grpc {
            artifact = 'io.grpc:protoc-gen-grpc-java:1.42.2'
        }
    }

    generateProtoTasks {
        all()*.plugins {
            grpc {}
        }
    }
}
```
gradle build시 자동으로 proto을 컴파일해준다.

## 의존성 주입
``` groovy
compileOnly 'io.grpc:grpc-protobuf:1.42.2'
compileOnly 'io.grpc:grpc-stub:1.42.2'

// https://mvnrepository.com/artifact/com.google.protobuf/protobuf-java-util
implementation group: 'com.google.protobuf', name: 'protobuf-java-util', version: '3.21.5'
```

## GrpcConfig.java
```java
@Configuration
@ImportAutoConfiguration({

net.devh.boot.grpc.common.autoconfigure.GrpcCommonCodecAutoConfiguration.class,
net.devh.boot.grpc.common.autoconfigure.GrpcCommonTraceAutoConfiguration.class,

net.devh.boot.grpc.server.autoconfigure.GrpcAdviceAutoConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcMetadataConsulConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcMetadataEurekaConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcMetadataNacosConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcMetadataZookeeperConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcServerAutoConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcServerFactoryAutoConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcServerSecurityAutoConfiguration.class,
net.devh.boot.grpc.server.autoconfigure.GrpcServerTraceAutoConfiguration.class
})

public class GrpcConfig {
    
}
```
> [!NOTE]
> spring boot 3.x로 버전을 올리면서
> 위 설정파일을 등록 해줘야 된다.
