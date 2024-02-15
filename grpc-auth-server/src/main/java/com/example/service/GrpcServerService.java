package com.example.service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import io.grpc.Deadline;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import net.devh.boot.grpc.server.service.GrpcService;
import net.grpc.lib.TokenReply;
import net.grpc.lib.TokenRequest;
import net.grpc.lib.TokenGrpc.TokenImplBase;

import io.grpc.Context;

@GrpcService
public class GrpcServerService extends TokenImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerService.class);
    private byte[] keyBytes = "ThisisgrpcTokenSecretKey!!!!!!!!".getBytes();
    
    private long expiryTime = 3600;

    @Override
    public void requestToken(TokenRequest request, io.grpc.stub.StreamObserver<TokenReply> responseObserver) {
        try {
            LOGGER.info("(req_token) {}", toJson(request.toBuilder()));

            TokenReply.Builder builder = TokenReply.newBuilder().setAccessToken(makeToken(request.getId())).setExpiresIn(expiryTime);

            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

            LOGGER.info("(res_token) {}", toJson(builder));

        } catch (IOException e) {
            // TODO Auto-generated catch block
            LOGGER.error("error", e);
        }
        
    }

    public static boolean isNotDeadlineExpired(Context currentContext) {

        if (currentContext == null) {
            return false;
        }

        final Deadline deadline = currentContext.getDeadline();

        if (deadline == null) {
            return false;
        }
        return deadline.isExpired();
    }

    public void sendA999UnkownError(TokenRequest request, io.grpc.stub.StreamObserver<TokenReply> responseObserver){        
        if (!isNotDeadlineExpired(Context.current())) {
            // A999 Unknown error
            LOGGER.info("isNotDeadlineExpired");
            TokenReply.Builder builder = TokenReply.newBuilder().setAccessToken(makeToken(request.getId())).setExpiresIn(expiryTime);
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } else {

            Deadline deadline = Context.current().getDeadline();
            LOGGER.info("GRPCDeadlineExpired [deadline: '{}']", deadline);
        }
    }

	public synchronized String  makeToken(String id){
        Key secretKey = new SecretKeySpec(keyBytes, io.jsonwebtoken.SignatureAlgorithm.HS256.getJcaName());
		Date expiration = new Date(System.currentTimeMillis() + 3600000);

        return Jwts.builder()
                .claim("id", id)
                .setExpiration(expiration)
                .signWith(secretKey)
                .compact();
	}
    
    public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
        return JsonFormat.printer().print(messageOrBuilder);
    }
}
