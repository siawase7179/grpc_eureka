package com.example.service;

import java.io.IOException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.util.JsonFormat;

import io.grpc.Deadline;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;
import net.devh.boot.grpc.server.service.GrpcService;
import net.grpc.lib.TokenReply;
import net.grpc.lib.TokenRequest;
import net.grpc.lib.TokenGrpc.TokenImplBase;

import io.grpc.Context;

@GrpcService
public class GrpcServerService extends TokenImplBase {
    private static final Logger LOGGER = LoggerFactory.getLogger(GrpcServerService.class);
    private String secretKey = "ThisisGrpcAcessToken";
    private long expiryTime = 3600;

    @Override
    public void requestToken(TokenRequest request, io.grpc.stub.StreamObserver<TokenReply> responseObserver) {
        try {
            LOGGER.info("(req_token) {}", toJson(request.toBuilder()));

            // sendA999UnkownError(request, responseObserver);
            TokenReply.Builder builder = TokenReply.newBuilder().setAccessToken(makeToken(request.getId(), request.getPassword())).setExpiresIn(expiryTime);

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
            TokenReply.Builder builder = TokenReply.newBuilder().setAccessToken(makeToken(request.getId(), request.getPassword())).setExpiresIn(expiryTime);
            responseObserver.onNext(builder.build());
            responseObserver.onCompleted();

        } else {

            Deadline deadline = Context.current().getDeadline();
            LOGGER.info("GRPCDeadlineExpired [deadline: '{}']", deadline);
        }
    }

	public synchronized String  makeToken(String id, String password){
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        Date expireDate = new Date();        
        long expireIn = expireDate.getTime() + 1000 * expiryTime;
        expireDate.setTime(expireIn);
        
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        
        Map<String, Object> headerMap = new HashMap<String, Object>();

        headerMap.put("typ","JWT");
        headerMap.put("alg","HS256");

        Map<String, Object> map= new HashMap<String, Object>();
        map.put("id", id);
        map.put("password", password);

        JwtBuilder builder = Jwts.builder().setHeader(headerMap)
                .setClaims(map)
                .setExpiration(expireDate)
                .signWith(signatureAlgorithm, signingKey);
        return builder.compact();
	}
    
    public static String toJson(MessageOrBuilder messageOrBuilder) throws IOException {
        return JsonFormat.printer().print(messageOrBuilder);
    }
}
